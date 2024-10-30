package dev.alexzvn.universalyogaplus.util

import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

fun Date.toLocal(): LocalDate = this.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()

fun LocalDate.format(pattern: String) = DateTimeFormatter.ofPattern(pattern).format(this)

fun Long.asLocalDate(): LocalDate = Date(this).toLocal()