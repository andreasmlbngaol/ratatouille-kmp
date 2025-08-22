package com.kotlinonly.moprog.data.core

import android.util.Log

actual fun logD(tag: String, message: String) {
    Log.d(tag, message)
}

actual fun logE(tag: String, message: String) {
    Log.e(tag, message)
}

actual fun logI(tag: String, message: String) {
    Log.i(tag, message)
}

actual fun logV(tag: String, message: String) {
    Log.v(tag, message)
}

actual fun logW(tag: String, message: String) {
    Log.w(tag, message)
}
