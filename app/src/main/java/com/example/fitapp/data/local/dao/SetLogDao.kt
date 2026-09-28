package com.example.fitapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.fitapp.data.local.entity.SetLog

@Dao
interface SetLogDao {

    @Query("SELECT * FROM set_logs WHERE logId = :logId ORDER BY exerciseOrder, setNumber, id")
    suspend fun getByLog(logId: Long): List<SetLog>

    /** All sets of finished workouts in one query, for progress statistics. */
    @Query(
        "SELECT s.* FROM set_logs s INNER JOIN workout_logs l ON l.id = s.logId " +
            "WHERE l.finishedAt IS NOT NULL ORDER BY s.logId, s.exerciseOrder, s.setNumber, s.id"
    )
    suspend fun getForFinishedLogs(): List<SetLog>

    @Query("SELECT * FROM set_logs ORDER BY logId, exerciseOrder, setNumber, id")
    suspend fun getAll(): List<SetLog>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<SetLog>)

    @Query("UPDATE set_logs SET weight = :weight WHERE id = :setId")
    suspend fun updateWeight(setId: Long, weight: Double)

    @Query("UPDATE set_logs SET reps = :reps WHERE id = :setId")
    suspend fun updateReps(setId: Long, reps: Int)

    @Query("UPDATE set_logs SET durationSeconds = :seconds WHERE id = :setId")
    suspend fun updateDuration(setId: Long, seconds: Int)

    @Query("UPDATE set_logs SET done = :done WHERE id = :setId")
    suspend fun updateDone(setId: Long, done: Boolean)

    @Update
    suspend fun update(item: SetLog)

    @Query("DELETE FROM set_logs WHERE logId = :logId")
    suspend fun deleteByLog(logId: Long)
}
