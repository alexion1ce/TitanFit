package com.example.fitapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "exercise_catalog_meta",
    indices = [
        Index(value = ["isFavorite"]),
        Index(value = ["lastUsedAt"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = Exercise::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ExerciseCatalogMeta(
    @PrimaryKey val exerciseId: Long,
    val isFavorite: Boolean = false,
    val lastUsedAt: Long? = null,
    val quickAddCount: Int = 0
)
