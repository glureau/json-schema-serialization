package com.github.ricky12awesome.jss

import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor

/**
 * A standard PrimitiveSerialDescriptor with PrimitiveKind.STRING but
 * compatible with jsonschema generator to define a list of possible values.
 */
public fun StringEnumSerialDescriptor(serialName: String, possibleValues: Array<String>): SerialDescriptor {
    return InternalStringEnumSerialDescriptor(
        delegatedDescriptor = PrimitiveSerialDescriptor(serialName = serialName, kind = PrimitiveKind.STRING),
        possibleValues = possibleValues
    )
}

// An internal holder for all possible values.
internal class InternalStringEnumSerialDescriptor(
    delegatedDescriptor: SerialDescriptor,
    val possibleValues: Array<String>
) : SerialDescriptor by delegatedDescriptor
