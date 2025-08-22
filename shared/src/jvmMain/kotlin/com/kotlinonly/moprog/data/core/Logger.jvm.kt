package com.kotlinonly.moprog.data.core

actual fun logD(tag: String, message: String) {
    println("$tag: $message")
}

actual fun logE(tag: String, message: String) {
    System.err.println("$tag: $message")
}

actual fun logI(tag: String, message: String) {
    println("$tag: $message")
}

actual fun logV(tag: String, message: String) {
    println("$tag: $message")
}

actual fun logW(tag: String, message: String) {
    println("$tag: $message")
}
