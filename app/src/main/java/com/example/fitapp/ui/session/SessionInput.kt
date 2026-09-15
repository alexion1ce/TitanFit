package com.example.fitapp.ui.session

import com.example.fitapp.data.local.entity.SetLog

internal fun parseSetWeight(text: String): Double? = text.trim().replace(',', '.')
    .toDoubleOrNull()?.takeIf { it.isFinite() && it >= 0.0 }

internal fun parseSetCount(text: String): Int? = text.trim().toIntOrNull()?.takeIf { it > 0 }

data class SetInputDraft(val weight: String, val count: String) {
    fun isValid(timed: Boolean): Boolean = parseSetCount(count) != null &&
        (timed || parseSetWeight(weight) != null)

    companion object {
        fun from(set: SetLog) = SetInputDraft(
            if (set.weight == set.weight.toLong().toDouble()) set.weight.toLong().toString() else set.weight.toString(),
            (set.durationSeconds ?: set.reps).toString()
        )
    }
}
