import com.github.ricky12awesome.jss.JsonSchema
import com.github.ricky12awesome.jss.encodeToSchema
import com.github.ricky12awesome.jss.myGlobalJson
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import kotlinx.serialization.serializer
import kotlin.jvm.JvmInline
import kotlin.test.Test
import kotlin.test.assertEquals

class PolymorphismColorTest {

    @Serializable
    private data class Config(
        @JsonSchema.Description(arrayOf("Name for this project."))
        val name: String = "",
        @JsonSchema.Description(arrayOf("Theme for this project."))
        val theme: Theme = Theme()
    )

    @SerialName("ColorSpaceWithHue")
    private interface ColorSpaceWithHue {
        val h: Int
    }

    @Serializable
    private sealed interface ThemeColor {
    }

    @JvmInline
    @Serializable
    @SerialName("HEX")
    value class HEX(
        @JsonSchema.Pattern("#[0-9a-fA-F]{2,6}") val hex: String
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
        @JsonSchema.FloatRange(0.0, 1.0) val s: Double,
        @JsonSchema.FloatRange(0.0, 1.0) val v: Double
    ) : ThemeColor, ColorSpaceWithHue

    @Serializable
    @SerialName("HSL")
    data class HSL(
        @JsonSchema.IntRange(1, 360) override val h: Int,
        @JsonSchema.FloatRange(0.0, 1.0) val s: Double,
        @JsonSchema.FloatRange(0.0, 1.0) val l: Double
    ) : ThemeColor, ColorSpaceWithHue

    @Serializable
    private data class Theme(
        @JsonSchema.Description(arrayOf("Primary color for this theme."))
        @JsonSchema.Definition("ThemeColor") val primary: ThemeColor = RGB(128, 128, 128),
        @JsonSchema.Description(arrayOf("Secondary color for this theme."))
        @JsonSchema.Definition("ThemeColor") val secondary: ThemeColor = HSV(0, 0.0, 0.3),
        @JsonSchema.Description(arrayOf("Accent color for this theme."))
        @JsonSchema.Definition("ThemeColor") val accent: ThemeColor = HSL(0, 0.0, 0.8),
        @JsonSchema.Description(arrayOf("Background color for this theme."))
        @JsonSchema.Definition("ThemeColor") val background: ThemeColor = HEX("#242424"),
    )

    val json = Json(myGlobalJson) {
        classDiscriminator = "classDiscriminator"
    }

