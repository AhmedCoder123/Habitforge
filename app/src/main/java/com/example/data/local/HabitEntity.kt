package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.HabitCategory
import com.example.data.model.HabitFrequencyType
import com.example.data.model.HabitInputType

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: HabitCategory,
    val frequencyType: HabitFrequencyType,
    val intervalDays: Int = 1,
    val daysOfWeekMask: Int = 0b1111111,
    val inputType: HabitInputType = HabitInputType.CHECKBOX,
    val targetCount: Int = 1,
    val unit: String = "",
    val isStandard: Boolean = false,
    val iconName: String = "check",
    val reminderTime: String? = null,
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
