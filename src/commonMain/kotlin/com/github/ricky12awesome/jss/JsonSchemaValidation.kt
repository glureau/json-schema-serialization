package com.github.ricky12awesome.jss

import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.elementNames
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.reflect.KProperty0

public class JsonSchemaValidationException(name: String, encodedValue: String, ajvFormatRegex: Regex) :
    Exception(
        "Cannot validate the field '$name' with value '$encodedValue'\n" +
                "regex:$ajvFormatRegex"
    )

@OptIn(ExperimentalJsonSchemaValidation::class)
public class JsonFormatValidator @PublishedApi internal constructor(
    @PublishedApi
    internal val descriptor: SerialDescriptor,
    @PublishedApi
    internal val json: Json
) {
    @Throws(JsonSchemaValidationException::class)
    inline fun <reified F> KProperty0<F>.validateOrThrow() =
        json.jsonFormatValidatorInternal(descriptor, this).getOrThrow()

    inline fun <reified F> KProperty0<F>.validate() = json.jsonFormatValidatorInternal(descriptor, this)
}

public inline fun <reified T> Json.jsonFormatValidator(target: T, validation: JsonFormatValidator.(target: T) -> Unit) {
    JsonFormatValidator(serializer<T>().descriptor, this).apply {
        validation(target)
    }
}

@ExperimentalJsonSchemaValidation
public inline fun <reified T> Json.jsonFormatValidator(field: KProperty0<String>): Result<Unit> =
    jsonFormatValidatorInternal(serializer<T>().descriptor, field)

@ExperimentalJsonSchemaValidation
@PublishedApi
internal inline fun <reified F> Json.jsonFormatValidatorInternal(
    descriptor: SerialDescriptor,
    field: KProperty0<F>
): Result<Unit> = runCatching {
    val index = descriptor.elementNames.indexOf(field.name)
    if (index < 0) return@runCatching

    val validationRegex = findValidationRegex(descriptor.getElementAnnotations(index))
        ?: return@runCatching // No regex = Success
    val value = field.get() ?: return@runCatching
    // Nullable = no check
    val encodedValue = encodeToString(value).removeSurrounding("\"")
    if (validationRegex.matches(encodedValue)) return@runCatching

    throw JsonSchemaValidationException(
        field.name,
        encodedValue,
        validationRegex
    )
}

fun findValidationRegex(
    annotations: List<Annotation>,
): Regex? {
    val annotationPattern = annotations.filterIsInstance<JsonSchema.Pattern>().firstOrNull()
    if (annotationPattern != null) return Regex(annotationPattern.pattern)

    val annotationFormat = annotations.filterIsInstance<JsonSchema.Format>().firstOrNull()
    return annotationFormat?.format?.ajvFormatRegex
}
