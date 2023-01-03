import com.github.ricky12awesome.jss.encodeToSchema
import com.github.ricky12awesome.jss.globalJson
import kotlinx.serialization.Contextual
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class CustomSerializerTest {
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

    @Serializable
    data class Container(@Contextual val fbd: FakeBigDecimal)

    val json = Json(globalJson) {
        serializersModule += SerializersModule {
            contextual(FakeBigDecimal::class) { FakeBigDecimalSerializer() }
        }
    }

    @Test
    fun check() {
        println(json.encodeToSchema(Container.serializer(), false,))
        assertEquals(
            json.encodeToSchema(Container.serializer(), false,), """
            {
              "${"$"}schema": "http://json-schema.org/draft-07/schema",
              "additionalProperties": false,
              "type": "object",
              "properties": {
                "fbd": {
                  "additionalProperties": false,
                  "type": "string"
                }
              },
              "required": [
                "fbd"
              ],
              "definitions": {
              }
            }
        """.trimIndent()
        )
    }
}