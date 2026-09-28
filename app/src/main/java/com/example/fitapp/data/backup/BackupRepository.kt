package com.example.fitapp.data.backup

import android.content.Context
import android.net.Uri
import androidx.room.withTransaction
import com.example.fitapp.data.local.AppDatabase
import com.example.fitapp.data.local.dao.ExerciseCatalogMetaDao
import com.example.fitapp.data.local.dao.ExerciseDao
import com.example.fitapp.data.local.dao.SetLogDao
import com.example.fitapp.data.local.dao.WorkoutDao
import com.example.fitapp.data.local.dao.WorkoutExerciseDao
import com.example.fitapp.data.local.dao.WorkoutLogDao
import com.example.fitapp.data.local.entity.ExerciseCatalogMeta
import com.example.fitapp.data.local.entity.SetLog
import com.example.fitapp.data.local.entity.Workout
import com.example.fitapp.data.local.entity.WorkoutExercise
import com.example.fitapp.data.local.entity.WorkoutLog
import com.example.fitapp.data.local.entity.WorkoutType
import com.example.fitapp.data.repository.DatabaseInitializer
import com.example.fitapp.data.repository.UserProfileRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Exports the user's data to a JSON file and merges it back.
 * Import only adds data: nothing already on the device is deleted, and
 * importing the same file twice does not duplicate workouts in the journal.
 */
