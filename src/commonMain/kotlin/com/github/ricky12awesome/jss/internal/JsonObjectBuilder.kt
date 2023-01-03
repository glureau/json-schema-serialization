package com.github.ricky12awesome.jss.internal

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

internal class JsonObjectBuilder(
    val content: MutableMap<String, JsonElement> = linkedMapOf()
) : MutableMap<String, JsonElement> by content {
    operator fun set(key: String, value: Iterable<String?>) = set(key, JsonArray(value.map(::JsonPrimitive)))
    operator fun set(key: String, value: String?) = set(key, JsonPrimitive(value))
    operator fun set(key: String, value: Number?) = set(key, JsonPrimitive(value))
    operator fun set(key: String, value: Boolean?) = set(key, JsonPrimitive(value))
}