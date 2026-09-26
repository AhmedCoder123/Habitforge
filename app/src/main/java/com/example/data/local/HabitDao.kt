package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Query("SELECT * FROM habits WHERE isEnabled = 1 ORDER BY id ASC")
    fun getAllActiveHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits ORDER BY id ASC")
    fun getAllHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits ORDER BY id ASC")
    suspend fun getAllHabitsList(): List<HabitEntity>

    @Query("SELECT * FROM habits WHERE id = :id")
    suspend fun getHabitById(id: Long): HabitEntity?

    @Query("SELECT COUNT(*) FROM habits")
    suspend fun getHabitsCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabits(habits: List<HabitEntity>)

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Delete
    suspend fun deleteHabit(habit: HabitEntity)

    // Logs
    @Query("SELECT * FROM habit_logs WHERE dateKey = :dateKey")
    fun getLogsForDate(dateKey: String): Flow<List<HabitLogEntity>>

    @Query("SELECT * FROM habit_logs WHERE dateKey = :dateKey")
    suspend fun getLogsForDateSync(dateKey: String): List<HabitLogEntity>

    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId AND dateKey = :dateKey LIMIT 1")
    suspend fun getLogForHabitAndDate(habitId: Long, dateKey: String): HabitLogEntity?

    @Query("SELECT * FROM habit_logs WHERE dateKey >= :startDate AND dateKey <= :endDate")
    fun getLogsBetweenDates(startDate: String, endDate: String): Flow<List<HabitLogEntity>>

    @Query("SELECT * FROM habit_logs WHERE dateKey >= :startDate AND dateKey <= :endDate")
    suspend fun getLogsBetweenDatesSync(startDate: String, endDate: String): List<HabitLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateLog(log: HabitLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateLogs(logs: List<HabitLogEntity>)

    // Summaries
    @Query("SELECT * FROM daily_summaries WHERE dateKey >= :startDate AND dateKey <= :endDate ORDER BY dateKey ASC")
    fun getDailySummariesInRange(startDate: String, endDate: String): Flow<List<DailySummaryEntity>>

    @Query("SELECT * FROM daily_summaries WHERE dateKey >= :startDate AND dateKey <= :endDate ORDER BY dateKey ASC")
    suspend fun getDailySummariesInRangeSync(startDate: String, endDate: String): List<DailySummaryEntity>

    @Query("SELECT * FROM daily_summaries WHERE dateKey = :dateKey LIMIT 1")
    suspend fun getDailySummary(dateKey: String): DailySummaryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateDailySummary(summary: DailySummaryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateDailySummaries(summaries: List<DailySummaryEntity>)

    @Query("DELETE FROM habit_logs WHERE dateKey = :dateKey")
    suspend fun clearLogsForDate(dateKey: String)
}
