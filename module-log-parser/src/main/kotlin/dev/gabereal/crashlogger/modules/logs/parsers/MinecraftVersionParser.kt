package dev.gabereal.crashlogger.modules.logs.parsers

import dev.gabereal.crashlogger.modules.logs.Version
import dev.gabereal.crashlogger.modules.logs.data.Log
import dev.gabereal.crashlogger.modules.logs.data.Order
import dev.gabereal.crashlogger.modules.logs.types.LogParser


private val PATTERNS = listOf(
	// Impossible to do this for vanilla; the logs don't contain the MC version for some reason

	"\\| Minecraft\\s+\\| minecraft\\s+\\| (\\S+).+"
		.toRegex(RegexOption.IGNORE_CASE),  // Quilt mods table

	": Loading Minecraft (\\S+)".toRegex(RegexOption.IGNORE_CASE), // Fabric, Quilt
	"--fml.mcVersion, ([^\\s,]+)".toRegex(RegexOption.IGNORE_CASE), // Forge
	"--version, ([^,]+),".toRegex(RegexOption.IGNORE_CASE), // ATLauncher
	" --version (\\S+) ".toRegex(RegexOption.IGNORE_CASE), // MMC, Prism, PolyMC
)

public class MinecraftVersionParser : LogParser() {
	override val identifier: String = "minecraft-version"
	override val order: Order = Order.Earlier

	override suspend fun process(log: Log) {
		for (pattern in PATTERNS) {
			val match = pattern.find(log.content)
				?: continue

			log.minecraftVersion = Version(match.groups[1]!!.value)
		}
	}
}
