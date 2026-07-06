package com.example.fitapp.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.fitapp.data.local.entity.Difficulty

/**
 * Цветной чип уровня сложности: зелёный/жёлтый/красный.
 */
@Composable
fun DifficultyChip(difficulty: Difficulty, modifier: Modifier = Modifier) {
    val (bg, fg) = when (difficulty) {
        Difficulty.BEGINNER -> Color(0xFF12352E) to Color(0xFF35D8B1)
        Difficulty.INTERMEDIATE -> Color(0xFF332818) to Color(0xFFF4B63F)
        Difficulty.ADVANCED -> Color(0xFF331816) to Color(0xFFFF6A57)
    }
    Surface(
        color = bg,
        contentColor = fg,
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.dp, fg.copy(alpha = 0.35f)),
        modifier = modifier
    ) {
        Text(
            text = difficulty.displayName,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
