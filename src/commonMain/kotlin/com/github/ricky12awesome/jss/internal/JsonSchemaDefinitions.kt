package com.github.ricky12awesome.jss.internal

import com.github.ricky12awesome.jss.JsonSchema
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.json.JsonObject

internal class JsonSchemaDefinitions(private val isEnabled: Boolean = true) {
    private val definitions: MutableMap<String, JsonObject> = mutableMapOf()
    private val creator: MutableMap<String, () -> JsonObject> = mutableMapOf()

    fun getId(key: Key): String {
        val (descriptor, annotations) = key

        return annotations
            .lastOfInstance<JsonSchema.Definition>()?.id
            ?.takeIf(String::isNotEmpty)
            ?: (descriptor.hashCode().toLong() shl 32 xor annotations.hashCode().toLong())
                .toString(36)
                .replaceFirst("-", "x")
    }

    fun canGenerateDefinitions(key: Key): Boolean {
        return key.annotations.any {
            it !is JsonSchema.NoDefinition && it is JsonSchema.Definition
        }
    }

    operator fun contains(key: Key): Boolean = getId(key) in definitions

    operator fun set(key: Key, value: JsonObject) {
        definitions[getId(key)] = value
    }

    operator fun get(key: Key): JsonObject {
        val id = getId(key)

        return key.descriptor.jsonSchemaElement(key.annotations, skipNullCheck = true, skipTypeCheck = true, extra = {
            it["\$ref"] = "#/definitions/$id"
        }, additionalProperties = false)
    }

    fun get(key: Key, create: () -> JsonObject): JsonObject {
        if (!isEnabled && !canGenerateDefinitions(key)) return create()

        val id = getId(key)

        if (id !in definitions) {
            creator[id] = create
        }

        return get(key)
    }

    fun getDefinitionsAsJsonObject(): JsonObject {
        while (creator.isNotEmpty()) {
            creator.toList().forEach { (id, create) ->
                definitions[id] = create()
                creator.remove(id)
            }
        }

        return JsonObject(definitions)
    }

    data class Key(val descriptor: SerialDescriptor, val annotations: List<Annotation>)
}