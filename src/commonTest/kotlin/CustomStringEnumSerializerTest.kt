import com.github.ricky12awesome.jss.StringEnumSerialDescriptor
import com.github.ricky12awesome.jss.encodeToSchema
import com.github.ricky12awesome.jss.myGlobalJson
import kotlinx.serialization.Contextual
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus
import shared.FakeBigDecimal
import shared.FakeBigDecimalSerializer
import kotlin.test.Test
import kotlin.test.assertEquals

class CustomStringEnumSerializerTest {
    @Serializable
    data class Container(@Serializable(YesNoSerializer::class) val response: String)

    class YesNoSerializer : KSerializer<String> {
        private val possibleValues = arrayOf("YES", "NO")

        override val descriptor: SerialDescriptor
            get() = StringEnumSerialDescriptor("YesNoSerializer", possibleValues = possibleValues)

        override fun deserialize(decoder: Decoder): String {
            val str = decoder.decodeString()
            return if (str.equals("yes", ignoreCase = true)) "YES"
            else "NO"
        }

        override fun serialize(encoder: Encoder, value: String) {
            if (value.equals("yes", ignoreCase = true))
                encoder.encodeString("YES")
            else encoder.encodeString("NO")
        }
    }

    val json = Json(myGlobalJson) {
        serializersModule += SerializersModule {
            contextual(FakeBigDecimal::class) { FakeBigDecimalSerializer() }
        }
    }


    @Test
    fun check() {
        assertEquals(
            json.encodeToSchema(Container.serializer(), false), """
            {
              "${"$"}schema": "http://json-schema.org/draft-07/schema",
              "title": "CustomStringEnumSerializerTest.Container",
              "additionalProperties": false,
              "type": "object",
              "properties": {
                "response": {
                  "type": "string",
                  "enum": [
                    "NO",
                    "YES"
                  ]
                }
              },
              "required": [
                "response"
              ]
            }
        """.trimIndent()
        )
    }

}