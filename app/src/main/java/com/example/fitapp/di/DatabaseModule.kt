package com.example.fitapp.di

import android.content.Context
import androidx.room.Room
import com.example.fitapp.data.local.AppDatabase
import com.example.fitapp.data.local.DatabaseMigrations
import com.example.fitapp.data.local.dao.EquipmentDao
import com.example.fitapp.data.local.dao.ExerciseDao
import com.example.fitapp.data.local.dao.MuscleGroupDao
import com.example.fitapp.data.local.dao.SetLogDao
import com.example.fitapp.data.local.dao.WorkoutDao
import com.example.fitapp.data.local.dao.WorkoutExerciseDao
import com.example.fitapp.data.local.dao.WorkoutLogDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "fitapp.db"
        ).addMigrations(*DatabaseMigrations.ALL).build()

    @Provides
    fun provideMuscleGroupDao(db: AppDatabase): MuscleGroupDao = db.muscleGroupDao()

    @Provides
    fun provideEquipmentDao(db: AppDatabase): EquipmentDao = db.equipmentDao()

    @Provides
    fun provideExerciseDao(db: AppDatabase): ExerciseDao = db.exerciseDao()

    @Provides
    fun provideWorkoutDao(db: AppDatabase): WorkoutDao = db.workoutDao()

    @Provides
    fun provideWorkoutExerciseDao(db: AppDatabase): WorkoutExerciseDao = db.workoutExerciseDao()

    @Provides
    fun provideWorkoutLogDao(db: AppDatabase): WorkoutLogDao = db.workoutLogDao()

    @Provides
    fun provideSetLogDao(db: AppDatabase): SetLogDao = db.setLogDao()
}
