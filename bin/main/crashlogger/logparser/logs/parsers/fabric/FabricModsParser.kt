/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package crashlogger.logparser.logs.parsers.fabric



import crashlogger.logparser.logs.Version
import crashlogger.logparser.logs.data.LoaderType
import crashlogger.logparser.logs.data.Log
import crashlogger.logparser.logs.data.Mod
import crashlogger.logparser.logs.data.Order
import crashlogger.logparser.logs.types.LogParser
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
