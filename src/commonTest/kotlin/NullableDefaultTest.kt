import com.github.ricky12awesome.jss.encodeToSchema
import com.github.ricky12awesome.jss.globalJson
import kotlinx.serialization.Serializable
import kotlin.test.assertEquals
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
            json.encodeToSchema(SimpleType.serializer(), false,), """
            {
              "${"$"}schema": "http://json-schema.org/draft-07/schema",
              "additionalProperties": false,
              "type": "object",
              "properties": {
                "myString": {
                  "type": "string"
                },
                "myStringWithDefaultVal": {
                  "type": "string"
                },
                "myNullableString": {
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
