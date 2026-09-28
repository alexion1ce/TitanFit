package com.example.fitapp.data.repository

import com.example.fitapp.data.local.entity.SetLog
import com.example.fitapp.data.local.entity.WorkoutLog
import java.time.Instant
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProgressSnapshotTest {
    private val now = Instant.parse("2026-10-01T12:00:00Z")
    private val zone = ZoneId.of("UTC")

    // Newest first, as WorkoutLogDao.getAllFinished returns them.
    private val logs = listOf(
        log(2, "2026-09-29T10:00:00Z"),
        log(1, "2026-09-22T10:00:00Z")
    )
    private val sets = listOf(
        set(1, 1, exerciseId = 10, weight = 50.0, reps = 10, done = true),
        set(2, 1, exerciseId = 10, weight = 60.0, reps = 5, done = true),
        set(3, 2, exerciseId = 10, weight = 70.0, reps = 3, done = true),
        set(4, 2, exerciseId = 20, weight = 100.0, reps = 1, done = false),
        set(5, 2, exerciseId = 30, weight = 0.0, reps = 0, done = true, durationSeconds = 60)
    )

    @Test fun `weekly volume groups completed sets by local week`() {
        val weekly = calculateWeeklyVolume(logs, sets, 2, now, zone)
        assertEquals(listOf(800.0, 210.0), weekly.map { it.totalVolume })
        assertEquals(listOf(1, 1), weekly.map { it.workoutCount })
    }

    @Test fun `weekly volume is empty when nothing was completed`() {
        assertTrue(calculateWeeklyVolume(logs, sets.map { it.copy(done = false) }, 4, now, zone).isEmpty())
    }

    @Test fun `recent summaries count only completed sets and keep order`() {
        val recent = calculateRecentSummaries(logs, sets, limit = 1)
        assertEquals(1, recent.size)
        assertEquals(2L, recent.single().logId)
        assertEquals(2, recent.single().completedSets)
        assertEquals(2, recent.single().exerciseCount)
        assertEquals(210.0, recent.single().totalVolume, 0.0)
    }

    @Test fun `records use the heaviest completed set and its date`() {
        val recordSets = sets.filter { it.done && it.durationSeconds == null && it.weight > 0 }
        val records = calculatePersonalRecords(logs, recordSets, limit = 5) { "ex$it" to "*" }
        assertEquals(1, records.size)
        val record = records.single()
        assertEquals(70.0, record.maxWeight, 0.0)
        assertEquals(Instant.parse("2026-09-29T10:00:00Z").toEpochMilli() - 3_600_000L, record.date)
        // Epley 1RM: 50x10 = 66.7, 60x5 = 70.0, 70x3 = 77.0.
        assertEquals(77.0, record.estimated1RM, 0.001)
    }

    private fun log(id: Long, finished: String): WorkoutLog {
        val finishedAt = Instant.parse(finished).toEpochMilli()
        return WorkoutLog(
            id = id, workoutId = 1, workoutName = "W$id",
            startedAt = finishedAt - 3_600_000L, finishedAt = finishedAt, durationMin = 60
        )
    }

    private fun set(
        id: Long, logId: Long, exerciseId: Long, weight: Double, reps: Int,
        done: Boolean, durationSeconds: Int? = null
    ) = SetLog(
        id = id, logId = logId, exerciseId = exerciseId, setNumber = id.toInt(),
        weight = weight, reps = reps, done = done, durationSeconds = durationSeconds
    )
}
