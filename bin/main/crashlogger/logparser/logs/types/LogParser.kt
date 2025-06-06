/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package crashlogger.logparser.logs.types

import crashlogger.logparser.logs.data.Log
import dev.kord.core.event.Event

@Suppress("FunctionNaming")
public abstract class LogParser : BaseLogHandler {
	protected open suspend fun predicate(log: Log, event: Event): Boolean =
		true

	/** @suppress Internal function; use for intermediary types only. **/
	public open suspend fun _predicate(log: Log, event: Event): Boolean =
		predicate(log, event)

	public abstract suspend fun process(log: Log)
}
