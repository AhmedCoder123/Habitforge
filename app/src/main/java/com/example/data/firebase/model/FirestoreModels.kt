package com.example.data.firebase.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue

data class FirestoreHabitLog(
    val id: String = "",
    val userId: String = "",
    val habitId: String = "",
    val habitTitle: String = "",
    val dateKey: String = "",
    val isCompleted: Boolean = false,
    val currentProgress: Int = 0,
    val targetProgress: Int = 1,
    val karachiDate: String = "",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) {
    fun toCreateMap(): Map<String, Any> {
        val map = mutableMapOf<String, Any>(
            "id" to id,
            "userId" to userId,
            "habitId" to habitId,
            "dateKey" to dateKey,
            "isCompleted" to isCompleted,
            "currentProgress" to currentProgress,
            "targetProgress" to targetProgress,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        if (habitTitle.isNotBlank()) map["habitTitle"] = habitTitle
        if (karachiDate.isNotBlank()) map["karachiDate"] = karachiDate
        return map
    }

    fun toUpdateMap(): Map<String, Any> {
        val map = mutableMapOf<String, Any>(
            "isCompleted" to isCompleted,
            "currentProgress" to currentProgress,
            "targetProgress" to targetProgress,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        if (habitTitle.isNotBlank()) map["habitTitle"] = habitTitle
        if (karachiDate.isNotBlank()) map["karachiDate"] = karachiDate
        return map
    }
}

data class FirestoreDailySummary(
    val dateKey: String = "",
    val userId: String = "",
    val totalScheduled: Int = 0,
    val completedCount: Int = 0,
    val completionRate: Double = 0.0,
    val karachiTimezone: String = "Asia/Karachi",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) {
    fun toCreateMap(): Map<String, Any> {
        val map = mutableMapOf<String, Any>(
            "dateKey" to dateKey,
            "userId" to userId,
            "totalScheduled" to totalScheduled,
            "completedCount" to completedCount,
            "completionRate" to completionRate,
            "karachiTimezone" to karachiTimezone,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        return map
    }

    fun toUpdateMap(): Map<String, Any> {
        return mapOf(
            "totalScheduled" to totalScheduled,
            "completedCount" to completedCount,
            "completionRate" to completionRate,
            "karachiTimezone" to karachiTimezone,
            "updatedAt" to FieldValue.serverTimestamp()
        )
    }
}
