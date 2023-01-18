package com.github.ricky12awesome.jss

// Source ajv-formats:
// https://github.com/ajv-validator/ajv-formats/blob/master/src/formats.ts
enum class JsonFormat(val jsonSchemaFormat: String, val ajvFormatRegex: Regex? = null) {
    // date: http://tools.ietf.org/html/rfc3339#section-5.6
    date("date", Regex("^\\d\\d\\d\\d-[0-1]\\d-[0-3]\\d\$", RegexOption.IGNORE_CASE)),

    // date-time: http://tools.ietf.org/html/rfc3339#section-5.6
    time("time", Regex("^(?:[0-2]\\d:[0-5]\\d:[0-5]\\d|23:59:60)(?:\\.\\d+)?(?:z|[+-]\\d\\d(?::?\\d\\d)?)\$", RegexOption.IGNORE_CASE)),
    dateTime("date-time", Regex("^\\d\\d\\d\\d-[0-1]\\d-[0-3]\\dt(?:[0-2]\\d:[0-5]\\d:[0-5]\\d|23:59:60)(?:\\.\\d+)?(?:z|[+-]\\d\\d(?::?\\d\\d)?)\$", RegexOption.IGNORE_CASE)),
    isoTime(
        "iso-time",
        Regex(
            "^(?:[0-2]\\d:[0-5]\\d:[0-5]\\d|23:59:60)(?:\\.\\d+)?(?:z|[+-]\\d\\d(?::?\\d\\d)?)?\$",
            RegexOption.IGNORE_CASE
        )
    ),
    isoDateTime(
        "iso-date-time",
        Regex(
            "^\\d\\d\\d\\d-[0-1]\\d-[0-3]\\d[t\\s](?:[0-2]\\d:[0-5]\\d:[0-5]\\d|23:59:60)(?:\\.\\d+)?(?:z|[+-]\\d\\d(?::?\\d\\d)?)?\$",
            RegexOption.IGNORE_CASE
        )
    ),

    // duration: https://tools.ietf.org/html/rfc3339#appendix-A
    duration("duration", Regex("^P(?!\$)((\\d+Y)?(\\d+M)?(\\d+D)?(T(?=\\d)(\\d+H)?(\\d+M)?(\\d+S)?)?|(\\d+W)?)\$")),
    uri("uri"),
    uriReference("uri-reference"),
    uriTemplate("uri-template"),
    url("url"),
    email(
        "email",
        Regex(
            "^[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\$",
            RegexOption.IGNORE_CASE
        )
    ),
    hostname("hostname"),
    ipv4("ipv4", Regex("^(?:(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\$")),
    ipv6(
        "ipv6", Regex(
            "^((([0-9a-f]{1,4}:){7}([0-9a-f]{1,4}|:))|(([0-9a-f]{1,4}:){6}(:[0-9a-f]{1,4}|((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9a-f]{1,4}:){5}(((:[0-9a-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9a-f]{1,4}:){4}(((:[0-9a-f]{1,4}){1,3})|((:[0-9a-f]{1,4})?:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9a-f]{1,4}:){3}(((:[0-9a-f]{1,4}){1,4})|((:[0-9a-f]{1,4}){0,2}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9a-f]{1,4}:){2}(((:[0-9a-f]{1,4}){1,5})|((:[0-9a-f]{1,4}){0,3}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9a-f]{1,4}:){1}(((:[0-9a-f]{1,4}){1,6})|((:[0-9a-f]{1,4}){0,4}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(:(((:[0-9a-f]{1,4}){1,7})|((:[0-9a-f]{1,4}){0,5}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:)))\$",
            RegexOption.IGNORE_CASE
        )
    ),
    uuid(
        "uuid", Regex(
            "^(?:urn:uuid:)?[0-9a-f]{8}-(?:[0-9a-f]{4}-){3}[0-9a-f]{12}\$",
            RegexOption.IGNORE_CASE
        )
    ),
    jsonPointer("json-pointer", Regex("^(?:\\/(?:[^~/]|~0|~1)*)*\$")),
    jsonPointerUriFragment(
        "json-pointer-uri-fragment",
        Regex(
            "^#(?:\\/(?:[a-z0-9_\\-.!\$&'()*+,;:=@]|%[0-9a-f]{2}|~0|~1)*)*\$",
            RegexOption.IGNORE_CASE
        )
    ),

    // relative JSON-pointer: http://tools.ietf.org/html/draft-luff-relative-json-pointer-00
    relativeJsonPointer("relative-json-pointer", Regex("^(?:0|[1-9][0-9]*)(?:#|(?:\\/(?:[^~/]|~0|~1)*)*)\$")),

    // the following formats are used by the openapi specification: https://spec.openapis.org/oas/v3.0.0#data-types
    // byte: https://github.com/miguelmota/is-base64
    byte("byte"),

    number("number"),
    password("password"),

    // unchecked string payload
    binary("binary")
}