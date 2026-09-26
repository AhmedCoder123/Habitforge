package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.HabitCategory
import com.example.data.model.HabitFrequencyType
import com.example.data.model.HabitInputType

class Converters {
    @TypeConverter
    fun fromCategory(category: HabitCategory): String = category.name

    @TypeConverter
    fun toCategory(value: String): HabitCategory =
        runCatching { HabitCategory.valueOf(value) }.getOrDefault(HabitCategory.CUSTOM)

    @TypeConverter
    fun fromFrequency(frequency: HabitFrequencyType): String = frequency.name

    @TypeConverter
    fun toFrequency(value: String): HabitFrequencyType =
        runCatching { HabitFrequencyType.valueOf(value) }.getOrDefault(HabitFrequencyType.DAILY)

    @TypeConverter
    fun fromInputType(inputType: HabitInputType): String = inputType.name

    @TypeConverter
    fun toInputType(value: String): HabitInputType =
        runCatching { HabitInputType.valueOf(value) }.getOrDefault(HabitInputType.CHECKBOX)
}
