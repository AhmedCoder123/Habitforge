package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.HabitFrequencyType
import com.example.util.KarachiTimeHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("HabitForge PKT", appName)
  }

  @Test
  fun `karachi timezone has plus 5 offset`() {
    val zone = KarachiTimeHelper.KARACHI_ZONE
    assertEquals("Asia/Karachi", zone.id)
  }

  @Test
  fun `weekend running scheduled on Saturday and Sunday only`() {
    val saturday = LocalDate.of(2026, 9, 26) // Saturday
    val sunday = LocalDate.of(2026, 9, 27) // Sunday
    val monday = LocalDate.of(2026, 9, 28) // Monday

    assertTrue(KarachiTimeHelper.isHabitScheduledForDate(HabitFrequencyType.WEEKEND_ONLY, 1, 0, saturday))
    assertTrue(KarachiTimeHelper.isHabitScheduledForDate(HabitFrequencyType.WEEKEND_ONLY, 1, 0, sunday))
    assertFalse(KarachiTimeHelper.isHabitScheduledForDate(HabitFrequencyType.WEEKEND_ONLY, 1, 0, monday))
  }
}
