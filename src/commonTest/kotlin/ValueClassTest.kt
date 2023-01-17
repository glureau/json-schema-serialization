import com.github.ricky12awesome.jss.encodeToSchema
import com.github.ricky12awesome.jss.myGlobalJson
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline
import kotlin.test.assertEquals
import kotlin.test.Test

@JvmInline
@Serializable
private value class ProtectedString(val value: String) {
    override fun toString(): String {
        return "PROTECTED"
    }
}

@Serializable
private data class MaybeUser(val username: ProtectedString? = null)

class ValueClassTest {

    @Test
    fun checkValueClassNullability() {
        assertEquals(
            myGlobalJson.encodeToSchema(MaybeUser.serializer(), false),
            """
                {
                  "${"$"}schema": "http://json-schema.org/draft-07/schema",
                  "title": "MaybeUser",
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
              "title": "ProtectedString",
              "additionalProperties": false,
              "type": "string"
            }
        """.trimIndent()
        )
    }
}
