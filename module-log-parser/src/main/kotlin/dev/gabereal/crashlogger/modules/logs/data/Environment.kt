package dev.gabereal.crashlogger.modules.logs.data

// NOTE: Doesn't seem like it's possible to get any of this info reliably
@Suppress("DataClassShouldBeImmutable")
public data class Environment(
	public var cpu: String? = null,
	public var gpu: String? = null,

	public var gameMemory: String? = null,
	public var systemMemory: String? = null,
	public var shaderpack: String? = null,

	public var os: String? = null,
	public var javaVersion: String? = null,
	public var jvmVersion: String? = null,
	public var jvmArgs: String? = null,
)
