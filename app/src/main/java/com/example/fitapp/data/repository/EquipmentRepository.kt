package com.example.fitapp.data.repository

import com.example.fitapp.data.local.dao.EquipmentDao
import com.example.fitapp.data.local.entity.Equipment
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EquipmentRepository @Inject constructor(
    private val dao: EquipmentDao
) {
    suspend fun getAll(): List<Equipment> = dao.getAll()
}
