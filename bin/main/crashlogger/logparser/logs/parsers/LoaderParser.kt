/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package crashlogger.logparser.logs.parsers

import crashlogger.logparser.logs.Version
import crashlogger.logparser.logs.data.LoaderType
import crashlogger.logparser.logs.data.Log
import crashlogger.logparser.logs.data.Order
import crashlogger.logparser.logs.types.LogParser



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
