import com.github.ricky12awesome.jss.encodeToSchema
import com.github.ricky12awesome.jss.globalJson
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class PolymorphismParentTest {

    @Serializable
    sealed interface GrandParent {
        @Serializable
        data class GrandParent1(
            val name: String,
            val children: List<Parent>
        ) : GrandParent

        @Serializable
        data class GrandParent2(
            val name: String,
            val children: List<Parent>
        ) : GrandParent

        @Serializable
        object GrandParent3 : Parent
    }

    @Serializable
    sealed interface Parent {
        @Serializable
        data class Parent1(
            val name: String,
            val childrenNames: List<String>
        ) : Parent

        @Serializable
        data class Children(val name: String)

        @Serializable
        data class Parent2(
            val name: String,
            val children: List<Children>
        ) : Parent

        @Serializable
        object Parent3 : Parent
    }

    val json = Json(globalJson) {
        classDiscriminator = "classDiscriminator"
    }

    @Test
    fun checkParents() {
        println(json.encodeToSchema(GrandParent.serializer(), false))
        assertEquals(
            json.encodeToSchema(GrandParent.serializer(), false),
            """
                {
                  "${"$"}schema": "http://json-schema.org/draft-07/schema",
                  "title": "PolymorphismParentTest.GrandParent",
                  "properties": {
                    "classDiscriminator": {
                      "type": "string",
                      "enum": [
                        "PolymorphismParentTest.GrandParent.GrandParent1",
                        "PolymorphismParentTest.GrandParent.GrandParent2"
                      ]
                    }
                  },
                  "anyOf": [
                    {
                      "additionalProperties": false,
                      "type": "object",
                      "properties": {
                        "classDiscriminator": {
                          "const": "PolymorphismParentTest.GrandParent.GrandParent1"
                        },
                        "name": {
                          "type": "string"
                        },
                        "children": {
                          "additionalProperties": false,
                          "type": "array",
                          "items": {
                            "properties": {
                              "classDiscriminator": {
                                "type": "string",
                                "enum": [
                                  "PolymorphismParentTest.GrandParent.GrandParent3",
                                  "PolymorphismParentTest.Parent.Parent1",
                                  "PolymorphismParentTest.Parent.Parent2",
                                  "PolymorphismParentTest.Parent.Parent3"
                                ]
                              }
                            },
                            "anyOf": [
                              {
                                "additionalProperties": false,
                                "type": "object",
                                "properties": {
                                  "classDiscriminator": {
                                    "const": "PolymorphismParentTest.GrandParent.GrandParent3"
                                  }
                                }
                              },
                              {
                                "additionalProperties": false,
                                "type": "object",
                                "properties": {
                                  "classDiscriminator": {
                                    "const": "PolymorphismParentTest.Parent.Parent1"
                                  },
                                  "name": {
                                    "type": "string"
                                  },
                                  "childrenNames": {
                                    "additionalProperties": false,
                                    "type": "array",
                                    "items": {
                                      "type": "string"
                                    }
                                  }
                                },
                                "required": [
                                  "name",
                                  "childrenNames"
                                ]
                              },
                              {
                                "additionalProperties": false,
                                "type": "object",
                                "properties": {
                                  "classDiscriminator": {
                                    "const": "PolymorphismParentTest.Parent.Parent2"
                                  },
                                  "name": {
                                    "type": "string"
                                  },
                                  "children": {
                                    "additionalProperties": false,
                                    "type": "array",
                                    "items": {
                                      "additionalProperties": false,
                                      "type": "object",
                                      "properties": {
                                        "name": {
                                          "type": "string"
                                        }
                                      },
                                      "required": [
                                        "name"
                                      ]
                                    }
                                  }
                                },
                                "required": [
                                  "name",
                                  "children"
                                ]
                              },
                              {
                                "additionalProperties": false,
                                "type": "object",
                                "properties": {
                                  "classDiscriminator": {
                                    "const": "PolymorphismParentTest.Parent.Parent3"
                                  }
                                }
                              }
                            ],
                            "required": [
                              "classDiscriminator"
                            ]
                          }
                        }
                      },
                      "required": [
                        "name",
                        "children"
                      ]
                    },
                    {
                      "additionalProperties": false,
                      "type": "object",
                      "properties": {
                        "classDiscriminator": {
                          "const": "PolymorphismParentTest.GrandParent.GrandParent2"
                        },
                        "name": {
                          "type": "string"
                        },
                        "children": {
                          "additionalProperties": false,
                          "type": "array",
                          "items": {
                            "properties": {
                              "classDiscriminator": {
                                "type": "string",
                                "enum": [
                                  "PolymorphismParentTest.GrandParent.GrandParent3",
                                  "PolymorphismParentTest.Parent.Parent1",
                                  "PolymorphismParentTest.Parent.Parent2",
                                  "PolymorphismParentTest.Parent.Parent3"
                                ]
                              }
                            },
                            "anyOf": [
                              {
                                "additionalProperties": false,
                                "type": "object",
                                "properties": {
                                  "classDiscriminator": {
                                    "const": "PolymorphismParentTest.GrandParent.GrandParent3"
                                  }
                                }
                              },
                              {
                                "additionalProperties": false,
                                "type": "object",
                                "properties": {
                                  "classDiscriminator": {
                                    "const": "PolymorphismParentTest.Parent.Parent1"
                                  },
                                  "name": {
                                    "type": "string"
                                  },
                                  "childrenNames": {
                                    "additionalProperties": false,
                                    "type": "array",
                                    "items": {
                                      "type": "string"
                                    }
                                  }
                                },
                                "required": [
                                  "name",
                                  "childrenNames"
                                ]
                              },
                              {
                                "additionalProperties": false,
                                "type": "object",
                                "properties": {
                                  "classDiscriminator": {
                                    "const": "PolymorphismParentTest.Parent.Parent2"
                                  },
                                  "name": {
                                    "type": "string"
                                  },
                                  "children": {
                                    "additionalProperties": false,
                                    "type": "array",
                                    "items": {
                                      "additionalProperties": false,
                                      "type": "object",
                                      "properties": {
                                        "name": {
                                          "type": "string"
                                        }
                                      },
                                      "required": [
                                        "name"
                                      ]
                                    }
                                  }
                                },
                                "required": [
                                  "name",
                                  "children"
                                ]
                              },
                              {
                                "additionalProperties": false,
                                "type": "object",
                                "properties": {
                                  "classDiscriminator": {
                                    "const": "PolymorphismParentTest.Parent.Parent3"
                                  }
                                }
                              }
                            ],
                            "required": [
                              "classDiscriminator"
                            ]
                          }
                        }
                      },
                      "required": [
                        "name",
                        "children"
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