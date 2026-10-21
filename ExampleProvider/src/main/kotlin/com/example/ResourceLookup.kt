package com.example

/** Android returns zero for an unknown resource; zero must never reach a resource loader. */
internal fun resolveResourceId(
    name: String,
    type: String,
    lookup: (String, String) -> Int?
): Int? = lookup(name, type)?.takeIf { it != 0 }
