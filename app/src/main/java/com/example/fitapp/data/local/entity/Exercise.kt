package com.example.fitapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Упражнение.
 *
 * @param primaryMuscleCode   код основной группы мышц (например "chest")
 * @param secondaryMuscleCode код вторичной группы мышц (nullable)
 * @param equipmentCode       код оборудования (например "barbell")
 * @param difficulty          сложность (BEGINNER / INTERMEDIATE / ADVANCED)
 * @param imageUrl            URL изображения (сетевое; на MVP может быть пустым)
 * @param videoUrl            ссылка на видео (URL YouTube)
 * @param technique           рекомендации по технике выполнения
 */
@Entity(
    tableName = "exercises",
    indices = [
        Index(value = ["primaryMuscleCode"]),
        Index(value = ["equipmentCode"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = MuscleGroup::class,
            parentColumns = ["code"],
            childColumns = ["primaryMuscleCode"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = Equipment::class,
            parentColumns = ["code"],
            childColumns = ["equipmentCode"],
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class Exercise(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val name: String,
    val description: String,
    val primaryMuscleCode: String,
    val secondaryMuscleCode: String?,
    val equipmentCode: String,
    val difficulty: String,
    val imageUrl: String?,
    val videoUrl: String?,
    val technique: String
)
