/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package crashlogger

import com.unascribed.flexver.FlexVerComparator
import crashlogger.logparser.logs.config.DefaultLogParserConfig
import dev.kord.common.entity.Snowflake
import dev.kordex.core.ExtensibleBot
import dev.kordex.core.utils.env
import dev.kordex.modules.pluralkit.extPluralKit
import crashlogger.extensions.TestExtension
import crashlogger.logparser.logs.LogParserExtension
import crashlogger.logparser.logs.config.LogParserConfig
import crashlogger.logparser.logs.extLogParser
import crashlogger.logparser.logs.processors.PiracyProcessor
import crashlogger.logparser.logs.processors.ProblematicLauncherProcessor
import crashlogger.logparser.logs.processors.quilt.NonQuiltLoaderProcessor
import crashlogger.logparser.logs.processors.quilt.RuleBreakingModProcessor
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import dev.kordex.core.checks.guildFor
import dev.kordex.core.utils.loadModule
import org.nibor.autolink.LinkExtractor
import org.nibor.autolink.LinkType
import java.io.File
import java.net.URI
import java.net.URL

val TEST_SERVER_ID = Snowflake(
	env("TEST_SERVER").toLong()  // Get the test server ID from the env vars or a .env file
)

private val TOKEN = env("TOKEN")
// Get the bot token from the env vars or a .env file


suspend fun main() {
	val bot = ExtensibleBot(TOKEN) {
		chatCommands {
			defaultPrefix = "?"
			enabled = true

			prefix { default ->
				if (guildId == TEST_SERVER_ID) {
					// For the test server, we use ! as the command prefix
					"!"
				} else {
					// For other servers, we use the configured default prefix
					default
				}
			}
		}


		extensions {
			add(::TestExtension)
			add(::LogParserExtension)


			extPluralKit()

			 extLogParser{
				// Bundled non-default processors
				processor(PiracyProcessor())
				processor(ProblematicLauncherProcessor())

				// Quilt-specific processors
				processor(NonQuiltLoaderProcessor())
				processor(RuleBreakingModProcessor())

				globalPredicate { event ->
					val guild = guildFor(event)

					guild?.id != TEST_SERVER_ID
				}
			}
		}

		hooks {
			beforeKoinSetup {
				loadModule {
					single<LogParserConfig> { DefaultLogParserConfig() }
				}
			}
		}

		@OptIn(PrivilegedIntent::class)
		intents {
			+Intent.GuildMembers
			+Intent.MessageContent
			+Intent.GuildMessages
		}

		if (devMode) {
			// In development mode, load any plugins from `src/main/dist/plugin` if it exists.
			plugins {
				if (File("src/main/dist/plugins").isDirectory) {
					pluginPath("src/main/dist/plugins")
				}
			}
		}
	}

	bot.start()
}
