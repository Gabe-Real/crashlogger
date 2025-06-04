package dev.gabereal.crashlogger.modules.logs.types

import dev.gabereal.crashlogger.modules.logs.data.Order

public interface BaseLogHandler {
	public val identifier: String
	public val order: Order
}

public fun <T : BaseLogHandler, C : Collection<T>> C.sortOrdered(): List<T> =
	this.sortedBy { it.order.value }
