import com.github.ricky12awesome.jss.encodeToSchema
import com.github.ricky12awesome.jss.globalJson
import kotlinx.serialization.Serializable
import kotlin.test.DefaultAsserter.assertEquals
import kotlin.test.Test


class NullableDefaultTest {

    @Serializable
    data class SimpleType(
        val myString: String,
        val myStringWithDefaultVal: String = "defaultVal",
        val myNullableString: String?,
        val myNullableStringWithDefaultVal: String? = "defaultVal",
        val myNullableStringWithDefaultNull: String? = null,
    )

    val json = globalJson

    @Test
    fun check_SimpleType() {
        assertEquals(
            "no message", json.encodeToSchema(SimpleType.serializer(), false,), """
            {
              "${"$"}schema": "http://json-schema.org/draft-07/schema",
              "additionalProperties": false,
              "type": "object",
              "properties": {
                "myString": {
                  "additionalProperties": false,
                  "type": "string"
                },
                "myStringWithDefaultVal": {
                  "additionalProperties": false,
                  "type": "string"
                },
                "myNullableString": {
                  "additionalProperties": false,
                  "oneOf": [
                    {
                      "type": "null"
                    },
                    {
                      "type": "string"
                    }
                  ]
                },
                "myNullableStringWithDefaultVal": {
                  "additionalProperties": false,
                  "oneOf": [
                    {
                      "type": "null"
                    },
                    {
                      "type": "string"
                    }
                  ]
                },
                "myNullableStringWithDefaultNull": {
                  "additionalProperties": false,
                  "oneOf": [
                    {
                      "type": "null"
                    },
                    {
                      "type": "string"
                    }
                  ]
                }
              },
              "required": [
                "myString",
                "myStringWithDefaultVal",
                "myNullableString"
              ],
              "definitions": {
              }
            }
        """.trimIndent()
        )
    }
}
