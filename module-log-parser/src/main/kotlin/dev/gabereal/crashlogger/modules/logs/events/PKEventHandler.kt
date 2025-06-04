package dev.gabereal.crashlogger.modules.logs.events

import dev.kordex.core.extensions.event
import dev.kordex.modules.pluralkit.events.PKMessageCreateEvent
import org.quiltmc.community.cozy.modules.logs.LogParserExtension

public class PKEventHandler(private val extension: LogParserExtension) : EventHandler {
	override suspend fun setup(): Unit = with(extension) {
		event<PKMessageCreateEvent> {
			action {
				handleMessage(event.message, event)
			}
		}
	}
}
