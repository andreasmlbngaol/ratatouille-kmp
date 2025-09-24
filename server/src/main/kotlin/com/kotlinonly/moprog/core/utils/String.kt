package com.kotlinonly.moprog.core.utils

fun String.uppercaseEachWord() = this
    .split(' ')
    .joinToString(" ") {
        it.replaceFirstChar { char -> char.uppercaseChar() }
    }