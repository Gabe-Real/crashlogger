/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package crashlogger.logparser.logs.config

import crashlogger.logparser.logs.parsers.MinecraftVersionParser
import crashlogger.logparser.logs.parsers.fabric.FabricModsParser
import crashlogger.logparser.logs.processors.*
import crashlogger.logparser.logs.processors.quilt.*
import crashlogger.logparser.logs.retrievers.AttachmentLogRetriever
import crashlogger.logparser.logs.retrievers.PastebinLogRetriever
import crashlogger.logparser.logs.types.LogParser
import crashlogger.logparser.logs.types.LogProcessor
import crashlogger.logparser.logs.types.LogRetriever
import dev.kordex.core.checks.types.Check
import org.quiltmc.community.cozy.modules.logs.parsers.EnvironmentParser
import org.quiltmc.community.cozy.modules.logs.parsers.LauncherParser
import org.quiltmc.community.cozy.modules.logs.parsers.LoaderParser
import org.quiltmc.community.cozy.modules.logs.parsers.launchers.ATLauncherParser
import org.quiltmc.community.cozy.modules.logs.parsers.launchers.MMCLikeParser
import org.quiltmc.community.cozy.modules.logs.parsers.launchers.TechnicParser
import org.quiltmc.community.cozy.modules.logs.parsers.quilt.QuiltModsParser


public class SimpleLogParserConfig(private val builder: Builder) : LogParserConfig {
	override suspend fun getParsers(): List<LogParser> = builder.parsers.sortedBy { it.order.value }
	override suspend fun getProcessors(): List<LogProcessor> = builder.processors.sortedBy { it.order.value }
	override suspend fun getRetrievers(): List<LogRetriever> = builder.retrievers.sortedBy { it.order.value }
	override suspend fun getUrlRegex(): Regex = builder.urlRegex

	override suspend fun getStaffCommandChecks(): List<Check<*>> = builder.staffCommandChecks
	override suspend fun getUserCommandChecks(): List<Check<*>> = builder.userCommandChecks
	override suspend fun getGlobalPredicates(): List<Predicate> = builder.globalPredicates

	public class Builder {
		public var parsers: MutableList<LogParser> = mutableListOf(
			ATLauncherParser(),
			EnvironmentParser(),
			FabricModsParser(),
			LauncherParser(),
			LoaderParser(),
			MMCLikeParser(),
			MinecraftVersionParser(),
			QuiltModsParser(),
			TechnicParser(),
		)

		public var processors: MutableList<LogProcessor> = mutableListOf(
			FabricApisProcessor(),
			FabricImplProcessor(),
			IncompatibleModProcessor(),
			FabricModUsedWhenQuiltVersionExistsProcessor(),
			CrashReportProcessor(),
			JavaClassFileVersionProcessor(),
			MixinErrorProcessor(),
			PlayerIPProcessor(),
			QuiltLibrariesVersionProcessor(),
			QuiltLoaderVersionProcessor(),
			UnknownModProcessor(),
			MissingItemProcessor(),
		)

		public var retrievers: MutableList<LogRetriever> = mutableListOf(
			AttachmentLogRetriever(),
			PastebinLogRetriever()
		)

		public var staffCommandChecks: MutableList<Check<*>> = mutableListOf()
		public var userCommandChecks: MutableList<Check<*>> = mutableListOf()
		public var globalPredicates: MutableList<Predicate> = mutableListOf()

		public var urlRegex: Regex = "(https?://[^\\s>]+)".toRegex(RegexOption.IGNORE_CASE)

		public fun parser(parser: LogParser): Boolean = parsers.add(parser)
		public fun processor(parser: LogProcessor): Boolean = processors.add(parser)
		public fun retriever(parser: LogRetriever): Boolean = retrievers.add(parser)
		public fun staffCommandCheck(check: Check<*>): Boolean = staffCommandChecks.add(check)
		public fun userCommandCheck(check: Check<*>): Boolean = userCommandChecks.add(check)
		public fun globalPredicate(predicate: Predicate): Boolean = globalPredicates.add(predicate)

		public fun build(): SimpleLogParserConfig = SimpleLogParserConfig(this)
	}
}

public inline fun SimpleLogParserConfig(builder: (SimpleLogParserConfig.Builder).() -> Unit): SimpleLogParserConfig {
	val builderObj = SimpleLogParserConfig.Builder()

	builder(builderObj)

	return builderObj.build()
}
