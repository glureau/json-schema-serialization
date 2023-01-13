import com.github.ricky12awesome.jss.dsl.*
import com.github.ricky12awesome.jss.myGlobalJson
import kotlinx.serialization.json.JsonObject
import kotlin.test.Test

class SchemaDslTest {
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

        println(myGlobalJson.encodeToString(JsonObject.serializer(), schema))
    }
}
