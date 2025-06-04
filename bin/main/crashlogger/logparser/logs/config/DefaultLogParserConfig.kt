package crashlogger.logparser.logs.config

import crashlogger.logparser.logs.types.LogParser
import crashlogger.logparser.logs.types.LogProcessor
import crashlogger.logparser.logs.types.LogRetriever
import dev.kordex.core.checks.types.Check

class DefaultLogParserConfig : LogParserConfig {

	override suspend fun getUrlRegex(): Regex {
		return Regex("""(https?://[^\s]+)""")
	}

	override suspend fun getParsers(): List<LogParser> {
		// Return empty or mocked list for now
		return emptyList()
	}

	override suspend fun getProcessors(): List<LogProcessor> {
		return emptyList()
	}

	override suspend fun getRetrievers(): List<LogRetriever> {
		return emptyList()
	}

	override suspend fun getStaffCommandChecks(): List<Check<*>> {
		return emptyList()
	}

	override suspend fun getUserCommandChecks(): List<Check<*>> {
		return emptyList()
	}

	override suspend fun getGlobalPredicates(): List<Predicate> {
		return emptyList()
	}
}
