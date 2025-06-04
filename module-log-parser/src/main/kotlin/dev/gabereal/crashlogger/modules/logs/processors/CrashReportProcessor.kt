package dev.gabereal.crashlogger.modules.logs.processors


import dev.gabereal.crashlogger.modules.logs.data.Log
import dev.gabereal.crashlogger.modules.logs.data.Order
import dev.gabereal.crashlogger.modules.logs.types.LogProcessor
import dev.kord.core.event.Event


private val CRASH_REPORT_REGEX = "Crashed! The full crash report has been saved to (\\S+)"
	.toRegex(RegexOption.IGNORE_CASE)

public class CrashReportProcessor : LogProcessor() {
	override val identifier: String = "crash-reports"
	override val order: Order = Order.Default

	override suspend fun predicate(log: Log, event: Event): Boolean =
		CRASH_REPORT_REGEX.find(log.content) != null

	override suspend fun process(log: Log) {
		val match = CRASH_REPORT_REGEX.find(log.content) ?: return
		log.addMessage("Please also provide the crash report at `${match.groups[1]!!.value}`")
	}
}
