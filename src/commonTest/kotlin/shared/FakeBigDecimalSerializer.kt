package shared

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.random.Random

class FakeBigDecimalSerializer : KSerializer<FakeBigDecimal> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Decimal", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: FakeBigDecimal) {
            encoder.encodeString(value.encodedString())
        }

        override fun deserialize(decoder: Decoder): FakeBigDecimal {
            return FakeBigDecimal.fromEncodedString(decoder.decodeString())
        }
    }

    class FakeBigDecimal(
        val data: Array<Int> = Array(4) { Random.nextInt() }
    ) {
        fun encodedString() = data.joinToString()

        companion object {
            fun fromEncodedString(str: String) =
                FakeBigDecimal(str.split(",").map { it.toInt() }.toTypedArray())
        }
    }