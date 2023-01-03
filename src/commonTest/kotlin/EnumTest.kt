import com.github.ricky12awesome.jss.encodeToSchema
import com.github.ricky12awesome.jss.globalJson
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals

class EnumTest {
    @Serializable
    public enum class Country(public val code: String) {
        France("FRA"),
    }

    @Serializable
    public data class Address(
        val country: Country?,
        val details: String,
    )

    @Test
    fun check() {
        println(globalJson.encodeToSchema(Address.serializer(), false))
        assertEquals(
            globalJson.encodeToSchema(Address.serializer(), false), """
            {
              "${"$"}schema": "http://json-schema.org/draft-07/schema",
              "additionalProperties": false,
              "type": "object",
              "properties": {
                "country": {
                  "additionalProperties": false,
                  "oneOf": [
                    {
                      "type": "null"
                    },
                    {
                      "type": "string",
                      "enum": [
                        "France"
                      ]
                    }
                  ]
                },
                "details": {
                  "additionalProperties": false,
                  "type": "string"
                }
              },
              "required": [
                "country",
                "details"
              ],
              "definitions": {
              }
            }
        """.trimIndent()
        )
    }
}