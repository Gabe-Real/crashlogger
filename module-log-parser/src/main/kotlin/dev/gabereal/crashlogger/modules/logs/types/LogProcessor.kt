package dev.gabereal.crashlogger.modules.logs.types

import dev.gabereal.crashlogger.modules.logs.data.Log
import dev.kord.core.event.Event
import dev.kordex.core.ExtensibleBot
import dev.kordex.core.koin.KordExKoinComponent
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import org.koin.core.component.inject


@Suppress("FunctionNaming")
public abstract class LogProcessor : BaseLogHandler, KordExKoinComponent {
	private val bot: ExtensibleBot by inject()
	protected val extension: LogParserExtension get() = bot.findExtension()!!

	protected val client: HttpClient = HttpClient(CIO) {
		install(ContentNegotiation) {
			json(
				kotlinx.serialization.json.Json { ignoreUnknownKeys = true },
				ContentType.Any
			)
		}
		install(UserAgent) {
			agent = "QuiltMC/cozy-discord (quiltmc.org)"
		}
	}

	protected open suspend fun predicate(log: Log, event: Event): Boolean =
		true

	/** @suppress Internal function; use for intermediary types only. **/
	public open suspend fun _predicate(log: Log, event: Event): Boolean =
		predicate(log, event)

	public open suspend fun setup() {}

	public abstract suspend fun process(log: Log)
}
