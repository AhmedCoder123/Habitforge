package com.example

import com.example.data.model.HabitFrequencyType
import com.example.util.KarachiTimeHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class ExampleUnitTest {
  @Test
  fun testCountdownFormatting() {
    val formatted = KarachiTimeHelper.formatCountdown(3665)
    assertEquals("01h 01m 05s", formatted)
  }

  @Test
  fun testRecurringIntervalScheduling() {
    val anchor = KarachiTimeHelper.ANCHOR_DATE
    // Anchor date diff is 0 -> scheduled
    assertTrue(KarachiTimeHelper.isHabitScheduledForDate(HabitFrequencyType.EVERY_N_DAYS, 3, 0, anchor))
    // Anchor + 1 day -> not scheduled
    assertFalse(KarachiTimeHelper.isHabitScheduledForDate(HabitFrequencyType.EVERY_N_DAYS, 3, 0, anchor.plusDays(1)))
    // Anchor + 2 days -> not scheduled
    assertFalse(KarachiTimeHelper.isHabitScheduledForDate(HabitFrequencyType.EVERY_N_DAYS, 3, 0, anchor.plusDays(2)))
    // Anchor + 3 days -> scheduled!
    assertTrue(KarachiTimeHelper.isHabitScheduledForDate(HabitFrequencyType.EVERY_N_DAYS, 3, 0, anchor.plusDays(3)))
  }
}
