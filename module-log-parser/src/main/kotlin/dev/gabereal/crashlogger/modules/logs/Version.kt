package dev.gabereal.crashlogger.modules.logs

import com.unascribed.flexver.FlexVerComparator
import io.github.z4kn4fein.semver.Version as SemverVersion

@JvmInline
public value class Version(
	public val string: String,
) {
	public operator fun compareTo(other: Version): Int =
		FlexVerComparator.compare(this.string, other.string)

	public fun toSemver(strict: Boolean = true): SemverVersion =
		SemverVersion.parse(string, strict)
}
