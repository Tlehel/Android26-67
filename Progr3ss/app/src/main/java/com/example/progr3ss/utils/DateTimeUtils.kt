package com.example.progr3ss.utils

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// Helyi idő -> UTC szöveg, pl. 2026-11-07T16:00:00Z
fun LocalDateTime.toUtcIsoString(): String {
    val instant = this.atZone(ZoneId.systemDefault()).toInstant()
    return DateTimeFormatter.ISO_INSTANT.format(instant)
}