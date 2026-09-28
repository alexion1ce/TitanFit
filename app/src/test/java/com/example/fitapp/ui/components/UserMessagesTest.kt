package com.example.fitapp.ui.components

import org.junit.Assert.assertEquals
import org.junit.Test

class UserMessagesTest {
    @Test fun `validation messages are shown as written`() {
        assertEquals("Добавьте хотя бы одно упражнение",
            userMessage(IllegalArgumentException("Добавьте хотя бы одно упражнение"), "fallback"))
    }

    @Test fun `technical errors are replaced with the fallback`() {
        assertEquals("fallback", userMessage(IllegalStateException("FOREIGN KEY constraint failed"), "fallback"))
        assertEquals("fallback", userMessage(IllegalArgumentException(), "fallback"))
    }
}
