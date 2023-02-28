import com.github.ricky12awesome.jss.encodeToSchema
import com.github.ricky12awesome.jss.myGlobalJson
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus
import shared.FakeBigDecimal
import shared.FakeBigDecimalSerializer
import kotlin.jvm.JvmInline
import kotlin.test.Test
import kotlin.test.assertEquals


class ValueClassTest {

    @JvmInline
    @Serializable
    private value class ProtectedString(val value: String?) {
        override fun toString(): String {
            return "PROTECTED"
        }
    }

    @Serializable
    private data class MaybeUser(val username: ProtectedString? = null)

    @JvmInline
    @Serializable
    private value class ProtectedBigDecimal(@Contextual val fbd: FakeBigDecimal? = null) {
        override fun toString(): String {
            return "PROTECTED"
        }
    }

    @Serializable
    private data class MaybeDecimal(val fbd: ProtectedBigDecimal? = null)

    val json = Json(myGlobalJson) {
        serializersModule += SerializersModule {
            contextual(FakeBigDecimal::class) { FakeBigDecimalSerializer() }
        }
    }

    @Test
    fun withcontext() {
        assertEquals(json.encodeToSchema(MaybeDecimal.serializer(), false),
            """
                {
                  "${"$"}schema": "http://json-schema.org/draft-07/schema",
                  "title": "ValueClassTest.MaybeDecimal",
                  "additionalProperties": false,
                  "type": "object",
                  "properties": {
                    "fbd": {
                      "oneOf": [
                        {
                          "type": "null"
                        },
                        {
                          "type": "string"
                        }
                      ]
                    }
                  }
                }
            """.trimIndent()
        )
    }

    @Test
    fun checkValueClassNullability() {
        assertEquals(
            myGlobalJson.encodeToSchema(MaybeUser.serializer(), false),
            """
                {
                  "${"$"}schema": "http://json-schema.org/draft-07/schema",
                  "title": "ValueClassTest.MaybeUser",
                  "additionalProperties": false,
                  "type": "object",
                  "properties": {
                    "username": {
                      "oneOf": [
                        {
                          "type": "null"
                        },
                        {
                          "type": "string"
                        }
                      ]
                    }
                  }
                }
            """.trimIndent()
        )
    }

    @Test
    fun check_ProtectedString() {
        assertEquals(
            myGlobalJson.encodeToSchema(ProtectedString.serializer(), false), """
            {
              "${"$"}schema": "http://json-schema.org/draft-07/schema",
              "title": "ValueClassTest.ProtectedString",
              "additionalProperties": false,
              "type": "string"
            }
        """.trimIndent()
        )
    }
}