@Singleton
class BackupRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val db: AppDatabase,
    private val exerciseDao: ExerciseDao,
    private val metaDao: ExerciseCatalogMetaDao,
    private val workoutDao: WorkoutDao,
    private val workoutExerciseDao: WorkoutExerciseDao,
    private val workoutLogDao: WorkoutLogDao,
    private val setLogDao: SetLogDao,
    private val userProfileRepository: UserProfileRepository,
    private val databaseInitializer: DatabaseInitializer
) {

    suspend fun exportTo(uri: Uri) {
        val text = BackupJson.encode(snapshot())
        withContext(Dispatchers.IO) {
            val stream = requireNotNull(context.contentResolver.openOutputStream(uri, "wt")) { "Не удалось открыть файл" }
            stream.use { it.write(text.toByteArray(Charsets.UTF_8)) }
        }
    }

    suspend fun importFrom(uri: Uri): ImportResult {
        val text = withContext(Dispatchers.IO) {
            val stream = requireNotNull(context.contentResolver.openInputStream(uri)) { "Не удалось открыть файл" }
            stream.use { it.readBytes().toString(Charsets.UTF_8) }
        }
        val data = BackupJson.decode(text)
        databaseInitializer.initializeIfNeeded()
        val result = db.withTransaction { merge(data) }
        data.profile?.let { userProfileRepository.saveProfile(it.copy(onboardingCompleted = true)) }
        return result
    }

    private suspend fun snapshot(): BackupData = db.withTransaction {
        val codeById = exerciseDao.getAllCodes().associate { it.id to it.code }
        val custom = workoutDao.getAllByType(WorkoutType.CUSTOM.storageKey)
        val customIds = custom.map { it.id }.toSet()
        val presetCodeById = workoutDao.getAllByType(WorkoutType.PRESET.storageKey)
            .associate { it.id to it.presetCode }
        val templateRows = workoutExerciseDao.getAll().groupBy { it.workoutId }
        val setsByLog = setLogDao.getAll().groupBy { it.logId }

        BackupData(
            exportedAt = System.currentTimeMillis(),
            appVersion = appVersion(),
            profile = userProfileRepository.loadProfile(),
            favorites = metaDao.getAll().mapNotNull { meta ->
                val code = codeById[meta.exerciseId] ?: return@mapNotNull null
                BackupFavorite(code, meta.isFavorite, meta.lastUsedAt, meta.quickAddCount)
            },
            workouts = custom.map { w ->
                BackupWorkout(
                    key = w.id,
                    name = w.name,
                    notes = w.notes,
                    isArchived = w.isArchived,
                    exercises = templateRows[w.id].orEmpty().mapNotNull { we ->
                        val code = codeById[we.exerciseId] ?: return@mapNotNull null
                        BackupWorkoutExercise(code, we.order, we.sets, we.reps, we.restSeconds)
                    }
                )
            },
            logs = workoutLogDao.getAll().map { log ->
                BackupLog(
                    workoutKey = log.workoutId.takeIf { it in customIds },
                    presetCode = presetCodeById[log.workoutId],
                    workoutName = log.workoutName,
                    startedAt = log.startedAt,
                    finishedAt = log.finishedAt,
                    durationMin = log.durationMin,
                    sets = setsByLog[log.id].orEmpty().mapNotNull { set ->
                        val code = codeById[set.exerciseId] ?: return@mapNotNull null
                        BackupSet(
                            exerciseCode = code,
                            exerciseOrder = set.exerciseOrder,
                            setNumber = set.setNumber,
                            weight = set.weight,
                            reps = set.reps,
                            done = set.done,
                            durationSeconds = set.durationSeconds,
                            restSeconds = set.restSeconds
                        )
                    }
                )
            }
        )
    }

    private suspend fun merge(data: BackupData): ImportResult {
        val idByCode = exerciseDao.getAllCodes().associate { it.code to it.id }
        var skippedSets = 0

        // Favorites: keep the stronger signal of both devices.
        for (fav in data.favorites) {
            val exerciseId = idByCode[fav.exerciseCode] ?: continue
            val current = metaDao.getByExerciseId(exerciseId)
            metaDao.upsert(
                ExerciseCatalogMeta(
                    exerciseId = exerciseId,
                    isFavorite = fav.isFavorite || current?.isFavorite == true,
                    lastUsedAt = listOfNotNull(fav.lastUsedAt, current?.lastUsedAt).maxOrNull(),
                    quickAddCount = maxOf(fav.quickAddCount, current?.quickAddCount ?: 0)
                )
            )
        }

        // Custom workouts: reuse an identical existing template, otherwise create one.
        val existingCustom = workoutDao.getAllByType(WorkoutType.CUSTOM.storageKey)
        val existingRows = workoutExerciseDao.getAll().groupBy { it.workoutId }
        val existingBySignature = existingCustom.associateBy { w ->
            templateSignature(w.name, existingRows[w.id].orEmpty().map { it.exerciseId to it.order })
        }
        val workoutIdByKey = mutableMapOf<Long, Long>()
        var importedWorkouts = 0
        for (w in data.workouts) {
            val rows = w.exercises.mapNotNull { e ->
                val exerciseId = idByCode[e.exerciseCode] ?: return@mapNotNull null
                WorkoutExercise(
                    workoutId = 0,
                    exerciseId = exerciseId,
                    order = e.order,
                    sets = e.sets,
                    reps = e.reps,
                    restSeconds = e.restSeconds
                )
            }
            val signature = templateSignature(w.name, rows.map { it.exerciseId to it.order })
            val existing = existingBySignature[signature]
            workoutIdByKey[w.key] = if (existing != null) {
                existing.id
            } else {
                val id = workoutDao.insert(
                    Workout(
                        name = w.name,
                        type = WorkoutType.CUSTOM.storageKey,
                        notes = w.notes,
                        isArchived = w.isArchived
                    )
                )
                if (rows.isNotEmpty()) workoutExerciseDao.insertAll(rows.map { it.copy(workoutId = id) })
                importedWorkouts++
                id
            }
        }

        // Logs: skip sessions that are already on the device.
        val existingLogs = workoutLogDao.getAll().map { it.startedAt to it.workoutName }.toSet()
        val fallbackIds = mutableMapOf<String, Long>()
        var importedLogs = 0
        var skippedLogs = 0
        for (log in data.logs) {
            if ((log.startedAt to log.workoutName) in existingLogs) {
                skippedLogs++
                continue
            }
            val workoutId = log.workoutKey?.let { workoutIdByKey[it] }
                ?: log.presetCode?.let { workoutDao.getPreset(it, log.workoutName)?.id }
                ?: fallbackIds.getOrPut(log.workoutName) {
                    // History must survive even if its template is unknown here.
                    workoutDao.insert(
                        Workout(
                            name = log.workoutName,
                            type = WorkoutType.CUSTOM.storageKey,
                            isArchived = true
                        )
                    )
                }
            val logId = workoutLogDao.insert(
                WorkoutLog(
                    workoutId = workoutId,
                    workoutName = log.workoutName,
                    startedAt = log.startedAt,
                    finishedAt = log.finishedAt,
                    durationMin = log.durationMin
                )
            )
            val sets = log.sets.mapNotNull { s ->
                val exerciseId = idByCode[s.exerciseCode]
                if (exerciseId == null) {
                    skippedSets++
                    return@mapNotNull null
                }
                SetLog(
                    logId = logId,
                    exerciseId = exerciseId,
                    exerciseOrder = s.exerciseOrder,
                    setNumber = s.setNumber,
                    weight = s.weight.takeIf { it.isFinite() && it >= 0.0 } ?: 0.0,
                    reps = s.reps.coerceAtLeast(0),
                    done = s.done,
                    durationSeconds = s.durationSeconds,
                    restSeconds = s.restSeconds.coerceIn(0, 3600)
                )
            }
            if (sets.isNotEmpty()) setLogDao.insertAll(sets)
            importedLogs++
        }
        return ImportResult(importedLogs, skippedLogs, importedWorkouts, skippedSets)
    }

    private fun templateSignature(name: String, rows: List<Pair<Long, Int>>) =
        name.trim() + "|" + rows.sortedBy { it.second }.joinToString(",") { it.first.toString() }

    private fun appVersion(): String = try {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName.orEmpty()
    } catch (e: Exception) {
        ""
    }
}
