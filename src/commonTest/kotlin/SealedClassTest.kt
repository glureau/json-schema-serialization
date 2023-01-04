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
            object Implem2 : Sealed
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
                    "classDiscriminator": {
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
                        "classDiscriminator": {
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
                        "classDiscriminator": {
                          "const": "Implem2"
                        }
                      }
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
                        "classDiscriminator": {
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
                            "classDiscriminator": {
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
                            "classDiscriminator": {
                              "const": "Implem2"
                            }
                          }
                        }
                      ],
                      "required": [
                        "classDiscriminator"
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