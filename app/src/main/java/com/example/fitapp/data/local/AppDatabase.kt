package com.example.fitapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.fitapp.data.local.dao.EquipmentDao
import com.example.fitapp.data.local.dao.ExerciseCatalogMetaDao
import com.example.fitapp.data.local.dao.ExerciseDao
import com.example.fitapp.data.local.dao.MuscleGroupDao
import com.example.fitapp.data.local.dao.SetLogDao
import com.example.fitapp.data.local.dao.WorkoutDao
import com.example.fitapp.data.local.dao.WorkoutExerciseDao
import com.example.fitapp.data.local.dao.WorkoutLogDao
import com.example.fitapp.data.local.entity.Equipment
import com.example.fitapp.data.local.entity.Exercise
import com.example.fitapp.data.local.entity.ExerciseCatalogMeta
import com.example.fitapp.data.local.entity.MuscleGroup
import com.example.fitapp.data.local.entity.SetLog
import com.example.fitapp.data.local.entity.Workout
import com.example.fitapp.data.local.entity.WorkoutExercise
import com.example.fitapp.data.local.entity.WorkoutLog

@Database(
    entities = [
        MuscleGroup::class,
        Equipment::class,
        Exercise::class,
        ExerciseCatalogMeta::class,
        Workout::class,
        WorkoutExercise::class,
        WorkoutLog::class,
        SetLog::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun muscleGroupDao(): MuscleGroupDao
    abstract fun equipmentDao(): EquipmentDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun exerciseCatalogMetaDao(): ExerciseCatalogMetaDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun workoutExerciseDao(): WorkoutExerciseDao
    abstract fun workoutLogDao(): WorkoutLogDao
    abstract fun setLogDao(): SetLogDao
}
