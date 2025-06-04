package dev.gabereal.crashlogger.modules.logs.types

import dev.gabereal.crashlogger.modules.logs.data.Log
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
