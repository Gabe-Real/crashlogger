/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package crashlogger.logparser.logs.events

import crashlogger.logparser.logs.LogParserExtension
import dev.kord.core.entity.Message
import dev.kordex.core.extensions.event
import dev.kordex.modules.pluralkit.events.PKMessageCreateEvent

public class PKEventHandler(private val extension: LogParserExtension) : EventHandler {
	override suspend fun setup(): Unit = with(extension) {
		event<PKMessageCreateEvent> {
			action {
				handleMessage(event.message, event)
			}
		}
	}
}
