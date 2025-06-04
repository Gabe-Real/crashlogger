@file:UseSerializers(MatchRegexSerializer::class)

package dev.gabereal.crashlogger.modules.logs.data

import dev.gabereal.crashlogger.modules.logs.MatchRegexSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.UseSerializers


import kotlinx.serialization.Serializable


@Serializable
public data class UrlTransformSplit(
	public val group: Int,
	public val string: String
)

@Serializable
public data class UrlTransform(
	public val match: Regex,
	public val output: String,

	public val split: UrlTransformSplit? = null
)

@Serializable
public enum class ScrapeType {
	@SerialName("first-element")
	FirstElement,

	@SerialName("hrefs")
	Hrefs
}

@Serializable
public data class Scrape(
	public val match: Regex,
	public val type: ScrapeType,
	public val selector: String,
)

@Serializable
public data class PastebinConfig(
	public val raw: List<Regex>,

	@SerialName("url-transform")
	public val urlTransform: List<UrlTransform>,
	public val scrape: List<Scrape>,
)
