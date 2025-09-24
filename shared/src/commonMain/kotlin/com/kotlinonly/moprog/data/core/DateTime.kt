package com.kotlinonly.moprog.data.core

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
val now
    get() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())