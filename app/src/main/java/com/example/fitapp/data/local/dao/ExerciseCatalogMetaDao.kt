package com.example.fitapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fitapp.data.local.entity.ExerciseCatalogMeta
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseCatalogMetaDao {

    @Query("SELECT * FROM exercise_catalog_meta")
    fun observeAll(): Flow<List<ExerciseCatalogMeta>>

    @Query("SELECT * FROM exercise_catalog_meta WHERE exerciseId = :exerciseId")
    suspend fun getByExerciseId(exerciseId: Long): ExerciseCatalogMeta?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(meta: ExerciseCatalogMeta)
}
