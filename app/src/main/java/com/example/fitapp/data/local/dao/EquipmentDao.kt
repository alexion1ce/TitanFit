package com.example.fitapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fitapp.data.local.entity.Equipment

@Dao
interface EquipmentDao {

    @Query("SELECT * FROM equipment ORDER BY name")
    suspend fun getAll(): List<Equipment>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<Equipment>)

    @Query("SELECT COUNT(*) FROM equipment")
    suspend fun count(): Int
}
