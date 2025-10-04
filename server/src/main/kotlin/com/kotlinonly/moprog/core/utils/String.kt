package com.kotlinonly.moprog.core.utils

fun String.uppercaseEachWord() = this
    .lowercase()
    .split(' ')
    .joinToString(" ") {
        it.replaceFirstChar { char -> char.uppercaseChar() }
    }