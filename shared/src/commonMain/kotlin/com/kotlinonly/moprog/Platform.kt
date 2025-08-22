package com.kotlinonly.moprog

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform