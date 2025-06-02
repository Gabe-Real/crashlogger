/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package org.quiltmc.community.modes.quilt.extensions.minecraft


import crashlogger.TEST_SERVER_ID
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Permission
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.entity.channel.NewsChannel
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.entity.channel.TopGuildMessageChannel
import dev.kord.rest.builder.channel.thread.StartThreadWithMessageBuilder
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.builder.message.embed
import dev.kordex.core.DISCORD_FUCHSIA
import dev.kordex.core.DISCORD_GREEN
import dev.kordex.core.checks.hasPermission
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.application.slash.ephemeralSubCommand
import dev.kordex.core.commands.converters.impl.optionalString
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.ephemeralSlashCommand
import dev.kordex.core.i18n.toKey
import dev.kordex.core.pagination.pages.Page
import dev.kordex.core.utils.scheduling.Scheduler
import dev.kordex.core.utils.scheduling.Task
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.datetime.Clock
import org.quiltmc.community.*
import kotlin.concurrent.thread



private const val PAGINATOR_TIMEOUT = 60_000L  // One minute
private const val CHUNK_SIZE = 10

private const val BASE_URL = "https://launchercontent.mojang.com"
private const val JSON_URL = "$BASE_URL/javaPatchNotes.json"

private const val CHECK_DELAY = 60L

private val LINK_REGEX = "<a href=\"(?<url>[^\"]+)\"[^>]*>(?<text>[^<]+)</a>".toRegex()

@Suppress("MagicNumber", "UnderscoresInNumericLiterals")
private val CHANNELS: List<Snowflake> = listOf(
	TEST_SERVER_ID,
	// YOUTUBE_GUILD,
)

class MinecraftExtension : Extension() {
	override val name: String = "minecraft"

	private val logger = KotlinLogging.logger { }

	private val client = HttpClient {
		install(ContentNegotiation) {
			json()
		}

		expectSuccess = true
	}

	private val scheduler = Scheduler()

	private var checkTask: Task? = null
	private var knownVersions: MutableSet<String> = mutableSetOf()
	private lateinit var currentEntries: PatchNoteEntries

	@OptIn(KordPreview::class)
	override suspend fun setup() {
		populateVersions()

		checkTask = scheduler.schedule(CHECK_DELAY, callback = ::checkTask)

		for (guildId in getGuilds()) {
			ephemeralSlashCommand {
				name = "mc".toKey()
				description = "Commands related to Minecraft updates".toKey()

				allowInDms = false

				guild(guildId)

				ephemeralSubCommand(::CheckArguments) {
					name = "get".toKey()
					description = "Retrieve the patch notes for a given Minecraft version, or the latest if not supplied".toKey()


					action {
						if (!::currentEntries.isInitialized) {
							respond { content = "Still setting up - try again a bit later!" }
							return@action
						}

						val patch = if (arguments.version == null) {
							currentEntries.entries.first()
						} else {
							currentEntries.entries.firstOrNull { it.version.equals(arguments.version, true) }
						}

						if (patch == null) {
							respond { content = "Unknown version supplied: `${arguments.version}`" }
							return@action
						}

						respond {
							embed {
								patchNotes(patch)
							}
						}
					}
				}

				ephemeralSubCommand {
					name = "versions".toKey()
					description = "Get a list of patch note versions.".toKey()

					action {
						if (!::currentEntries.isInitialized) {
							respond { content = "Still setting up - try again a bit later!" }

							return@action
						}

						editingPaginator {
							timeoutSeconds = PAGINATOR_TIMEOUT

							knownVersions.chunked(CHUNK_SIZE).forEach { chunk ->
								page(
									Page {
										title = "Patch note versions"
										color = DISCORD_FUCHSIA

										description = chunk.joinToString("\n") { "**»** `$it`" }

										footer {
											text = "${currentEntries.entries.size} versions"
										}
									}
								)
							}
						}.send()
					}
				}

				ephemeralSubCommand(::CheckArguments) {
					name = "forget".toKey()
					description = "Forget a version (the last one by default), allowing it to be relayed again.".toKey()

					check { hasPermission(Permission.ManageMessages )}

					action {
						if (!::currentEntries.isInitialized) {
							respond { content = "Still setting up - try again a bit later!" }
							return@action
						}

						val version = if (arguments.version == null) {
							currentEntries.entries.first().version
						} else {
							currentEntries.entries.firstOrNull {
								it.version.equals(arguments.version, true)
							}?.version
						}

						if (version == null) {
							respond { content = "Unknown version supplied: `${arguments.version}`" }
							return@action
						}

						knownVersions.remove(version)

						respond { content = "Version forgotten: `$version`" }
					}
				}

				ephemeralSubCommand {
					name = "run".toKey()
					description = "Run the check task now, without waiting for it.".toKey()

					check { hasPermission(Permission.ManageMessages) }

					action {
						respond { content = "Checking now..." }

						checkTask?.callNow()
					}
				}
			}
		}
	}

	private fun getGuilds(): Any {

	}

