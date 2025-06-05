/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package crashlogger.logparser.logs.retrievers

import crashlogger.logparser.logs.data.Order
import crashlogger.logparser.logs.endsWithExtensions
import crashlogger.logparser.logs.types.LogRetriever
import dev.kord.core.event.Event
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.utils.io.*
import kotlinx.io.readByteArray
import java.net.URL
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

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
