package dev.gabereal.crashlogger.modules.logs.parsers.launchers

import dev.gabereal.crashlogger.modules.logs.data.Launcher
import dev.gabereal.crashlogger.modules.logs.data.Log
import dev.gabereal.crashlogger.modules.logs.data.Order
import dev.gabereal.crashlogger.modules.logs.types.LogParser
import dev.kord.core.event.Event


private val JAVA_REGEX = (
	"Checking Java version[.]{3}\nJava is version ([^,]+), using ([\\w\\s(]+[)]?) architecture, from ([^.]+)\\."
	).toRegex(RegexOption.IGNORE_CASE)

private val JAVA_ARGS_REGEX = "Java Arguments:\n\\[([^]]+)]".toRegex(RegexOption.IGNORE_CASE)

private const val LIBRARIES_OPEN = "\nLibraries:\n"
private const val LIBRARIES_CLOSE = "\nMods:"

public class MMCLikeParser : LogParser() {
	override val identifier: String = "launcher-mmc-like"
	override val order: Order = Order.Early

	override suspend fun predicate(log: Log, event: Event): Boolean =
		// No PolyMC, we don't support nazis
		log.launcher?.name in arrayOf(Launcher.MultiMC, Launcher.Prism)

	@Suppress("MagicNumber")
	override suspend fun process(log: Log) {
		val javaMatch = JAVA_REGEX.find(log.content)

		if (javaMatch != null) {
			val java = "Java ${javaMatch.groupValues[1]}@${javaMatch.groupValues[2]}, ${javaMatch.groupValues[3]}"

			log.environment.jvmVersion = java
		}

		val javaArgsMatch = JAVA_ARGS_REGEX.find(log.content)

		if (javaArgsMatch != null) {
			log.environment.jvmArgs = javaArgsMatch.groupValues[1]
		}

		val librariesStart = log.content.substringAfter(LIBRARIES_OPEN)
		val librariesList = librariesStart.substringBefore(LIBRARIES_CLOSE)

		when {
			"linux" in librariesList ->
				log.environment.os = "Linux"

			"macos" in librariesList ->
				log.environment.os = "MacOS"

			"windows" in librariesList ->
				log.environment.os = "Windows"
		}
	}
}
