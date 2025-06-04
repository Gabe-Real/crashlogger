package dev.gabereal.crashlogger.modules.logs.parsers

import dev.gabereal.crashlogger.modules.logs.Version
import dev.gabereal.crashlogger.modules.logs.data.LoaderType
import dev.gabereal.crashlogger.modules.logs.data.Log
import dev.gabereal.crashlogger.modules.logs.data.Order
import dev.gabereal.crashlogger.modules.logs.types.LogParser


private val PATTERNS = mapOf(
	"\\| Quilt Loader\\s+\\| quilt_loader\\s+\\| (\\S+).+"
		.toRegex(RegexOption.IGNORE_CASE) to LoaderType.Quilt,  // Quilt mods table

	": Loading .+ with Quilt Loader (\\S+)".toRegex(RegexOption.IGNORE_CASE) to LoaderType.Quilt,
	": Loading .+ with Fabric Loader (\\S+)".toRegex(RegexOption.IGNORE_CASE) to LoaderType.Fabric,

	"--fml.forgeVersion, ([^\\s,]+)".toRegex(RegexOption.IGNORE_CASE) to LoaderType.Forge,
	"MinecraftForge v([^\\s,]+) Initialized".toRegex(RegexOption.IGNORE_CASE) to LoaderType.Forge,  // Older versions
)

public class LoaderParser : LogParser() {
	override val identifier: String = "loader"
	override val order: Order = Order.Earlier

	override suspend fun process(log: Log) {
		for ((pattern, loader) in PATTERNS) {
			val match = pattern.find(log.content)
				?: continue

			log.setLoaderVersion(loader, Version(match.groups[1]!!.value))

			return
		}
	}
}
