package dev.gabereal.crashlogger.modules.logs.data

public open class Launcher(
	public val name: String,
	public val version: String? = null
) {
	public companion object {
		public const val ATLauncher: String = "ATLauncher"
		public const val MultiMC: String = "MultiMC"
		public const val Prism: String = "Prism"
		public const val PolyMC: String = "PolyMC"
		public const val Technic: String = "Technic"
		public const val TLauncher: String = "TLauncher"
	}
}
