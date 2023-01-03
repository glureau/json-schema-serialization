import com.github.ricky12awesome.jss.JsonSchema
import com.github.ricky12awesome.jss.JsonSchema.*
import com.github.ricky12awesome.jss.dsl.*
import com.github.ricky12awesome.jss.encodeToSchema
import com.github.ricky12awesome.jss.globalJson
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


@Serializable
private data class Config(
    @Description(arrayOf("Name for this project."))
    val name: String = "",
    @Description(arrayOf("Theme for this project."))
    val theme: Theme = Theme()
)

private interface ColorSpaceWithHue {
    val h: Int
}

@Serializable
private sealed interface ThemeColor {
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
private data class Theme(
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

// Stuff: because many other details are still in this class for now...
class PolymorphismAndStuffTest {
    val json = Json(globalJson) {
        classDiscriminator = "classDiscriminator"
    }

    @Test
    fun annotated_schema() {
        println(json.encodeToSchema(Config.serializer(), false))
    }

    @Test
    fun serialName() {
        assertEquals(
            json.encodeToSchema(Foo.Baz.serializer(), false, exposeClassDiscriminator = true),
            """
                {
                  "${"$"}schema": "http://json-schema.org/draft-07/schema",
                  "additionalProperties": false,
                  "type": "object",
                  "properties": {
                    "classDiscriminator": "Bazzz"
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
                  "additionalProperties": false,
                  "type": "object",
                  "properties": {
                    "classDiscriminator": "Bim"
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
}
