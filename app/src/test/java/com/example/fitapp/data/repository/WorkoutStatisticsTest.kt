package com.example.fitapp.data.repository

import com.example.fitapp.data.local.entity.SetLog
import com.example.fitapp.data.local.entity.WorkoutLog
import java.time.Instant
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Test

class WorkoutStatisticsTest {
    private val now = Instant.parse("2026-09-09T12:00:00Z")
    private val zone = ZoneId.of("Asia/Almaty")

    @Test fun `period statistics exclude old unfinished and future sessions`() {
        val logs = listOf(
            log(1, "2026-09-01T12:00:00Z"), log(2, "2026-09-08T12:00:00Z"),
            log(3, "2026-09-10T12:00:00Z"), log(4, null)
        )
        val sets = (1L..4).map { set(it) }
        assertEquals(OverallStats(1, 1, 200.0, 30, 100), calculateOverallStats(logs, sets, 1, now, zone))
        assertEquals(OverallStats(2, 2, 400.0, 60, 100), calculateOverallStats(logs, sets, 4, now, zone))
    }

    @Test fun `local Monday boundary and timed sets match chart workout counts`() {
        val logs = listOf(log(1, "2026-09-06T18:59:59Z"), log(2, "2026-09-06T19:00:00Z"), log(3, "2026-09-08T10:00:00Z"))
        val sets = listOf(set(1), set(2).copy(durationSeconds = 30), set(3).copy(done = false))
        assertEquals(OverallStats(1, 1, 0.0, 30, 50), calculateOverallStats(logs, sets, 1, now, zone))
    }

    private fun log(id: Long, finish: String?) = WorkoutLog(id, 1, "Test", 0,
        finish?.let { Instant.parse(it).toEpochMilli() }, 30)
    private fun set(logId: Long) = SetLog(id = logId, logId = logId, exerciseId = 1,
        setNumber = 1, weight = 20.0, reps = 10, done = true)
}
