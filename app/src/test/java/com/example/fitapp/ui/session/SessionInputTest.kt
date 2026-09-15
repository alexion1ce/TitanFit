package com.example.fitapp.ui.session

import org.junit.Assert.*
import org.junit.Test

class SessionInputTest {
    @Test fun rejectsBlankNegativeAndNonFiniteWeight() {
        listOf("", " ", "-1", "NaN", "Infinity", "1e999", "oops").forEach { assertNull(parseSetWeight(it)) }
        assertEquals(0.0, parseSetWeight("0")!!, 0.0)
        assertEquals(12.5, parseSetWeight("12,5")!!, 0.0)
    }
    @Test fun requiresPositiveWholeCount() {
        listOf("", "0", "-1", "1.5", "999999999999").forEach { assertNull(parseSetCount(it)) }
        assertEquals(30, parseSetCount("30"))
    }
    @Test fun durationDoesNotRequireWeightButDoesRequireSeconds() {
        assertTrue(SetInputDraft("", "30").isValid(timed = true))
        assertFalse(SetInputDraft("", "30").isValid(timed = false))
        assertFalse(SetInputDraft("0", "").isValid(timed = true))
        assertFalse(SetInputDraft("0", "").isValid(timed = false))
    }
}
