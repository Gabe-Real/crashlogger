plugins {

	alias(libs.plugins.kotlin.jvm)
	alias(libs.plugins.kotlin.serialization)

	alias(libs.plugins.detekt)

	alias(libs.plugins.kordex.docker)
	alias(libs.plugins.kordex.plugin)
	alias(libs.plugins.ksp.plugin)
}


dependencies {
	detektPlugins(libs.detekt)

	implementation(libs.ktor.client.cio)
	implementation(libs.ktor.client.encoding)

	implementation(libs.autolink)
	implementation(libs.flexver)
	implementation(libs.jansi)
	implementation(libs.jsoup)
	implementation(libs.kaml)
	implementation(libs.logging)
	implementation(libs.logback)
	implementation(libs.logback.groovy)
	implementation(libs.groovy)
	implementation(libs.semver)


	implementation(platform(libs.kotlin.bom))
	implementation(libs.kotlin.stdlib)
	implementation(libs.kx.ser)


	implementation(platform(libs.kotlin.bom))
	implementation(libs.kotlin.stdlib)
}

kordEx {
	module("pluralkit")
	module("dev-unsafe")
}
