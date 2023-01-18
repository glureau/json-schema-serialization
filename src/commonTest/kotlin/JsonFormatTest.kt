import com.github.ricky12awesome.jss.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.serializer
import kotlin.jvm.JvmInline
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.days

@OptIn(ExperimentalTime::class, ExperimentalJsonSchemaValidation::class)
class JsonFormatTest {

    @JvmInline
    @Serializable
    @JsonSchema.Format(JsonFormat.uuid)
    value class MyUUID(val uuid: String)

    @Serializable
    data class JsonFormatSerialized(
        @JsonSchema.Format(JsonFormat.duration)
        val duration: Duration = 3.days + 45.seconds,
        @JsonSchema.Format(JsonFormat.email)
        val email: String = "johndoe@gmail.com",
        @JsonSchema.Format(JsonFormat.ipv4)
        val ipv4: String = "127.0.0.1",
        @JsonSchema.Format(JsonFormat.ipv6)
        val ipv6: String = "2001:0db8:0001:0000:0000:0ab9:C0A8:0102",
        @JsonSchema.Format(JsonFormat.uuid)
        val uuid: String = "123e4567-e89b-12d3-a456-426614174000",
        @JsonSchema.Pattern("^A[0-9]+$")
        val code: String = "A001",
        @JsonSchema.Format(JsonFormat.dateTime)
        val date: Instant? = Clock.System.now(),
        val myUUID: MyUUID = MyUUID("hop"),
    )

    @Test
    fun validateField() {
        val original = JsonFormatSerialized()
        assertTrue(myGlobalJson.formatValidator<JsonFormatSerialized>(original::uuid).isSuccess)

        val bad = JsonFormatSerialized(uuid = "BadValueForUUID")
        val result = myGlobalJson.formatValidator<JsonFormatSerialized>(bad::uuid)
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is JsonSchemaValidationException)
        assertEquals(
            exception.message, """
            Cannot validate the field 'uuid' with value 'BadValueForUUID'
            regex:^(?:urn:uuid:)?[0-9a-f]{8}-(?:[0-9a-f]{4}-){3}[0-9a-f]{12}${'$'}
        """.trimIndent()
        )
    }

    @Test
    fun validatePattern() {
        val bad = JsonFormatSerialized(code = "BadCode")
        val result = myGlobalJson.formatValidator<JsonFormatSerialized>(bad::code)
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is JsonSchemaValidationException)
        assertEquals(
            exception.message, """
            Cannot validate the field 'code' with value 'BadCode'
            regex:^A[0-9]+${'$'}
        """.trimIndent()
        )
    }

    @Test
    fun validateInstant() {
        // ISO 8601 expects 4 digits for the year, so we maxout this to ensure it's not accepted
        val bad = JsonFormatSerialized(date = Instant.fromEpochMilliseconds(Long.MAX_VALUE))
        myGlobalJson.formatValidator(bad) {
            val result = bad::date.validateResult()
            assertTrue(result.isFailure)
            val exception = result.exceptionOrNull()
            assertTrue(exception is JsonSchemaValidationException)
            assertEquals(
                expected = """
                            Cannot validate the field 'date' with value '+292278994-08-17T07:12:55.807Z'
                            regex:^\d\d\d\d-[0-1]\d-[0-3]\dt(?:[0-2]\d:[0-5]\d:[0-5]\d|23:59:60)(?:\.\d+)?(?:z|[+-]\d\d(?::?\d\d)?)${'$'}
                        """.trimIndent(),
                actual = exception.message
            )
        }
    }

    @Test
    fun validateNullableWhenNull() {
        val bad = JsonFormatSerialized(date = null)
        myGlobalJson.formatValidator(bad) {
            val result = bad::date.validateResult()
            assertTrue(result.isSuccess)
        }
    }

    @Test
    fun validateClass() {
        val original = JsonFormatSerialized()
        myGlobalJson.formatValidator(original) {
            it::duration.validateOrThrow()
            it::email.validateOrThrow()
            it::ipv4.validateOrThrow()
            it::ipv6.validateOrThrow()
            it::uuid.validateOrThrow()
            it::code.validateOrThrow()
            it::date.validateOrThrow()
        }

        val bad = JsonFormatSerialized(uuid = "BadValueForUUID")
        myGlobalJson.formatValidator(bad) {
            val result = it::uuid.validateResult()
            assertTrue(result.isFailure)
            val exception = result.exceptionOrNull()
            assertTrue(exception is JsonSchemaValidationException)
            assertEquals(
                exception.message, """
            Cannot validate the field 'uuid' with value 'BadValueForUUID'
            regex:^(?:urn:uuid:)?[0-9a-f]{8}-(?:[0-9a-f]{4}-){3}[0-9a-f]{12}${'$'}
        """.trimIndent()
            )
        }
    }

    @Test
    fun allFormats() {
        val original = JsonFormatSerialized()
        val jsonLines = myGlobalJson.encodeToString(original).split("\n").map { it.trim() }
        jsonLines.forEach { println(it) }
        val jsonMap =
            jsonLines.map {
                it.split(": ", limit = 2)
                    .map {
                        it.trim()
                            .removeSuffix(",")
                            .removeSurrounding("\"")
                    }
            }

        fun getValue(key: String): String = jsonMap.first { keyToValue -> keyToValue[0] == key }[1]
        fun assertMatches(key: String, jsonFormat: JsonFormat, expectedValue: String? = null) {
            val value = getValue(key)
            println("$key=$value")
            if (expectedValue != null) assertEquals(expectedValue, value)
            println("MATCH ${jsonFormat.ajvFormatRegex!!.matches(value)} REGEX=${jsonFormat.ajvFormatRegex} VALUE=$value")
            assertTrue(jsonFormat.ajvFormatRegex!!.matches(value))
        }

        assertMatches("duration", JsonFormat.duration, "PT72H0M45S")
        assertMatches("email", JsonFormat.email)
        assertMatches("ipv4", JsonFormat.ipv4)
        assertMatches("ipv6", JsonFormat.ipv6)
        assertMatches("uuid", JsonFormat.uuid)
    }

    // POC for the future, need a reflection mechanism to get the value associated with the descriptor.
    @ExperimentalJsonSchemaValidation
    inline fun <reified T> Any?.validateJsonFormat() {
        val descriptor = serializer<T>().descriptor
        for (i in 0..descriptor.elementsCount) {
            validate(this, descriptor.getElementDescriptor(i), descriptor.getElementAnnotations(i))
        }
    }

    @ExperimentalJsonSchemaValidation
    fun validate(any: Any?, elementDescriptor: SerialDescriptor, elementAnnotations: List<Annotation>) {
        if (elementDescriptor.kind is PrimitiveKind) {
            if (elementDescriptor.kind == PrimitiveKind.STRING) {
                val jsonFormat = elementAnnotations.filterIsInstance<JsonFormat>().firstOrNull() ?: return
                // If we find something to get the value from the object without too much expect/actual
                // we could provide an auto-validation mechanism.
                jsonFormat.ajvFormatRegex?.matches(TODO())
            } // else do nothing
        } else {
            for (i in 0..elementDescriptor.elementsCount) {
                //elementDescriptor.getElementDescriptor(i)
                TODO("recursive call to validate() method, need to get the value associated to the serializer")
            }
        }
    }
}
