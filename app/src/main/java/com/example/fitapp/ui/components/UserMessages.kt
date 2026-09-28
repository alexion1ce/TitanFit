package com.example.fitapp.ui.components

/**
 * Validation errors carry Russian messages written for the user (see `require`
 * calls in repositories); anything else gets a neutral [fallback] text.
 */
fun userMessage(e: Exception, fallback: String): String =
    (e as? IllegalArgumentException)?.message?.takeIf { it.isNotBlank() } ?: fallback
