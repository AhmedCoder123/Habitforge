package com.example.data.model

import com.example.data.local.HabitEntity

data class HabitWithProgress(
    val habit: HabitEntity,
    val isScheduledToday: Boolean,
    val isCompleted: Boolean,
    val currentProgress: Int,
    val targetProgress: Int,
    val dateKey: String,
    val completedAtTimestamp: Long? = null
) {
    val progressFraction: Float
        get() = if (targetProgress > 0) (currentProgress.toFloat() / targetProgress).coerceIn(0f, 1f) else if (isCompleted) 1f else 0f

    val isGoalReached: Boolean
        get() = isCompleted || (targetProgress > 0 && currentProgress >= targetProgress)
}
