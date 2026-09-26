package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "habit_logs",
    indices = [
        Index(value = ["habitId", "dateKey"], unique = true),
        Index(value = ["dateKey"])
    ]
)
data class HabitLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val habitId: Long,
    val dateKey: String, // "YYYY-MM-DD"
    val isCompleted: Boolean,
    val currentProgress: Int,
    val targetProgress: Int,
    val completedAtTimestamp: Long? = null
)