	suspend fun populateVersions() {
		currentEntries = client.get(JSON_URL).body()

		currentEntries.entries.forEach { knownVersions.add(it.version) }
	}

	@Suppress("TooGenericExceptionCaught")
	suspend fun checkTask() {
		try {
			val now = Clock.System.now()

			currentEntries = client.get(JSON_URL + "?cbt=${now.epochSeconds}").body()

			currentEntries.entries.forEach {
				if (it.version !in knownVersions) {
					relayUpdate(it)
					knownVersions.add(it.version)
				}
			}
		} catch (t: Throwable) {
			logger.error(t) { "Check task run failed" }
		} finally {
			checkTask = scheduler.schedule(CHECK_DELAY, callback = ::checkTask)
		}
	}

	@Suppress("TooGenericExceptionCaught")
	suspend fun relayUpdate(patchNote: PatchNote) =
		CHANNELS
			.map {
				try {
					kord.getChannelOf<TopGuildMessageChannel>(it)
				} catch (t: Throwable) {
					logger.warn(t) { "Unable to get channel of ID: ${it.value}" }

					null
				}
			}
			.filterNotNull()
			.forEach { it.relay(patchNote) }

	fun String.formatHTML(): String {
		var result = this

		result = result.replace("\u200B", "")
		result = result.replace("<p></p>", "")

		result = result.replace("<hr/?>".toRegex(), "\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_")
		result = result.replace("</hr>", "")

		result = result.replace("[\n]*</p>\n+<p>[\n]*".toRegex(), "\n\n")
		result = result.replace("[\n]*<[/]*p>[\n]*".toRegex(), "\n")

		result = result.replace("<strong>", "**")
		result = result.replace("</strong>", "**")

		result = result.replace("<code>", "`")
		result = result.replace("</code>", "`")

		result = result.replace("[\n]*<h\\d+>[\n]*".toRegex(), "\n\n__**")
		result = result.replace("[\n]*</h\\d+>[\n]*".toRegex(), "**__\n")

		result = result.replace("[\n]*<[ou]l>[\n]*".toRegex(), "\n\n")
		result = result.replace("[\n]*</[ou]l>[\n]*".toRegex(), "\n\n")

		result = result.replace("[\n]*</li>\n+<li>[\n]*".toRegex(), "\n**»** ")
		result = result.replace("([\n]{2,})?<li>[\n]*".toRegex(), "\n**»** ")
		result = result.replace("[\n]*</li>[\n]*".toRegex(), "\n\n")

		val links = LINK_REGEX.findAll(result)

		links.forEach {
			result = result.replace(
				it.value,
				"[${it.groups["text"]?.value}](${it.groups["url"]?.value})"
			)
		}

		return StringEscapeUtils.unescapeHtml4(result.trim('\n'))
	}

	fun String.truncateMarkdown(maxLength: Int = 1000): Pair<String, Int> {
		var result = this

		if (length > maxLength) {
			val truncated = result.substring(0, maxLength).substringBeforeLast("\n")
			val remaining = result.substringAfter(truncated).count { it == '\n' }

			result = truncated

			return result to remaining
		}

		return result to 0
	}

	private fun EmbedBuilder.patchNotes(patchNote: PatchNote, maxLength: Int = 1000) {
		val (truncated, remaining) = patchNote.body.formatHTML().truncateMarkdown(maxLength)

		title = patchNote.title
		color = DISCORD_GREEN

		description = "[Full patch notes](https://quiltmc.org/mc-patchnotes/#${patchNote.version})\n\n"
		description += truncated

		if (remaining > 0) {
			description += "\n\n[... $remaining more lines]"
		}

		thumbnail {
			url = "$BASE_URL${patchNote.image.url}"
		}

		footer {
			text = "URL: https://quiltmc.org/mc-patchnotes/#${patchNote.version}"
		}
	}

	private suspend fun TopGuildMessageChannel.relay(patchNote: PatchNote, maxLength: Int = 1000) {
		val message = createMessage {
			// If we are in the community guild, ping the update role
//			if (guildId == LADYSNAKE_GUILD) {
//				content = "<@&$MINECRAFT_UPDATE_PING_ROLE>"
//			}
			embed { patchNotes(patchNote, maxLength) }
		}

		val title = if (patchNote.title.startsWith("minecraft ", true)) {
			patchNote.title.split(" ", limit = 2).last()
		} else {
			patchNote.title
		}

		if (guildId == TEST_SERVER_ID) {
			val builder: StartThreadWithMessageBuilder.() -> Unit = {
				reason = "Minecraft update discussion thread"
			}
			when (this) {
				is TextChannel -> startPublicThreadWithMessage(message.id, title, builder)
				is NewsChannel -> {
					startPublicThreadWithMessage(message.id, title, builder)
					message.publish()
				}
			}
		}
	}

	@OptIn(KordPreview::class)
	class CheckArguments : Arguments() {
		val version by optionalString {
			name = "version".toKey()
			description = "Specific version to get patch notes for".toKey()
		}
	}
}
