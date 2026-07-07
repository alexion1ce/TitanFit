package com.example.fitapp.data.repository

import com.example.fitapp.data.local.dao.ExerciseCatalogMetaDao
import com.example.fitapp.data.local.entity.ExerciseCatalogMeta
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExerciseCatalogRepository @Inject constructor(
    private val metaDao: ExerciseCatalogMetaDao
) {
    fun observeAllMeta(): Flow<List<ExerciseCatalogMeta>> = metaDao.observeAll()

    suspend fun toggleFavorite(exerciseId: Long) {
        val current = metaDao.getByExerciseId(exerciseId)
        metaDao.upsert(
            current?.copy(isFavorite = !current.isFavorite)
                ?: ExerciseCatalogMeta(exerciseId = exerciseId, isFavorite = true)
        )
    }

    suspend fun markUsed(exerciseId: Long) {
        val current = metaDao.getByExerciseId(exerciseId)
        val now = System.currentTimeMillis()
        metaDao.upsert(
            current?.copy(
                lastUsedAt = now,
                quickAddCount = current.quickAddCount + 1
            ) ?: ExerciseCatalogMeta(
                exerciseId = exerciseId,
                lastUsedAt = now,
                quickAddCount = 1
            )
        )
    }
}
