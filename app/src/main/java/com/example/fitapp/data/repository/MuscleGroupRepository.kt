package com.example.fitapp.data.repository

import com.example.fitapp.data.local.dao.MuscleGroupDao
import com.example.fitapp.data.local.entity.MuscleGroup
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MuscleGroupRepository @Inject constructor(
    private val dao: MuscleGroupDao
) {
    fun observeAll(): Flow<List<MuscleGroup>> = dao.observeAll()
    suspend fun getAll(): List<MuscleGroup> = dao.getAll()
}
