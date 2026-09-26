package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.DailySummaryEntity
import com.example.data.local.HabitEntity
import com.example.data.model.HabitCategory
import com.example.data.model.HabitFrequencyType
import com.example.data.model.HabitInputType
import com.example.data.model.HabitWithProgress
import com.example.data.repository.HabitRepository
import com.example.notifications.HabitNotificationManager
import com.example.util.KarachiTimeHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZonedDateTime

data class HabitConsistencyStat(
    val habit: HabitEntity,
    val totalScheduledIn30Days: Int,
    val completedCountIn30Days: Int,
    val rate: Float
)

data class AnalyticsState(
    val averageAdherence: Float = 0f,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalCompletionsLogged: Int = 0,
    val categoryRates: Map<HabitCategory, Float> = emptyMap(),
    val habitStats: List<HabitConsistencyStat> = emptyList(),
    val dailySummaries: List<DailySummaryEntity> = emptyList()
)

data class DayForecast(
    val date: LocalDate,
    val dayName: String,
    val scheduledHabits: List<HabitEntity>
)

class HabitViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    val authManager = com.example.auth.AuthManager(application)
    val firestoreSyncRepo = com.example.data.firebase.FirestoreHabitSyncRepository(application)
    private val repository = HabitRepository(database.habitDao(), firestoreSyncRepo)
    val notificationManager = HabitNotificationManager(application)

    val currentUser = authManager.currentUser
    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _syncMessage = MutableStateFlow<String?>("Local data ready")
    val syncMessage: StateFlow<String?> = _syncMessage.asStateFlow()

    // Karachi Clock State
    private val _karachiTime = MutableStateFlow(KarachiTimeHelper.nowInKarachi())
    val karachiTime: StateFlow<ZonedDateTime> = _karachiTime.asStateFlow()

    private val _currentDate = MutableStateFlow(KarachiTimeHelper.todayKarachiDate())
    val currentDate: StateFlow<LocalDate> = _currentDate.asStateFlow()

    private val _countdownSeconds = MutableStateFlow(KarachiTimeHelper.secondsUntilKarachiMidnight())
    val countdownSeconds: StateFlow<Long> = _countdownSeconds.asStateFlow()

    // Habits for current Karachi Date
    private val _todayHabits = MutableStateFlow<List<HabitWithProgress>>(emptyList())
    val todayHabits: StateFlow<List<HabitWithProgress>> = _todayHabits.asStateFlow()

    // All registered habits
    val allHabits: StateFlow<List<HabitEntity>> = repository.allHabits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 30-Day Analytics
    private val _analyticsState = MutableStateFlow(AnalyticsState())
    val analyticsState: StateFlow<AnalyticsState> = _analyticsState.asStateFlow()

    // Forecast for next 7 days
    private val _weekForecast = MutableStateFlow<List<DayForecast>>(emptyList())
    val weekForecast: StateFlow<List<DayForecast>> = _weekForecast.asStateFlow()

    // Notification Toggles
    private val _notifEnabled = MutableStateFlow(notificationManager.isNotificationEnabled())
    val notifEnabled: StateFlow<Boolean> = _notifEnabled.asStateFlow()

    private val _midnightAlertEnabled = MutableStateFlow(notificationManager.isMidnightAlertEnabled())
    val midnightAlertEnabled: StateFlow<Boolean> = _midnightAlertEnabled.asStateFlow()

    private val _morningBriefEnabled = MutableStateFlow(notificationManager.isMorningBriefEnabled())
    val morningBriefEnabled: StateFlow<Boolean> = _morningBriefEnabled.asStateFlow()

    private val _weekendRunAlert = MutableStateFlow(notificationManager.isWeekendRunEnabled())
    val weekendRunAlert: StateFlow<Boolean> = _weekendRunAlert.asStateFlow()

    private val _groomingAlert = MutableStateFlow(notificationManager.isGroomingAlertEnabled())
    val groomingAlert: StateFlow<Boolean> = _groomingAlert.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initializeIfNeeded()
            loadTodayHabits()
            refreshAnalytics()
            generateForecast()
        }
        startKarachiClockTicker()
    }

    private fun startKarachiClockTicker() {
        viewModelScope.launch(Dispatchers.Default) {
            var lastObservedDate = KarachiTimeHelper.todayKarachiDate()
            while (isActive) {
                val now = KarachiTimeHelper.nowInKarachi()
                val today = now.toLocalDate()
                val secondsRemaining = KarachiTimeHelper.secondsUntilKarachiMidnight()

                _karachiTime.value = now
                _countdownSeconds.value = secondsRemaining

                // Detect 12:00 AM Midnight Rollover
                if (today != lastObservedDate) {
                    lastObservedDate = today
                    _currentDate.value = today
                    withContext(Dispatchers.Main) {
                        onDateRollover(today)
                    }
                }

                delay(1000L)
            }
        }
    }

    private fun onDateRollover(newDate: LocalDate) {
        viewModelScope.launch {
            val key = KarachiTimeHelper.formatDateKey(newDate)
            repository.updateDailySummaryForDate(key)
            loadTodayHabits()
            refreshAnalytics()
            generateForecast()
            if (notificationManager.isNotificationEnabled() && notificationManager.isMidnightAlertEnabled()) {
                notificationManager.triggerTestNotification(
                    title = "Karachi Reset Complete (12:00 AM)",
                    message = "A brand new day has begun in Karachi! Your checklist is reset and ready.",
                    isResetChannel = true
                )
            }
        }
    }

    private fun loadTodayHabits() {
        viewModelScope.launch {
            val date = _currentDate.value
            repository.getHabitsWithProgress(date).collect { list ->
                _todayHabits.value = list
                refreshAnalytics()
            }
        }
    }

    fun toggleHabit(habitId: Long) {
        viewModelScope.launch {
            val dateKey = KarachiTimeHelper.formatDateKey(_currentDate.value)
            repository.toggleHabitCompletion(habitId, dateKey)
            refreshAnalytics()
        }
    }

    fun updateProgress(habitId: Long, newProgress: Int) {
        viewModelScope.launch {
            val dateKey = KarachiTimeHelper.formatDateKey(_currentDate.value)
            repository.updateHabitProgress(habitId, dateKey, newProgress)
            refreshAnalytics()
        }
    }

    fun refreshAnalytics() {
        viewModelScope.launch(Dispatchers.IO) {
            val today = _currentDate.value
            val summaries = repository.getRolling30DaySummariesSync(today)
            val habits = database.habitDao().getAllHabitsList().filter { it.isEnabled }

            val startDate = today.minusDays(29)
            val startKey = KarachiTimeHelper.formatDateKey(startDate)
            val endKey = KarachiTimeHelper.formatDateKey(today)
            val logs = repository.getLogsBetweenDatesSync(startKey, endKey)
            val logsMap = logs.groupBy { it.habitId }

            // Average adherence
            val avgAdherence = if (summaries.isNotEmpty()) {
                summaries.map { it.completionRate }.average().toFloat()
            } else 0f

            // Total completions logged
            val totalCompletions = logs.count { it.isCompleted || it.currentProgress >= it.targetProgress }

            // Current streak (consecutive days ending yesterday or today with >= 75% completion)
            var currentStreak = 0
            var longestStreak = 0
            var tempStreak = 0

            val sortedSummaries = summaries.sortedBy { it.dateKey }
            for (summary in sortedSummaries) {
                if (summary.completionRate >= 70f) {
                    tempStreak++
                    if (tempStreak > longestStreak) {
                        longestStreak = tempStreak
                    }
                } else {
                    tempStreak = 0
                }
            }
            // Count backwards from latest for current streak
            for (summary in sortedSummaries.reversed()) {
                if (summary.completionRate >= 70f) {
                    currentStreak++
                } else {
                    break
                }
            }

            // Category breakdown
            val categoryTotal = mutableMapOf<HabitCategory, Int>()
            val categoryCompleted = mutableMapOf<HabitCategory, Int>()

            for (h in habits) {
                val hLogs = logsMap[h.id] ?: emptyList()
                val completedCount = hLogs.count { it.isCompleted || it.currentProgress >= it.targetProgress }
                val scheduledDaysCount = (0..29).count { dayOffset ->
                    val d = today.minusDays(dayOffset.toLong())
                    KarachiTimeHelper.isHabitScheduledForDate(h.frequencyType, h.intervalDays, h.daysOfWeekMask, d)
                }
                categoryTotal[h.category] = (categoryTotal[h.category] ?: 0) + scheduledDaysCount
                categoryCompleted[h.category] = (categoryCompleted[h.category] ?: 0) + completedCount
            }

            val categoryRates = HabitCategory.entries.associateWith { cat ->
                val total = categoryTotal[cat] ?: 0
                val comp = categoryCompleted[cat] ?: 0
                if (total > 0) (comp.toFloat() / total) * 100f else 0f
            }

            // Per habit stats
            val habitStats = habits.map { h ->
                val hLogs = logsMap[h.id] ?: emptyList()
                val completedCount = hLogs.count { it.isCompleted || it.currentProgress >= it.targetProgress }
                val scheduledDaysCount = (0..29).count { dayOffset ->
                    val d = today.minusDays(dayOffset.toLong())
                    KarachiTimeHelper.isHabitScheduledForDate(h.frequencyType, h.intervalDays, h.daysOfWeekMask, d)
                }.coerceAtLeast(1)
                val rate = (completedCount.toFloat() / scheduledDaysCount * 100f).coerceIn(0f, 100f)
                HabitConsistencyStat(
                    habit = h,
                    totalScheduledIn30Days = scheduledDaysCount,
                    completedCountIn30Days = completedCount,
                    rate = rate
                )
            }.sortedByDescending { it.rate }

            _analyticsState.value = AnalyticsState(
                averageAdherence = avgAdherence,
                currentStreak = currentStreak,
                longestStreak = longestStreak.coerceAtLeast(currentStreak),
                totalCompletionsLogged = totalCompletions,
                categoryRates = categoryRates,
                habitStats = habitStats,
                dailySummaries = sortedSummaries
            )
        }
    }

    fun generateForecast() {
        viewModelScope.launch(Dispatchers.IO) {
            val today = _currentDate.value
            val habits = database.habitDao().getAllHabitsList().filter { it.isEnabled }
            val forecast = (0..6).map { offset ->
                val date = today.plusDays(offset.toLong())
                val dayName = if (offset == 0) "Today" else if (offset == 1) "Tomorrow" else date.dayOfWeek.name.take(3)
                val scheduled = habits.filter {
                    KarachiTimeHelper.isHabitScheduledForDate(it.frequencyType, it.intervalDays, it.daysOfWeekMask, date)
                }
                DayForecast(date = date, dayName = dayName, scheduledHabits = scheduled)
            }
            _weekForecast.value = forecast
        }
    }

    fun addCustomHabit(
        title: String,
        category: HabitCategory,
        frequencyType: HabitFrequencyType,
        intervalDays: Int,
        inputType: HabitInputType,
        targetCount: Int,
        unit: String,
        iconName: String,
        reminderTime: String?
    ) {
        viewModelScope.launch {
            val habit = HabitEntity(
                title = title.trim(),
                category = category,
                frequencyType = frequencyType,
                intervalDays = intervalDays,
                inputType = inputType,
                targetCount = targetCount,
                unit = unit.trim(),
                iconName = iconName,
                reminderTime = reminderTime,
                isStandard = false
            )
            repository.addCustomHabit(habit)
            generateForecast()
            refreshAnalytics()
        }
    }

    fun deleteHabit(habit: HabitEntity) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
            generateForecast()
            refreshAnalytics()
        }
    }

    fun simulateResetNow() {
        viewModelScope.launch {
            val todayKey = KarachiTimeHelper.todayKarachiKey()
            repository.resetTodayChecklist(todayKey)
            refreshAnalytics()
            if (_midnightAlertEnabled.value) {
                notificationManager.triggerTestNotification(
                    title = "Karachi 12:00 AM Reset Triggered",
                    message = "Today's checklist has been cleared according to Karachi (GMT+5) schedule.",
                    isResetChannel = true
                )
            }
        }
    }

    fun testHabitNotification(title: String, body: String) {
        notificationManager.triggerTestNotification(title, body, isResetChannel = false)
    }

    fun testResetNotification() {
        notificationManager.triggerTestNotification(
            title = "12:00 AM Karachi Reset Alert (Test)",
            message = "Scheduled daily checklist reset strictly applied at 12:00 AM Karachi Time (GMT+5).",
            isResetChannel = true
        )
    }

    fun toggleNotification(enabled: Boolean) {
        _notifEnabled.value = enabled
        notificationManager.setNotificationEnabled(enabled)
    }

    fun toggleMidnightAlert(enabled: Boolean) {
        _midnightAlertEnabled.value = enabled
        notificationManager.setMidnightAlertEnabled(enabled)
    }

    fun toggleMorningBrief(enabled: Boolean) {
        _morningBriefEnabled.value = enabled
        notificationManager.setMorningBriefEnabled(enabled)
    }

    fun toggleWeekendRun(enabled: Boolean) {
        _weekendRunAlert.value = enabled
        notificationManager.setWeekendRunEnabled(enabled)
    }

    fun toggleGroomingAlert(enabled: Boolean) {
        _groomingAlert.value = enabled
        notificationManager.setGroomingAlertEnabled(enabled)
    }

    fun signInWithGoogle(
        activity: android.app.Activity,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        authManager.signInWithGoogle(
            activity = activity,
            scope = viewModelScope,
            onSuccess = { user ->
                _syncMessage.value = "Signed in as ${user.displayName ?: user.email}"
                syncToFirestore()
                onSuccess()
            },
            onError = { error ->
                _syncMessage.value = "Sign-in error: $error"
                onError(error)
            },
            onCancelled = {
                _syncMessage.value = "Sign-in cancelled"
            }
        )
    }

    fun signOut(onComplete: () -> Unit = {}) {
        authManager.signOut(viewModelScope) {
            _syncMessage.value = "Signed out"
            onComplete()
        }
    }

    fun syncToFirestore() {
        viewModelScope.launch {
            if (currentUser.value == null) {
                _syncMessage.value = "Sign in with Google to sync to Cloud Firestore"
                return@launch
            }
            _isSyncing.value = true
            _syncMessage.value = "Syncing with Karachi GMT+5 Cloud Firestore..."
            try {
                repository.syncAllToFirestore()
                _syncMessage.value = "Synced with Karachi GMT+5 Cloud Firestore at ${KarachiTimeHelper.formatShortTime(KarachiTimeHelper.nowInKarachi())}"
            } catch (e: Exception) {
                _syncMessage.value = "Sync failed: ${e.message}"
            } finally {
                _isSyncing.value = false
            }
        }
    }
}
