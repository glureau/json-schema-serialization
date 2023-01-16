package com.github.ricky12awesome.jss

import kotlinx.serialization.json.JsonPrimitive

/**
 * Represents the type of a json type
 */
public enum class JsonType(jsonType: String) {
    /**
     * Represents the json array type
     */
    ARRAY("array"),

    /**
     * Represents the json number type
     */
    NUMBER("number"),

    /**
     * Represents the string type
     */
    STRING("string"),

    /**
     * Represents the boolean type
     */
    BOOLEAN("boolean"),

    /**
     * Represents the object type, this is used for serializing normal classes
     */
    OBJECT("object"),

    /**
     * Represents the object type, this is used for serializing sealed classes
     */
    OBJECT_SEALED("object"),

    /**
     * Represents the object type, this is used for serializing maps
     */
    OBJECT_MAP("object");

    val json = JsonPrimitive(jsonType)

    override fun toString(): String = json.content
}