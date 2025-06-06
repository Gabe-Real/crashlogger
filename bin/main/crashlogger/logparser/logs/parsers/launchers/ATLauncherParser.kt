/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package crashlogger.logparser.logs.parsers.launchers

import crashlogger.logparser.logs.data.Launcher
import crashlogger.logparser.logs.data.Log
import crashlogger.logparser.logs.data.Order
import crashlogger.logparser.logs.types.LogParser
import dev.kord.core.event.Event


private val INFO_LINE_REGEX = "Launching Minecraft with the following arguments \\([^)]+\\): \\[([^]]+)]\n"
	.toRegex(RegexOption.IGNORE_CASE)

private val OS_REGEX = "-Dos.name=(.+?), -".toRegex(RegexOption.IGNORE_CASE)

public class ATLauncherParser : LogParser() {
	override val identifier: String = "launcher-atlauncher"
	override val order: Order = Order.Early

	override suspend fun predicate(log: Log, event: Event): Boolean =
		log.launcher?.name == Launcher.ATLauncher

	override suspend fun process(log: Log) {
		val infoString = INFO_LINE_REGEX.findAll(log.content).lastOrNull()?.groupValues?.getOrNull(1)
			?: return

		log.environment.jvmArgs = infoString.substringBefore(", -Djava.library.path")

		val osString = OS_REGEX.find(infoString)?.groupValues?.getOrNull(1)

		if (osString != null) {
			log.environment.os = osString
		}
	}
}
