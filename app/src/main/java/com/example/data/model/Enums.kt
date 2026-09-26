package com.example.data.model

enum class HabitCategory(val displayName: String) {
    CORE_HEALTH("Health & Fitness"),
    GROOMING("Grooming & Hygiene"),
    MIND_FOCUS("Focus & Mindset"),
    CUSTOM("Custom Habits")
}

enum class HabitFrequencyType(val label: String) {
    DAILY("Daily"),
    EVERY_N_DAYS("Every N Days"),
    WEEKEND_ONLY("Saturday & Sunday"),
    WEEKLY("Weekly"),
    SPECIFIC_DAYS("Selected Days")
}

enum class HabitInputType {
    CHECKBOX,
    COUNTER
}
