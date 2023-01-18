import com.github.ricky12awesome.jss.JsonSchema
import com.github.ricky12awesome.jss.encodeToSchema
import com.github.ricky12awesome.jss.jsonFormatValidator
import com.github.ricky12awesome.jss.myGlobalJson
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlin.jvm.JvmInline
import kotlin.test.Test
import kotlin.test.assertEquals

class AnnotationInheritanceTest {

    @JvmInline
    @Serializable
    value class BasicValueClass(val value: String)

    @JvmInline
    @Serializable
    value class AnnotatedValueClass(@JsonSchema.Pattern("valueclass") val value: String)

    @Serializable
    data class AnnotatedDataClass(@JsonSchema.Pattern("dataclass") val value: String)

    @Serializable
    data class Container(
        @JsonSchema.Pattern("field")
        val basic: BasicValueClass = BasicValueClass("field"),
        // No annotation here, as it's already in the value
        val annotatedValueClass: AnnotatedValueClass = AnnotatedValueClass("valueclass"),

        // Double annotation = to avoid re-use, adding this annotation actually overrides the one in the value class
        @JsonSchema.Pattern("override-valueclass")
        val doubleAnnotationValueClass: AnnotatedValueClass = AnnotatedValueClass("override-valueclass"),

        // No annotation here, as it's already in the value
        val annotatedDataClass: AnnotatedDataClass = AnnotatedDataClass("dataclass"),

        // Annotation is not relevant here, type=object doesn't have pattern
        // Also it's not a good idea to validate on it since the pattern will be applied on the serialized version of the class
        // Could be a warning to avoid surprises.
        @JsonSchema.Pattern("ignored")
        val doubleAnnotationDataClass: AnnotatedDataClass = AnnotatedDataClass("dataclass"),
    )

    @Test
    fun validation() {
        myGlobalJson.jsonFormatValidator(Container()) {
            it::basic.validateOrThrow()
            it.basic::value.validateOrThrow()
            it::annotatedValueClass.validateOrThrow()
            it.annotatedValueClass::value.validateOrThrow()
            it::doubleAnnotationValueClass.validateOrThrow()
            it.doubleAnnotationValueClass::value.validateOrThrow()
            it::annotatedDataClass.validateOrThrow()
            it.annotatedDataClass::value.validateOrThrow()
            // Non-sense to validate a Pattern on a class, as it will check against the serialized version.
            //it::doubleAnnotationDataClass.validateOrThrow()
            it.doubleAnnotationDataClass::value.validateOrThrow()
        }
    }

    @Test
    fun checkInheritance() {
        assertEquals(
            """
                {
                  "basic": "field",
                  "annotatedValueClass": "valueclass",
                  "doubleAnnotationValueClass": "override-valueclass",
                  "annotatedDataClass": {
                    "value": "dataclass"
                  },
                  "doubleAnnotationDataClass": {
                    "value": "dataclass"
                  }
                }
            """.trimIndent(),
            myGlobalJson.encodeToString(Container())
        )

        assertEquals(
            """
                {
                  "${"$"}schema": "http://json-schema.org/draft-07/schema",
                  "title": "AnnotationInheritanceTest.Container",
                  "additionalProperties": false,
                  "type": "object",
                  "properties": {
                    "basic": {
                      "type": "string",
                      "pattern": "field"
                    },
                    "annotatedValueClass": {
                      "type": "string",
                      "pattern": "valueclass"
                    },
                    "doubleAnnotationValueClass": {
                      "type": "string",
                      "pattern": "override-valueclass"
                    },
                    "annotatedDataClass": {
                      "additionalProperties": false,
                      "type": "object",
                      "properties": {
                        "value": {
                          "type": "string",
                          "pattern": "dataclass"
                        }
                      },
                      "required": [
                        "value"
                      ]
                    },
                    "doubleAnnotationDataClass": {
                      "additionalProperties": false,
                      "type": "object",
                      "properties": {
                        "value": {
                          "type": "string",
                          "pattern": "dataclass"
                        }
                      },
                      "required": [
                        "value"
                      ]
                    }
                  },
                  "required": [
                    "basic",
                    "annotatedValueClass",
                    "doubleAnnotationValueClass",
                    "annotatedDataClass",
                    "doubleAnnotationDataClass"
                  ]
                }
            """.trimIndent(),
            myGlobalJson.encodeToSchema(Container.serializer(), false)
        )
    }
}