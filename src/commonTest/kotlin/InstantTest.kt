import com.github.ricky12awesome.jss.encodeToSchema
import com.github.ricky12awesome.jss.globalJson
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals

class InstantTest {

    @Serializable
    public data class Event(
        val createdAt: Instant,
    )

    @Test
    fun check_ProtectedString() {
        println(globalJson.encodeToSchema(Event.serializer(), false,))
        assertEquals(
            globalJson.encodeToSchema(Event.serializer(), false,), """
            {
              "${"$"}schema": "http://json-schema.org/draft-07/schema",
              "title": "InstantTest.Event",
              "additionalProperties": false,
              "type": "object",
              "properties": {
                "createdAt": {
                  "type": "string",
                  "format": "date-time"
                }
              },
              "required": [
                "createdAt"
              ],
              "definitions": {
              }
            }
        """.trimIndent()
        )
    }
}