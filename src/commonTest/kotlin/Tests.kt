import com.github.ricky12awesome.jss.JsonSchema
import com.github.ricky12awesome.jss.JsonSchema.*
import com.github.ricky12awesome.jss.dsl.*
import com.github.ricky12awesome.jss.encodeToSchema
import com.github.ricky12awesome.jss.globalJson
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import kotlinx.serialization.serializer
import kotlin.jvm.JvmInline
import kotlin.test.DefaultAsserter.assertEquals
import kotlin.test.Test

@Serializable
data class SimpleType(
    val myString: String,
    val myStringWithDefaultVal: String = "defaultVal",
    val myNullableString: String?,
    val myNullableStringWithDefaultVal: String? = "defaultVal",
    val myNullableStringWithDefaultNull: String? = null,
    val myProtectedString: ProtectedString,
)

@JvmInline
@Serializable
value class ProtectedString(val value: String) {
    override fun toString(): String {
        return "PROTECTED"
    }
}

@Serializable
data class Config(
    @Description(arrayOf("Name for this project."))
    val name: String = "",
    @Description(arrayOf("Theme for this project."))
    val theme: Theme = Theme()
)

interface ColorSpaceWithHue {
    val h: Int
}

@Serializable
sealed interface ThemeColor {
    @JvmInline
    @Serializable
    @SerialName("HEX")
    value class HEX(
        @Pattern("#[0-9a-fA-F]{2,6}") val hex: String
    ) : ThemeColor

    @Serializable
    @SerialName("RGB")
    data class RGB(
        @JsonSchema.IntRange(0, 255) val r: Int,
        @JsonSchema.IntRange(0, 255) val g: Int,
        @JsonSchema.IntRange(0, 255) val b: Int
    ) : ThemeColor

    @Serializable
    @SerialName("HSV")
    data class HSV(
        @JsonSchema.IntRange(1, 360) override val h: Int,
        @FloatRange(0.0, 1.0) val s: Double,
        @FloatRange(0.0, 1.0) val v: Double
    ) : ThemeColor, ColorSpaceWithHue

    @Serializable
    @SerialName("HSL")
    data class HSL(
        @JsonSchema.IntRange(1, 360) override val h: Int,
        @FloatRange(0.0, 1.0) val s: Double,
        @FloatRange(0.0, 1.0) val l: Double
    ) : ThemeColor, ColorSpaceWithHue
}

@Serializable
data class Theme(
    @Description(arrayOf("Primary color for this theme."))
    @Definition("ThemeColor") val primary: ThemeColor = ThemeColor.RGB(128, 128, 128),
    @Description(arrayOf("Secondary color for this theme."))
    @Definition("ThemeColor") val secondary: ThemeColor = ThemeColor.HSV(0, 0.0, 0.3),
    @Description(arrayOf("Accent color for this theme."))
    @Definition("ThemeColor") val accent: ThemeColor = ThemeColor.HSL(0, 0.0, 0.8),
    @Description(arrayOf("Background color for this theme."))
    @Definition("ThemeColor") val background: ThemeColor = ThemeColor.HEX("#242424"),
    val foo: Foo = Foo.Baz
)

@Serializable
sealed interface Foo {
    @Serializable
    object Bar : Foo

    @Serializable
    object Baz : Foo
}

class Tests {
    val json = globalJson

    @Test
    fun annotated_schema() {
        println(json.encodeToSchema(Config.serializer(), false))
    }

    @Test
    fun check_SimpleType() {
        assertEquals(
            "no message", json.encodeToSchema(SimpleType.serializer(), false), """
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
                },
                "myProtectedString": {
                  "additionalProperties": false,
                  "type": "string"
                }
              },
              "required": [
                "myString",
                "myStringWithDefaultVal",
                "myNullableString",
                "myProtectedString"
              ],
              "definitions": {
              }
            }
        """.trimIndent()
        )
    }

    @Test
    fun check_ProtectedString() {
        assertEquals(
            "no message", json.encodeToSchema(ProtectedString.serializer(), false), """
            {
              "${"$"}schema": "http://json-schema.org/draft-07/schema",
              "additionalProperties": false,
              "type": "string",
              "definitions": {
              }
            }
        """.trimIndent()
        )
    }


    @Test
    fun annotated_schema_interface() {
        val module = SerializersModule {
            polymorphic(ColorSpaceWithHue::class) {
                subclass(Json.serializersModule.serializer<ThemeColor.HSV>())
                subclass(Json.serializersModule.serializer<ThemeColor.HSL>())
            }
        }
        val json = Json(globalJson) {
            serializersModule = Json.serializersModule + module
        }
        println(json.encodeToSchema<ColorSpaceWithHue>(false, true))
    }

    @Test
    @ExperimentalJsonSchemaDSL
    fun schema_dsl() {
        val schema = buildSchema {
            val data by definitions(PropertyType.Object) {
                properties {
                    property("name", PropertyType.String) {
                        description = "Name of something"
                    }

                    property("type", PropertyType.String) {
                        enum = listOf("A", "B", "C")
                    }
                }
            }

            properties {
                property("array", PropertyType.Array(PropertyType.Object)) {
                    description = "Some array with random data"
                    minItems = 1
                    maxItems = 64

                    items {
                        reference(data)
                    }
                }

                property("map", PropertyType.ObjectMap(PropertyType.Object)) {
                    description = "Some map with random data"

                    propertyNames {
                        pattern = Regex("^(\\d*)\$")
                    }

                    additionalProperties {
                        reference(data)
                    }
                }
            }
        }

        println(json.encodeToString(JsonObject.serializer(), schema))
    }
}
