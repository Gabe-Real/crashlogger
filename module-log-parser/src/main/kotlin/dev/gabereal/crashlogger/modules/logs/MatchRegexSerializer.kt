package dev.gabereal.crashlogger.modules.logs

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

public class MatchRegexSerializer : KSerializer<Regex> {
	override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Regex", PrimitiveKind.STRING)

	override fun deserialize(decoder: Decoder): Regex {
		val string = decoder.decodeString()

		return string.toRegex(RegexOption.IGNORE_CASE)
	}

	override fun serialize(encoder: Encoder, value: Regex) {
		val string = value.toString()

		encoder.encodeString(string)
	}
}
