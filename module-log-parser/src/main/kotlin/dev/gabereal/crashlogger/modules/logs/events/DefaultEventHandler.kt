package dev.gabereal.crashlogger.modules.logs.events

import dev.kord.core.event.message.MessageCreateEvent
import dev.kordex.core.extensions.event


public class DefaultEventHandler(private val extension: LogParserExtension) : EventHandler {
	override suspend fun setup(): Unit = with(extension) {
		event<MessageCreateEvent> {
			action {
				handleMessage(event.message, event)
			}
		}
	}
}
