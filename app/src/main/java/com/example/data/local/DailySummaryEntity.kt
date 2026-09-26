package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_summaries")
data class DailySummaryEntity(
    @PrimaryKey val dateKey: String, // "YYYY-MM-DD"
    val totalScheduled: Int,
    val completedCount: Int,
    val completionRate: Float, // 0f to 100f
    val updatedAt: Long = System.currentTimeMillis()
)
