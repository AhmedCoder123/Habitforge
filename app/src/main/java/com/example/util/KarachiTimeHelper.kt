package com.example.util

import com.example.data.model.HabitFrequencyType
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

object KarachiTimeHelper {
    val KARACHI_ZONE: ZoneId = ZoneId.of("Asia/Karachi")
    val ANCHOR_DATE: LocalDate = LocalDate.of(2026, 1, 1)

    private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US)
    private val DISPLAY_DATE_FORMATTER = DateTimeFormatter.ofPattern("EEEE, MMM d, yyyy", Locale.US)
    private val DISPLAY_TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm:ss a", Locale.US)
    private val SHORT_TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a", Locale.US)

    fun nowInKarachi(): ZonedDateTime {
        return ZonedDateTime.now(KARACHI_ZONE)
    }

    fun todayKarachiDate(): LocalDate {
        return LocalDate.now(KARACHI_ZONE)
    }

    fun todayKarachiKey(): String {
        return todayKarachiDate().format(DATE_FORMATTER)
    }

    fun formatDateKey(date: LocalDate): String {
        return date.format(DATE_FORMATTER)
    }

    fun parseDateKey(key: String): LocalDate {
        return try {
            LocalDate.parse(key, DATE_FORMATTER)
        } catch (e: Exception) {
            todayKarachiDate()
        }
    }

    fun nextKarachiMidnight(): ZonedDateTime {
        return todayKarachiDate().plusDays(1).atStartOfDay(KARACHI_ZONE)
    }

    fun secondsUntilKarachiMidnight(): Long {
        val now = nowInKarachi()
        val midnight = nextKarachiMidnight()
        return Duration.between(now, midnight).seconds.coerceAtLeast(0)
    }

    fun formatCountdown(totalSeconds: Long): String {
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.US, "%02dh %02dm %02ds", hours, minutes, seconds)
    }

    fun formatDisplayDate(date: LocalDate): String {
        return date.format(DISPLAY_DATE_FORMATTER)
    }

    fun formatDisplayTime(zdt: ZonedDateTime): String {
        return zdt.format(DISPLAY_TIME_FORMATTER)
    }

    fun formatShortTime(zdt: ZonedDateTime): String {
        return zdt.format(SHORT_TIME_FORMATTER)
    }

    /**
     * Determines whether a habit should appear for a given date in Karachi.
     */
    fun isHabitScheduledForDate(
        frequencyType: HabitFrequencyType,
        intervalDays: Int,
        daysOfWeekMask: Int,
        date: LocalDate
    ): Boolean {
        return when (frequencyType) {
            HabitFrequencyType.DAILY -> true
            HabitFrequencyType.WEEKEND_ONLY -> {
                date.dayOfWeek == DayOfWeek.SATURDAY || date.dayOfWeek == DayOfWeek.SUNDAY
            }
            HabitFrequencyType.EVERY_N_DAYS -> {
                val interval = if (intervalDays <= 0) 3 else intervalDays
                val daysDiff = ChronoUnit.DAYS.between(ANCHOR_DATE, date)
                (daysDiff % interval) == 0L
            }
            HabitFrequencyType.WEEKLY -> {
                // Weekly haircut appears on Sunday or every 7 days from anchor
                val daysDiff = ChronoUnit.DAYS.between(ANCHOR_DATE, date)
                (daysDiff % 7L) == 0L || date.dayOfWeek == DayOfWeek.SUNDAY
            }
            HabitFrequencyType.SPECIFIC_DAYS -> {
                val dayBit = 1 shl (date.dayOfWeek.value - 1)
                (daysOfWeekMask and dayBit) != 0
            }
        }
    }
}
