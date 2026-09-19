package de.einsatzlog.app.core.format

import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.char
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime

private val dateTimeFormat = LocalDateTime.Format {
    day(); char('.'); monthNumber(); char('.'); year()
    char(','); char(' ')
    hour(); char(':'); minute()
}

private val timeFormat = LocalDateTime.Format {
    hour(); char(':'); minute()
}

@OptIn(ExperimentalTime::class)
private fun Long.toLocal(): LocalDateTime =
    Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.currentSystemDefault())

/** "18.07.2026, 14:35" — device timezone. */
fun formatDateTime(epochMs: Long): String = dateTimeFormat.format(epochMs.toLocal())

/** "14:35" — tabular time for the entry stream. */
fun formatTime(epochMs: Long): String = timeFormat.format(epochMs.toLocal())
