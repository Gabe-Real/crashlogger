package dev.gabereal.crashlogger.modules.logs.data

import dev.gabereal.crashlogger.modules.logs.Version

public data class Mod(
	val id: String,
	val version: Version,

	// Only present on Quilt Loader
	val path: String?,
	val hash: String?,
	val type: String?
)
