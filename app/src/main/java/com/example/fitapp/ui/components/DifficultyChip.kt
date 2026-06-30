package com.example.fitapp.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
        Difficulty.BEGINNER -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        Difficulty.INTERMEDIATE -> Color(0xFFFFF8E1) to Color(0xFFEF6C00)
        Difficulty.ADVANCED -> Color(0xFFFFEBEE) to Color(0xFFC62828)
    }
    Surface(
        color = bg,
        contentColor = fg,
        shape = RoundedCornerShape(50),
        modifier = modifier
    ) {
        Text(
            text = difficulty.displayName,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
