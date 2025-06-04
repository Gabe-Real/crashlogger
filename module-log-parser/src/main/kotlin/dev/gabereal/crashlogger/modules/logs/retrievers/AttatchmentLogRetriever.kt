package dev.gabereal.crashlogger.modules.logs.retrievers


import dev.gabereal.crashlogger.modules.logs.data.Order
import dev.gabereal.crashlogger.modules.logs.types.LogRetriever
import dev.kord.core.event.Event
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.utils.io.*
import kotlinx.io.readByteArray

import java.net.URL

private val DOMAINS: Array<String> = arrayOf(
	"cdn.discord.com",
	"cdn.discordapp.com",
	"media.discord.com",
	"media.discordapp.com",
)

private val EXTENSIONS: Array<String> = arrayOf(
	"log",
	"txt"
)

public class AttachmentLogRetriever : LogRetriever() {
	override val identifier: String = "message-attachment"
	override val order: Order = Order.Earlier

	@Suppress("SpreadOperator")
	override suspend fun predicate(url: URL, event: Event): Boolean =
		url.host in DOMAINS && (
			url.path.endsWithExtensions(*EXTENSIONS) ||
				'.' !in url.path.substringAfterLast('/')
			)

	override suspend fun process(url: URL): Set<String> =
		setOf(client.get(url).bodyAsChannel().readRemaining().readByteArray().decodeToString())
}
