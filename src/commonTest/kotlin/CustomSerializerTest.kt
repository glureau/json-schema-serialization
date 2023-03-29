import com.github.ricky12awesome.jss.encodeToSchema
import com.github.ricky12awesome.jss.myGlobalJson
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus
import shared.FakeBigDecimal
import shared.FakeBigDecimalSerializer
import kotlin.jvm.JvmInline
import kotlin.test.Test
import kotlin.test.assertEquals

class CustomSerializerTest {

    @Serializable
    data class Container(@Contextual val fbd: FakeBigDecimal)

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
              "title": "CustomSerializerTest.Container",
              "additionalProperties": false,
              "type": "object",
              "properties": {
                "fbd": {
                  "type": "string"
                }
              },
              "required": [
                "fbd"
              ]
            }
        """.trimIndent()
        )
    }


    @JvmInline
    @Serializable
    value class FBDHolder(@Contextual val fbd: FakeBigDecimal)

    @Test
    fun checkValueClassHoldingContextual() {
        val holder = FBDHolder(FakeBigDecimal(arrayOf(1, 2)))
        println(json.encodeToString(holder))
        println(json.encodeToSchema(FBDHolder.serializer(), false))
        assertEquals(
            """
                {
                  "${"$"}schema": "http://json-schema.org/draft-07/schema",
                  "title": "CustomSerializerTest.FBDHolder",
                  "additionalProperties": false,
                  "type": "string"
                }
            """.trimIndent(),
            json.encodeToSchema(FBDHolder.serializer(), false)
        )
    }
}