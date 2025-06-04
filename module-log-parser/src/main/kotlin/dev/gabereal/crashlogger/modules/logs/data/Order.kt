@file:Suppress("MagicNumber")

package dev.gabereal.crashlogger.modules.logs.data

public open class Order(public val value: Int) {
	public object Earlier : Order(-200)
	public object Early : Order(-100)
	public object Default : Order(0)
	public object Late : Order(100)
	public object Later : Order(200)
}
