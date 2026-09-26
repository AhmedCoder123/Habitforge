package com.example.data.repository

import com.example.data.local.DailySummaryEntity
import com.example.data.local.HabitDao
import com.example.data.local.HabitEntity
import com.example.data.local.HabitLogEntity
import com.example.data.model.HabitCategory
import com.example.data.model.HabitFrequencyType
import com.example.data.model.HabitInputType
import com.example.data.model.HabitWithProgress
import com.example.util.KarachiTimeHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import java.time.LocalDate
import kotlin.random.Random

class HabitRepository(
    private val habitDao: HabitDao,
    private val firestoreSyncRepo: com.example.data.firebase.FirestoreHabitSyncRepository? = null
) {

    val allHabits: Flow<List<HabitEntity>> = habitDao.getAllHabits()

    suspend fun initializeIfNeeded() = withContext(Dispatchers.IO) {
        val count = habitDao.getHabitsCount()
        if (count == 0) {
            seedDefaultHabits()
        }
        val todayKey = KarachiTimeHelper.todayKarachiKey()
        ensureDailySummaryExists(todayKey)
        seedRolling30DaysIfEmpty()
    }

    private suspend fun seedDefaultHabits() {
        val defaults = listOf(
            // 1. Sleep 7hrs daily
            HabitEntity(
                title = "Sleep 7hrs daily",
                description = "Consistent restorative 7+ hours sleep",
                category = HabitCategory.CORE_HEALTH,
                frequencyType = HabitFrequencyType.DAILY,
                intervalDays = 1,
                inputType = HabitInputType.CHECKBOX,
                targetCount = 1,
                unit = "night",
                isStandard = true,
                iconName = "bed",
                reminderTime = "23:00"
            ),
            // 2. Daily macros fulfill
            HabitEntity(
                title = "Daily macros fulfill",
                description = "Hit daily protein, carb, and healthy fat targets",
                category = HabitCategory.CORE_HEALTH,
                frequencyType = HabitFrequencyType.DAILY,
                intervalDays = 1,
                inputType = HabitInputType.CHECKBOX,
                targetCount = 1,
                unit = "goal",
                isStandard = true,
                iconName = "nutrition",
                reminderTime = "20:30"
            ),
            // 3. Drink 3 litres water a day
            HabitEntity(
                title = "Drink 3 litres water a day",
                description = "Hydration goal: 3 liters (1L increments)",
                category = HabitCategory.CORE_HEALTH,
                frequencyType = HabitFrequencyType.DAILY,
                intervalDays = 1,
                inputType = HabitInputType.COUNTER,
                targetCount = 3,
                unit = "Litres",
                isStandard = true,
                iconName = "water",
                reminderTime = "14:00"
            ),
            // 4. Brush teeth twice daily
            HabitEntity(
                title = "Brush teeth twice daily",
                description = "Morning and bedtime oral hygiene",
                category = HabitCategory.GROOMING,
                frequencyType = HabitFrequencyType.DAILY,
                intervalDays = 1,
                inputType = HabitInputType.COUNTER,
                targetCount = 2,
                unit = "times",
                isStandard = true,
                iconName = "smile",
                reminderTime = "22:30"
            ),
            // 5. Face wash twice at least
            HabitEntity(
                title = "Face wash twice at least",
                description = "Morning cleanse & evening skincare routine",
                category = HabitCategory.GROOMING,
                frequencyType = HabitFrequencyType.DAILY,
                intervalDays = 1,
                inputType = HabitInputType.COUNTER,
                targetCount = 2,
                unit = "times",
                isStandard = true,
                iconName = "face",
                reminderTime = "22:15"
            ),
            // 6. Shower daily (once atleast)
            HabitEntity(
                title = "Shower daily (once atleast)",
                description = "Daily refreshing personal hygiene shower",
                category = HabitCategory.GROOMING,
                frequencyType = HabitFrequencyType.DAILY,
                intervalDays = 1,
                inputType = HabitInputType.CHECKBOX,
                targetCount = 1,
                unit = "shower",
                isStandard = true,
                iconName = "shower",
                reminderTime = "08:30"
            ),
            // 7. Daily Gym
            HabitEntity(
                title = "Daily Gym",
                description = "Strength training or resistance workout",
                category = HabitCategory.CORE_HEALTH,
                frequencyType = HabitFrequencyType.DAILY,
                intervalDays = 1,
                inputType = HabitInputType.CHECKBOX,
                targetCount = 1,
                unit = "session",
                isStandard = true,
                iconName = "fitness",
                reminderTime = "17:30"
            ),
            // 8. Creatine daily
            HabitEntity(
                title = "Creatine daily",
                description = "5g creatine monohydrate supplementation",
                category = HabitCategory.CORE_HEALTH,
                frequencyType = HabitFrequencyType.DAILY,
                intervalDays = 1,
                inputType = HabitInputType.CHECKBOX,
                targetCount = 1,
                unit = "dose",
                isStandard = true,
                iconName = "bolt",
                reminderTime = "12:00"
            ),
            // 9. Study 1.5 hour daily
            HabitEntity(
                title = "Study 1.5 hour daily",
                description = "Focused uninterrupted study & skill development (90m)",
                category = HabitCategory.MIND_FOCUS,
                frequencyType = HabitFrequencyType.DAILY,
                intervalDays = 1,
                inputType = HabitInputType.CHECKBOX,
                targetCount = 1,
                unit = "session",
                isStandard = true,
                iconName = "book",
                reminderTime = "19:00"
            ),
            // 10. Reduce Phone usage
            HabitEntity(
                title = "Reduce Phone usage",
                description = "Keep non-productive screen time strictly controlled",
                category = HabitCategory.MIND_FOCUS,
                frequencyType = HabitFrequencyType.DAILY,
                intervalDays = 1,
                inputType = HabitInputType.CHECKBOX,
                targetCount = 1,
                unit = "target",
                isStandard = true,
                iconName = "phone",
                reminderTime = "21:30"
            ),
            // 11. Haircut (weekly)
            HabitEntity(
                title = "Haircut (weekly)",
                description = "Weekly fresh haircut and grooming upkeep",
                category = HabitCategory.GROOMING,
                frequencyType = HabitFrequencyType.WEEKLY,
                intervalDays = 7,
                inputType = HabitInputType.CHECKBOX,
                targetCount = 1,
                unit = "cut",
                isStandard = true,
                iconName = "cut",
                reminderTime = "11:00"
            ),
            // 12. Eyebrows setting (3 days)
            HabitEntity(
                title = "Eyebrows setting (3 days)",
                description = "Trim and shape eyebrows every 3 days",
                category = HabitCategory.GROOMING,
                frequencyType = HabitFrequencyType.EVERY_N_DAYS,
                intervalDays = 3,
                inputType = HabitInputType.CHECKBOX,
                targetCount = 1,
                unit = "setting",
                isStandard = true,
                iconName = "eye",
                reminderTime = "10:00"
            ),
            // 13. Keep beard trimmed (3 days)
            HabitEntity(
                title = "Keep beard trimmed (3 days)",
                description = "Neatly trim and line up beard every 3 days",
                category = HabitCategory.GROOMING,
                frequencyType = HabitFrequencyType.EVERY_N_DAYS,
                intervalDays = 3,
                inputType = HabitInputType.CHECKBOX,
                targetCount = 1,
                unit = "trim",
                isStandard = true,
                iconName = "face",
                reminderTime = "09:30"
            ),
            // 14. Running (every saturday sunday morning)
            HabitEntity(
                title = "Running (Sat & Sun morning)",
                description = "Outdoor morning cardio running on weekends",
                category = HabitCategory.CORE_HEALTH,
                frequencyType = HabitFrequencyType.WEEKEND_ONLY,
                intervalDays = 1,
                inputType = HabitInputType.CHECKBOX,
                targetCount = 1,
                unit = "run",
                isStandard = true,
                iconName = "directions_run",
                reminderTime = "06:30"
            )
        )
        habitDao.insertHabits(defaults)
    }

    private suspend fun seedRolling30DaysIfEmpty() {
        val today = KarachiTimeHelper.todayKarachiDate()
        val startDate = today.minusDays(29)
        val existingSummaries = habitDao.getDailySummariesInRangeSync(
            KarachiTimeHelper.formatDateKey(startDate),
            KarachiTimeHelper.formatDateKey(today.minusDays(1))
        )
        if (existingSummaries.isNotEmpty()) return

        val habits = habitDao.getAllHabitsList()
        if (habits.isEmpty()) return

        val seededSummaries = mutableListOf<DailySummaryEntity>()
        val seededLogs = mutableListOf<HabitLogEntity>()
        val random = Random(42)

        for (i in 29 downTo 1) {
            val date = today.minusDays(i.toLong())
            val dateKey = KarachiTimeHelper.formatDateKey(date)

            val scheduledHabits = habits.filter { habit ->
                KarachiTimeHelper.isHabitScheduledForDate(
                    habit.frequencyType,
                    habit.intervalDays,
                    habit.daysOfWeekMask,
                    date
                )
            }

            var completedForDay = 0
            val totalForDay = scheduledHabits.size

            for (habit in scheduledHabits) {
                // Realistic adherence rate between 70% and 95%
                val isCompleted = random.nextFloat() < 0.82f
                val progress = if (isCompleted) habit.targetCount else (if (habit.inputType == HabitInputType.COUNTER) random.nextInt(0, habit.targetCount) else 0)
                if (isCompleted || progress >= habit.targetCount) {
                    completedForDay++
                }
                seededLogs.add(
                    HabitLogEntity(
                        habitId = habit.id,
                        dateKey = dateKey,
                        isCompleted = isCompleted || progress >= habit.targetCount,
                        currentProgress = progress,
                        targetProgress = habit.targetCount,
                        completedAtTimestamp = if (isCompleted) System.currentTimeMillis() - (i * 86400000L) else null
                    )
                )
            }

            val rate = if (totalForDay > 0) (completedForDay.toFloat() / totalForDay) * 100f else 0f
            seededSummaries.add(
                DailySummaryEntity(
                    dateKey = dateKey,
                    totalScheduled = totalForDay,
                    completedCount = completedForDay,
                    completionRate = rate,
                    updatedAt = System.currentTimeMillis() - (i * 86400000L)
                )
            )
        }

        habitDao.insertOrUpdateLogs(seededLogs)
        habitDao.insertOrUpdateDailySummaries(seededSummaries)
    }

    fun getHabitsWithProgress(date: LocalDate): Flow<List<HabitWithProgress>> {
        val dateKey = KarachiTimeHelper.formatDateKey(date)
        return combine(
            habitDao.getAllActiveHabits(),
            habitDao.getLogsForDate(dateKey)
        ) { habits, logs ->
            val logMap = logs.associateBy { it.habitId }
            habits.map { habit ->
                val scheduled = KarachiTimeHelper.isHabitScheduledForDate(
                    habit.frequencyType,
                    habit.intervalDays,
                    habit.daysOfWeekMask,
                    date
                )
                val log = logMap[habit.id]
                val current = log?.currentProgress ?: 0
                val completed = log?.isCompleted ?: false
                HabitWithProgress(
                    habit = habit,
                    isScheduledToday = scheduled,
                    isCompleted = completed,
                    currentProgress = current,
                    targetProgress = habit.targetCount,
                    dateKey = dateKey,
                    completedAtTimestamp = log?.completedAtTimestamp
                )
            }
        }
    }

    suspend fun toggleHabitCompletion(habitId: Long, dateKey: String) = withContext(Dispatchers.IO) {
        val habit = habitDao.getHabitById(habitId) ?: return@withContext
        val currentLog = habitDao.getLogForHabitAndDate(habitId, dateKey)
        val isNowCompleted = !(currentLog?.isCompleted ?: false)
        val progress = if (isNowCompleted) habit.targetCount else 0

        val newLog = HabitLogEntity(
            id = currentLog?.id ?: 0,
            habitId = habitId,
            dateKey = dateKey,
            isCompleted = isNowCompleted,
            currentProgress = progress,
            targetProgress = habit.targetCount,
            completedAtTimestamp = if (isNowCompleted) System.currentTimeMillis() else null
        )
        habitDao.insertOrUpdateLog(newLog)
        updateDailySummaryForDate(dateKey)
        firestoreSyncRepo?.syncHabitLog(newLog, habit)
    }

    suspend fun updateHabitProgress(habitId: Long, dateKey: String, newProgress: Int) = withContext(Dispatchers.IO) {
        val habit = habitDao.getHabitById(habitId) ?: return@withContext
        val clampedProgress = newProgress.coerceIn(0, habit.targetCount)
        val isCompleted = clampedProgress >= habit.targetCount
        val currentLog = habitDao.getLogForHabitAndDate(habitId, dateKey)

        val newLog = HabitLogEntity(
            id = currentLog?.id ?: 0,
            habitId = habitId,
            dateKey = dateKey,
            isCompleted = isCompleted,
            currentProgress = clampedProgress,
            targetProgress = habit.targetCount,
            completedAtTimestamp = if (isCompleted) System.currentTimeMillis() else null
        )
        habitDao.insertOrUpdateLog(newLog)
        updateDailySummaryForDate(dateKey)
        firestoreSyncRepo?.syncHabitLog(newLog, habit)
    }

    private suspend fun ensureDailySummaryExists(dateKey: String) {
        val existing = habitDao.getDailySummary(dateKey)
        if (existing == null) {
            updateDailySummaryForDate(dateKey)
        }
    }

    suspend fun updateDailySummaryForDate(dateKey: String) = withContext(Dispatchers.IO) {
        val date = KarachiTimeHelper.parseDateKey(dateKey)
        val allActive = habitDao.getAllHabitsList().filter { it.isEnabled }
        val scheduled = allActive.filter {
            KarachiTimeHelper.isHabitScheduledForDate(
                it.frequencyType,
                it.intervalDays,
                it.daysOfWeekMask,
                date
            )
        }
        val logs = habitDao.getLogsForDateSync(dateKey).associateBy { it.habitId }

        var completedCount = 0
        scheduled.forEach { habit ->
            val log = logs[habit.id]
            if (log != null && (log.isCompleted || log.currentProgress >= habit.targetCount)) {
                completedCount++
            }
        }

        val totalScheduled = scheduled.size
        val completionRate = if (totalScheduled > 0) (completedCount.toFloat() / totalScheduled) * 100f else 0f

        val summary = DailySummaryEntity(
            dateKey = dateKey,
            totalScheduled = totalScheduled,
            completedCount = completedCount,
            completionRate = completionRate,
            updatedAt = System.currentTimeMillis()
        )
        habitDao.insertOrUpdateDailySummary(summary)
        firestoreSyncRepo?.syncDailySummary(summary)
    }

    suspend fun syncAllToFirestore() = withContext(Dispatchers.IO) {
        val today = KarachiTimeHelper.todayKarachiDate()
        val summaries = getRolling30DaySummariesSync(today)
        firestoreSyncRepo?.syncAllRollingSummaries(summaries)

        val todayKey = KarachiTimeHelper.todayKarachiKey()
        val todayLogs = habitDao.getLogsForDateSync(todayKey)
        val habits = habitDao.getAllHabitsList().associateBy { it.id }
        todayLogs.forEach { log ->
            firestoreSyncRepo?.syncHabitLog(log, habits[log.habitId])
        }
    }

    fun getRolling30DaySummaries(today: LocalDate): Flow<List<DailySummaryEntity>> {
        val startDate = today.minusDays(29)
        val startKey = KarachiTimeHelper.formatDateKey(startDate)
        val endKey = KarachiTimeHelper.formatDateKey(today)
        return habitDao.getDailySummariesInRange(startKey, endKey)
    }

    suspend fun getRolling30DaySummariesSync(today: LocalDate): List<DailySummaryEntity> = withContext(Dispatchers.IO) {
        val startDate = today.minusDays(29)
        val startKey = KarachiTimeHelper.formatDateKey(startDate)
        val endKey = KarachiTimeHelper.formatDateKey(today)
        habitDao.getDailySummariesInRangeSync(startKey, endKey)
    }

    suspend fun getLogsBetweenDatesSync(startDateKey: String, endDateKey: String): List<HabitLogEntity> = withContext(Dispatchers.IO) {
        habitDao.getLogsBetweenDatesSync(startDateKey, endDateKey)
    }

    suspend fun addCustomHabit(habit: HabitEntity): Long = withContext(Dispatchers.IO) {
        val id = habitDao.insertHabit(habit)
        val todayKey = KarachiTimeHelper.todayKarachiKey()
        updateDailySummaryForDate(todayKey)
        id
    }

    suspend fun updateHabit(habit: HabitEntity) = withContext(Dispatchers.IO) {
        habitDao.updateHabit(habit)
        val todayKey = KarachiTimeHelper.todayKarachiKey()
        updateDailySummaryForDate(todayKey)
    }

    suspend fun deleteHabit(habit: HabitEntity) = withContext(Dispatchers.IO) {
        habitDao.deleteHabit(habit)
        val todayKey = KarachiTimeHelper.todayKarachiKey()
        updateDailySummaryForDate(todayKey)
    }

    suspend fun resetTodayChecklist(dateKey: String) = withContext(Dispatchers.IO) {
        habitDao.clearLogsForDate(dateKey)
        updateDailySummaryForDate(dateKey)
    }
}
