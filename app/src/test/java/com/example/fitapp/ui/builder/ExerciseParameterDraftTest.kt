package com.example.fitapp.ui.builder

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExerciseParameterDraftTest {
    @Test fun blankOrInvalidVisibleDraftCannotBeSaved() {
        val valid = ExerciseParameterDraft("3", "8-12", "60")
        assertTrue(valid.isValid)
        listOf("", "0", "-1", "101", "999999999999").forEach { assertFalse(valid.copy(sets = it).isValid) }
        listOf("", "0", "-1", "12-8", "8-", "1-2-3", "NaN", "10000").forEach { assertFalse(valid.copy(reps = it).isValid) }
        listOf("", "-1", "1.5", "3601").forEach { assertFalse(valid.copy(rest = it).isValid) }
        assertTrue(valid.copy(reps = "30", rest = "0").isValid)
        assertTrue(valid.copy(sets = "100", reps = "9999", rest = "3600").isValid)
        assertTrue(valid.copy(reps = "8–12").isValid)
        assertTrue(valid.copy(reps = "30с").isValid)
    }
}