    @Test
    fun checkColors() {
        println(json.encodeToSchema(Config.serializer(), false))
        assertEquals(
            json.encodeToSchema(Config.serializer(), false),
            """
                {
                  "${"$"}schema": "http://json-schema.org/draft-07/schema",
                  "title": "PolymorphismColorTest.Config",
                  "additionalProperties": false,
                  "type": "object",
                  "properties": {
                    "name": {
                      "description": "Name for this project.",
                      "type": "string"
                    },
                    "theme": {
                      "additionalProperties": false,
                      "type": "object",
                      "properties": {
                        "primary": {
                          "description": "Primary color for this theme.",
                          "additionalProperties": false,
                          "${"$"}ref": "#/definitions/ThemeColor"
                        },
                        "secondary": {
                          "description": "Secondary color for this theme.",
                          "additionalProperties": false,
                          "${"$"}ref": "#/definitions/ThemeColor"
                        },
                        "accent": {
                          "description": "Accent color for this theme.",
                          "additionalProperties": false,
                          "${"$"}ref": "#/definitions/ThemeColor"
                        },
                        "background": {
                          "description": "Background color for this theme.",
                          "additionalProperties": false,
                          "${"$"}ref": "#/definitions/ThemeColor"
                        }
                      },
                      "required": [
                        "primary",
                        "secondary",
                        "accent",
                        "background"
                      ]
                    }
                  },
                  "required": [
                    "name",
                    "theme"
                  ],
                  "definitions": {
                    "ThemeColor": {
                      "properties": {
                        "classDiscriminator": {
                          "type": "string",
                          "enum": [
                            "HEX",
                            "HSL",
                            "HSV",
                            "RGB"
                          ]
                        }
                      },
                      "anyOf": [
                        {
                          "type": "string",
                          "properties": {
                            "classDiscriminator": {
                              "const": "HEX"
                            }
                          }
                        },
                        {
                          "additionalProperties": false,
                          "type": "object",
                          "properties": {
                            "classDiscriminator": {
                              "const": "HSL"
                            },
                            "h": {
                              "type": "number",
                              "minimum": 1,
                              "maximum": 360
                            },
                            "s": {
                              "type": "number",
                              "minimum": 0.0,
                              "maximum": 1.0
                            },
                            "l": {
                              "type": "number",
                              "minimum": 0.0,
                              "maximum": 1.0
                            }
                          },
                          "required": [
                            "h",
                            "s",
                            "l"
                          ]
                        },
                        {
                          "additionalProperties": false,
                          "type": "object",
                          "properties": {
                            "classDiscriminator": {
                              "const": "HSV"
                            },
                            "h": {
                              "type": "number",
                              "minimum": 1,
                              "maximum": 360
                            },
                            "s": {
                              "type": "number",
                              "minimum": 0.0,
                              "maximum": 1.0
                            },
                            "v": {
                              "type": "number",
                              "minimum": 0.0,
                              "maximum": 1.0
                            }
                          },
                          "required": [
                            "h",
                            "s",
                            "v"
                          ]
                        },
                        {
                          "additionalProperties": false,
                          "type": "object",
                          "properties": {
                            "classDiscriminator": {
                              "const": "RGB"
                            },
                            "r": {
                              "type": "number",
                              "minimum": 0,
                              "maximum": 255
                            },
                            "g": {
                              "type": "number",
                              "minimum": 0,
                              "maximum": 255
                            },
                            "b": {
                              "type": "number",
                              "minimum": 0,
                              "maximum": 255
                            }
                          },
                          "required": [
                            "r",
                            "g",
                            "b"
                          ]
                        }
                      ],
                      "required": [
                        "classDiscriminator"
                      ]
                    }
                  }
                }
            """.trimIndent()
        )
    }


    @Test
    fun annotated_schema_interface() {
        val module = SerializersModule {
            polymorphic(ColorSpaceWithHue::class) {
                subclass(Json.serializersModule.serializer<HSV>())
                subclass(Json.serializersModule.serializer<HSL>())
            }
        }
        val json = Json(myGlobalJson) {
            serializersModule = Json.serializersModule + module
        }
        println(json.encodeToSchema<ColorSpaceWithHue>(false))
        assertEquals(
            json.encodeToSchema<ColorSpaceWithHue>(false),
            """
                {
                  "${"$"}schema": "http://json-schema.org/draft-07/schema",
                  "additionalProperties": false,
                  "properties": {
                    "classDiscriminator": {
                      "type": "string",
                      "enum": [
                        "HSL",
                        "HSV"
                      ]
                    }
                  },
                  "anyOf": [
                    {
                      "additionalProperties": false,
                      "type": "object",
                      "properties": {
                        "classDiscriminator": {
                          "const": "HSL"
                        },
                        "h": {
                          "type": "number",
                          "minimum": 1,
                          "maximum": 360
                        },
                        "s": {
                          "type": "number",
                          "minimum": 0.0,
                          "maximum": 1.0
                        },
                        "l": {
                          "type": "number",
                          "minimum": 0.0,
                          "maximum": 1.0
                        }
                      },
                      "required": [
                        "h",
                        "s",
                        "l"
                      ]
                    },
                    {
                      "additionalProperties": false,
                      "type": "object",
                      "properties": {
                        "classDiscriminator": {
                          "const": "HSV"
                        },
                        "h": {
                          "type": "number",
                          "minimum": 1,
                          "maximum": 360
                        },
                        "s": {
                          "type": "number",
                          "minimum": 0.0,
                          "maximum": 1.0
                        },
                        "v": {
                          "type": "number",
                          "minimum": 0.0,
                          "maximum": 1.0
                        }
                      },
                      "required": [
                        "h",
                        "s",
                        "v"
                      ]
                    }
                  ],
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