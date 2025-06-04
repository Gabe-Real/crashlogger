package dev.gabereal.crashlogger.modules.logs.parsers.fabric


import dev.gabereal.crashlogger.modules.logs.Version
import dev.gabereal.crashlogger.modules.logs.data.LoaderType
import dev.gabereal.crashlogger.modules.logs.data.Log
import dev.gabereal.crashlogger.modules.logs.data.Mod
import dev.gabereal.crashlogger.modules.logs.data.Order
import dev.gabereal.crashlogger.modules.logs.types.LogParser
import dev.kord.core.event.Event


private val OPENING_LINE = "Loading \\d+ mods:\n".toRegex(RegexOption.IGNORE_CASE)
private val CLOSE = "\n[^\\s-]+".toRegex(RegexOption.IGNORE_CASE)

public class FabricModsParser : LogParser() {
	override val identifier: String = "mods-fabric"
	override val order: Order = Order.Default

	override suspend fun predicate(log: Log, event: Event): Boolean =
		log.getLoaderVersion(LoaderType.Fabric) != null

	override suspend fun process(log: Log) {
		val start = log.content.split(OPENING_LINE, 2).last()
		val list = start.split(CLOSE, 2).first().trim()

		list.split("\n")
			.map { it.trim().trimStart('-', ' ') }
			.forEach {
				val split = it.split(" ", limit = 2)

				log.addMod(
					Mod(
						split.first(),
						Version(split.last()),
						null,
						null,
						null
					)
				)
			}

		log.environment.javaVersion = log.getMod("java")!!.version.string
	}
}
