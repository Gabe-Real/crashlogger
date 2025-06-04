/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package crashlogger.logparser.logs.processors.quilt

import crashlogger.logparser.logs.data.LoaderType
import crashlogger.logparser.logs.data.Log
import crashlogger.logparser.logs.data.Order
import crashlogger.logparser.logs.types.LogProcessor
import dev.kord.core.event.Event

class NonQuiltLoaderProcessor : LogProcessor() {
	override val identifier: String = "non-quilt-loader"
	override val order: Order = Order.Early

	override suspend fun predicate(log: Log, event: Event): Boolean =
		log.getLoaderVersion(LoaderType.Quilt) == null && log.getLoaders().isNotEmpty()

	override suspend fun process(log: Log) {
		log.hasProblems = true

		log.addMessage(
			"**You don't appear to be using Quilt:** please double-check that you have Quilt installed!"
		)
	}
}
