import com.github.ricky12awesome.jss.encodeToSchema
import com.github.ricky12awesome.jss.myGlobalJson
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
        println(myGlobalJson.encodeToSchema(Address.serializer(), false))
        assertEquals(
            myGlobalJson.encodeToSchema(Address.serializer(), false), """
            {
              "${"$"}schema": "http://json-schema.org/draft-07/schema",
              "title": "EnumTest.Address",
              "additionalProperties": false,
              "type": "object",
              "properties": {
                "country": {
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
                  "type": "string"
                }
              },
              "required": [
                "country",
                "details"
              ]
            }
        """.trimIndent()
        )
    }
}