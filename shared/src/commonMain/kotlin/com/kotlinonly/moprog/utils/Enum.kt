package com.kotlinonly.moprog.utils

inline fun <reified T : Enum<T>> enumValueOfOrNull(name: String?): T? =
    try {
        if (name == null) null else enumValueOf<T>(name)
    } catch (_: IllegalArgumentException) {
        println("Failed to convert $name to enum ${T::class.simpleName}")
        null
    }
