package com.example.data.firebase

import android.content.Context
import com.example.R
import com.example.data.firebase.model.FirestoreDailySummary
import com.example.data.firebase.model.FirestoreHabitLog
import com.example.data.local.DailySummaryEntity
import com.example.data.local.HabitEntity
import com.example.data.local.HabitLogEntity
import com.example.util.KarachiTimeHelper
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirestoreHabitSyncRepository(
    private val context: Context,
    customFirestore: FirebaseFirestore? = null,
    customAuth: FirebaseAuth? = null
) {
    // CRITICAL: Always initialize with R.string.firestore_database_id
    private val databaseId: String = try {
        val resId = context.resources.getIdentifier("firestore_database_id", "string", context.packageName)
        if (resId != 0) context.getString(resId) else "ai-studio-android-habitfor-7cf055ff-8541-4e2f-812d-1951c04345e3"
    } catch (e: Exception) {
        "ai-studio-android-habitfor-7cf055ff-8541-4e2f-812d-1951c04345e3"
    }

    private val db: FirebaseFirestore = customFirestore ?: FirebaseFirestore.getInstance(databaseId)
    private val auth: FirebaseAuth = customAuth ?: Firebase.auth

    fun getCurrentUserId(): String? = auth.currentUser?.uid

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    /**
     * Observes real-time habit completion logs for the authenticated user for a specific Karachi dateKey.
     */
    fun observeUserLogsForDate(dateKey: String): Flow<List<FirestoreHabitLog>> = flow {
        val uid = requireUserId()
        val path = "users/$uid/habit_logs"
        emitAll(
            db.collection("users")
                .document(uid)
                .collection("habit_logs")
                .whereEqualTo("dateKey", dateKey)
                .snapshots()
                .map { snapshot -> snapshot.toObjects(FirestoreHabitLog::class.java) }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    /**
     * Observes real-time daily progress summaries for the user.
     */
    fun observeUserDailySummaries(): Flow<List<FirestoreDailySummary>> = flow {
        val uid = requireUserId()
        val path = "users/$uid/daily_summaries"
        emitAll(
            db.collection("users")
                .document(uid)
                .collection("daily_summaries")
                .snapshots()
                .map { snapshot -> snapshot.toObjects(FirestoreDailySummary::class.java) }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    /**
     * Syncs a habit completion log record to Firestore.
     * Document ID format: "${habitId}_${dateKey}" ensuring idempotency.
     */
    suspend fun syncHabitLog(
        log: HabitLogEntity,
        habit: HabitEntity?,
        onError: (String) -> Unit = {}
    ) = withContext(Dispatchers.IO) {
        val uid = getCurrentUserId() ?: return@withContext
        val docId = "${log.habitId}_${log.dateKey}"
        val docRef = db.collection("users").document(uid).collection("habit_logs").document(docId)

        val firestoreModel = FirestoreHabitLog(
            id = docId,
            userId = uid,
            habitId = log.habitId.toString(),
            habitTitle = habit?.title ?: "Habit ${log.habitId}",
            dateKey = log.dateKey,
            isCompleted = log.isCompleted,
            currentProgress = log.currentProgress,
            targetProgress = log.targetProgress,
            karachiDate = KarachiTimeHelper.formatDisplayDate(KarachiTimeHelper.parseDateKey(log.dateKey))
        )

        try {
            val exists = docRef.get().await().exists()
            if (!exists) {
                docRef.set(firestoreModel.toCreateMap()).await()
            } else {
                docRef.update(firestoreModel.toUpdateMap()).await()
            }
        } catch (e: Exception) {
            val errorJson = handleFirestoreError(e, OperationType.WRITE, docRef.path)
            onError(errorJson)
        }
    }

    /**
     * Syncs a daily summary record to Firestore.
     * Keyed strictly by the Karachi GMT+5 dateKey (e.g. "2026-09-26").
     */
    suspend fun syncDailySummary(
        summary: DailySummaryEntity,
        onError: (String) -> Unit = {}
    ) = withContext(Dispatchers.IO) {
        val uid = getCurrentUserId() ?: return@withContext
        val docRef = db.collection("users").document(uid).collection("daily_summaries").document(summary.dateKey)

        val firestoreSummary = FirestoreDailySummary(
            dateKey = summary.dateKey,
            userId = uid,
            totalScheduled = summary.totalScheduled,
            completedCount = summary.completedCount,
            completionRate = summary.completionRate.toDouble(),
            karachiTimezone = "Asia/Karachi"
        )

        try {
            val exists = docRef.get().await().exists()
            if (!exists) {
                docRef.set(firestoreSummary.toCreateMap()).await()
            } else {
                docRef.update(firestoreSummary.toUpdateMap()).await()
            }
        } catch (e: Exception) {
            val errorJson = handleFirestoreError(e, OperationType.WRITE, docRef.path)
            onError(errorJson)
        }
    }

    /**
     * Syncs rolling 30-day summaries from Room to Firestore in a batch.
     */
    suspend fun syncAllRollingSummaries(
        summaries: List<DailySummaryEntity>,
        onError: (String) -> Unit = {}
    ) = withContext(Dispatchers.IO) {
        val uid = getCurrentUserId() ?: return@withContext
        for (summary in summaries) {
            syncDailySummary(summary, onError)
        }
    }
}
