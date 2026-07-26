package com.example.fitapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.fitapp.data.local.entity.SetLog

@Dao
interface SetLogDao {

    @Query("SELECT * FROM set_logs WHERE logId = :logId ORDER BY exerciseId, setNumber")
    suspend fun getByLog(logId: Long): List<SetLog>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<SetLog>)

    @Update
    suspend fun update(item: SetLog)

    @Query("DELETE FROM set_logs WHERE logId = :logId")
    suspend fun deleteByLog(logId: Long)
}
