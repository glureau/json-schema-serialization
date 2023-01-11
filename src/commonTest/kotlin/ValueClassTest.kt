import com.github.ricky12awesome.jss.encodeToSchema
import com.github.ricky12awesome.jss.globalJson
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline
import kotlin.test.DefaultAsserter.assertEquals
import kotlin.test.Test

@JvmInline
@Serializable
private value class ProtectedString(val value: String) {
    override fun toString(): String {
        return "PROTECTED"
    }
}

class ValueClassTest {
    @Test
    fun check_ProtectedString() {
        assertEquals(
            "no message", globalJson.encodeToSchema(ProtectedString.serializer(), false,), """
            {
              "${"$"}schema": "http://json-schema.org/draft-07/schema",
              "title": "ProtectedString",
              "additionalProperties": false,
              "type": "string",
              "definitions": {
              }
            }
        """.trimIndent()
        )
    }
}
