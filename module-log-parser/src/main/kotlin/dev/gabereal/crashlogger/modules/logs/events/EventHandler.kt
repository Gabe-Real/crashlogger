package dev.gabereal.crashlogger.modules.logs.events

public sealed interface EventHandler {
	public suspend fun setup()
}
