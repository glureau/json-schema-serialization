package com.github.ricky12awesome.jss

import kotlinx.serialization.descriptors.elementNames
import kotlinx.serialization.serializer
import kotlin.reflect.KProperty0

public class JsonSchemaValidationException(name: String, get: String, format: JsonFormat, ajvFormatRegex: Regex) :
    Exception(
        "Cannot validate the field '$name' with value '$get', annotated format $format\n" +
                "regex:$ajvFormatRegex"
    )

@ExperimentalJsonSchemaValidation
public inline fun <reified T> jsonFormatValidator(field: KProperty0<String>): Result<Unit> = runCatching {
    val descriptor = serializer<T>().descriptor
    val index = descriptor.elementNames.indexOf(field.name)
    println("index=$index")
    if (index < 0) return@runCatching
    println("descriptor=$descriptor")
    println("descriptor=${descriptor.annotations}")
    println("annotations = ${descriptor.getElementAnnotations(index)}")
    val annotation = descriptor.getElementAnnotations(index)
        .filterIsInstance<JsonSchema.Format>().firstOrNull() ?: return@runCatching

    println(annotation)
    annotation.format.ajvFormatRegex ?: return@runCatching

    println(field.get())
    println(annotation.format.ajvFormatRegex.matches(field.get()))
    if (annotation.format.ajvFormatRegex.matches(field.get())) return@runCatching

    throw JsonSchemaValidationException(
        field.name,
        field.get(),
        annotation.format,
        annotation.format.ajvFormatRegex
    )
}
