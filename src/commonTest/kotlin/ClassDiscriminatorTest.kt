import com.github.ricky12awesome.jss.JsonSchema.*
import com.github.ricky12awesome.jss.dsl.*
import com.github.ricky12awesome.jss.encodeToSchema
import com.github.ricky12awesome.jss.myGlobalJson
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

@Serializable
private sealed interface Foo {
    @Serializable
    object Bar : Foo

    @Serializable
    @SerialName("Bazzz")
    object Baz : Foo
}

@Serializable
@SerialName("Bim")
object Bim

class ClassDiscriminatorTest {
    val json = Json(myGlobalJson) {
        classDiscriminator = "classDiscriminator"
    }

    @Test
    fun serialName() {
        assertEquals(
            json.encodeToSchema(Foo.Baz.serializer(), false, exposeClassDiscriminator = true),
            """
                {
                  "${"$"}schema": "http://json-schema.org/draft-07/schema",
                  "title": "Bazzz",
                  "additionalProperties": false,
                  "type": "object",
                  "properties": {
                    "classDiscriminator": {
                      "const": "Bazzz"
                    }
                  },
                  "required": [
                    "classDiscriminator"
                  ],
                  "definitions": {
                  }
                }
            """.trimIndent()
        )
    }

    @Test
    fun serialName2() {
        assertEquals(
            json.encodeToSchema(Bim.serializer(), false, exposeClassDiscriminator = true),
            """
                {
                  "${"$"}schema": "http://json-schema.org/draft-07/schema",
                  "title": "Bim",
                  "additionalProperties": false,
                  "type": "object",
                  "properties": {
                    "classDiscriminator": {
                      "const": "Bim"
                    }
                  },
                  "required": [
                    "classDiscriminator"
                  ],
                  "definitions": {
                  }
                }
            """.trimIndent()
        )
    }
}
