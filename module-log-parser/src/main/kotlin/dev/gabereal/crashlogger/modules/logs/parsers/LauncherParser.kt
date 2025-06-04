package dev.gabereal.crashlogger.modules.logs.parsers

import com.github.zafarkhaja.semver.Version
import dev.gabereal.crashlogger.modules.logs.data.Launcher
import dev.gabereal.crashlogger.modules.logs.data.Log
import dev.gabereal.crashlogger.modules.logs.data.Order
import dev.gabereal.crashlogger.modules.logs.types.LogParser


private val PATTERNS = mapOf(
	Launcher.ATLauncher to "minecraft.launcher.brand=ATLauncher".toRegex(RegexOption.IGNORE_CASE),
	Launcher.MultiMC to "MultiMC version: (\\S+)".toRegex(RegexOption.IGNORE_CASE),
	Launcher.Prism to "Prism Launcher version: (\\S+)".toRegex(RegexOption.IGNORE_CASE),
	Launcher.PolyMC to "PolyMC version: (\\S+)".toRegex(RegexOption.IGNORE_CASE),
	Launcher.Technic to "api\\.technicpack\\.net resolves to".toRegex(RegexOption.IGNORE_CASE),

	Launcher.TLauncher to "Starting TLauncher ([^\n]+)".toRegex(RegexOption.IGNORE_CASE),
	Launcher.TLauncher to "tlauncher\\.org".toRegex(RegexOption.IGNORE_CASE),
)

public class LauncherParser : LogParser() {
	override val identifier: String = "launcher"
	override val order: Order = Order.Earlier

	// You tried, Detekt.
	@Suppress("UnconditionalJumpStatementInLoop")
	override suspend fun process(log: Log) {
		val (launcher, match) = PATTERNS.entries
			.map { (launcher, pattern) -> Pair(launcher, pattern.find(log.content)) }
			.firstOrNull { (_, match) -> match != null }
			?: return

		log.launcher = Launcher(launcher, match!!.groups[1]?.value)

		// MultiMC-derived launchers declare their version in the log, so we can parse it
		if (launcher == Launcher.MultiMC || launcher == Launcher.PolyMC || launcher == Launcher.Prism) {
			val message: String? = match.groups[1]?.value
			if (message != null) {
				log.launcherVersion = message.split("version: ")[1]
			}
		}

		if (launcher == Launcher.Prism && log.launcherVersion != null) {
			val version: Version = Version.parse(log.launcherVersion!!)
			if (version.compareTo(Version.parse("7.0")) < 0) {
				log.addMessage(
					"**It looks like you're using an outdated version of Prism.** +" +
						"Please note that Prism [formerly broke uploaded logs]" +
						"(https://github.com/PrismLauncher/PrismLauncher/issues/930), and the information " +
						"provided here may be incomplete. Please either update Prism to 7.0+ or manually " +
						"upload your `latest.log` file to the channel instead."
				)
			}
		}
	}
}
