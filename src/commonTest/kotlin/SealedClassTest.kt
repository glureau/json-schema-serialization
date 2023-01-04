import com.github.ricky12awesome.jss.encodeToSchema
import com.github.ricky12awesome.jss.globalJson
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals

class SealedClassTest {

    @Serializable
    data class Container(val sealed: Sealed) {
        @Serializable
        sealed interface Sealed {
            @Serializable
            @SerialName("Implem1")
            data class Implem1(val name: String = "name") : Sealed

            @Serializable
            @SerialName("Implem2")
            data class Implem2(val title: String = "title") : Sealed
        }
    }

    @Test
    fun check() {
        println(globalJson.encodeToSchema(Container.Sealed.serializer(), false))
        assertEquals(
            globalJson.encodeToSchema(Container.Sealed.serializer(), false),
            """
                {
                  "${"$"}schema": "http://json-schema.org/draft-07/schema",
                  "properties": {
                    "type": {
                      "type": "string",
                      "enum": [
                        "Implem1",
                        "Implem2"
                      ]
                    }
                  },
                  "anyOf": [
                    {
                      "additionalProperties": false,
                      "type": "object",
                      "properties": {
                        "type": {
                          "const": "Implem1"
                        },
                        "name": {
                          "type": "string"
                        }
                      },
                      "required": [
                        "name"
                      ]
                    },
                    {
                      "additionalProperties": false,
                      "type": "object",
                      "properties": {
                        "type": {
                          "const": "Implem2"
                        },
                        "title": {
                          "type": "string"
                        }
                      },
                      "required": [
                        "title"
                      ]
                    }
                  ],
                  "required": [
                    "type"
                  ],
                  "definitions": {
                  }
                }
            """.trimIndent()
        )
    }

    @Test
    fun checkContainer() {
        println(globalJson.encodeToString(Container.serializer(), Container(Container.Sealed.Implem1())))
        assertEquals(
            globalJson.encodeToSchema(Container.serializer(), false),
            """
                {
                  "${"$"}schema": "http://json-schema.org/draft-07/schema",
                  "additionalProperties": false,
                  "type": "object",
                  "properties": {
                    "sealed": {
                      "properties": {
                        "type": {
                          "type": "string",
                          "enum": [
                            "Implem1",
                            "Implem2"
                          ]
                        }
                      },
                      "anyOf": [
                        {
                          "additionalProperties": false,
                          "type": "object",
                          "properties": {
                            "type": {
                              "const": "Implem1"
                            },
                            "name": {
                              "type": "string"
                            }
                          },
                          "required": [
                            "name"
                          ]
                        },
                        {
                          "additionalProperties": false,
                          "type": "object",
                          "properties": {
                            "type": {
                              "const": "Implem2"
                            },
                            "title": {
                              "type": "string"
                            }
                          },
                          "required": [
                            "title"
                          ]
                        }
                      ],
                      "required": [
                        "type"
                      ]
                    }
                  },
                  "required": [
                    "sealed"
                  ],
                  "definitions": {
                  }
                }
            """.trimIndent()
        )
    }
}