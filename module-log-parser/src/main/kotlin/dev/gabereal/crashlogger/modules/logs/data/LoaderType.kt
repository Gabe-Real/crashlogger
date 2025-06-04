package dev.gabereal.crashlogger.modules.logs.data

public sealed class LoaderType(public val name: String) {
	public object Fabric : LoaderType("fabric")
	public object Forge : LoaderType("forge")
	public object Quilt : LoaderType("quilt")
}
