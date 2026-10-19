package com.example.fitapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp

@Composable
fun MuscleMap(
    primaryMuscleCode: String,
    secondaryMuscleCode: String?,
    modifier: Modifier = Modifier
) {
    val bodyColor = MaterialTheme.colorScheme.surfaceVariant
    val outlineColor = MaterialTheme.colorScheme.outlineVariant
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.tertiary

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Box(Modifier.fillMaxSize()) {
            Canvas(Modifier.fillMaxSize()) {
                val muscleColor: (String) -> Color = { code ->
                    when (code) {
                        primaryMuscleCode -> primaryColor
                        secondaryMuscleCode -> secondaryColor
                        else -> bodyColor
                    }
                }
                drawMuscleFigure(size.width * 0.32f, bodyColor, outlineColor, muscleColor, isBack = false)
                drawMuscleFigure(size.width * 0.68f, bodyColor, outlineColor, muscleColor, isBack = true)
            }
        }
    }
}

@Composable
fun MuscleMapHero(
    primaryMuscleCode: String,
    secondaryMuscleCode: String?,
    modifier: Modifier = Modifier
) {
    MuscleMap(
        primaryMuscleCode = primaryMuscleCode,
        secondaryMuscleCode = secondaryMuscleCode,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
    )
}

/**
 * Front and back figures with several muscles highlighted at once, e.g. recent load:
 * [fresh] muscles use the primary color, [recovering] ones the tertiary color.
 */
@Composable
fun MuscleLoadMap(
    fresh: Set<String>,
    recovering: Set<String>,
    modifier: Modifier = Modifier,
    // Lighter than surfaceVariant so idle muscles stay visible on dark cards.
    bodyColor: Color = Color(0xFF2E3542)
) {
    val outlineColor = MaterialTheme.colorScheme.outlineVariant
    val freshColor = MaterialTheme.colorScheme.primary
    val recoveringColor = MaterialTheme.colorScheme.tertiary

    Canvas(modifier) {
        val muscleColor: (String) -> Color = { code ->
            when (code) {
                in fresh -> freshColor
                in recovering -> recoveringColor
                else -> bodyColor
            }
        }
        drawMuscleFigure(size.width * 0.32f, bodyColor, outlineColor, muscleColor, isBack = false)
        drawMuscleFigure(size.width * 0.68f, bodyColor, outlineColor, muscleColor, isBack = true)
    }
}

private fun DrawScope.drawMuscleFigure(
    centerX: Float,
    bodyColor: Color,
    outlineColor: Color,
    muscleColor: (String) -> Color,
    isBack: Boolean
) {
    val scale = minOf(size.width, size.height) / 260f
    val top = size.height * 0.12f

    fun rounded(x: Float, y: Float, w: Float, h: Float, color: Color, radius: Float = 12f) {
        drawRoundRect(
            color = color,
            topLeft = Offset(centerX + x * scale, top + y * scale),
            size = Size(w * scale, h * scale),
            cornerRadius = CornerRadius(radius * scale, radius * scale)
        )
    }

    fun oval(x: Float, y: Float, w: Float, h: Float, color: Color) {
        drawOval(
            color = color,
            topLeft = Offset(centerX + x * scale, top + y * scale),
            size = Size(w * scale, h * scale)
        )
    }

    drawCircle(bodyColor, radius = 18f * scale, center = Offset(centerX, top + 18f * scale))
    rounded(-34f, 42f, 68f, 86f, bodyColor, 18f)
    rounded(-56f, 48f, 20f, 88f, bodyColor, 10f)
    rounded(36f, 48f, 20f, 88f, bodyColor, 10f)
    rounded(-30f, 128f, 24f, 88f, bodyColor, 10f)
    rounded(6f, 128f, 24f, 88f, bodyColor, 10f)

    if (isBack) {
        rounded(-30f, 50f, 60f, 58f, muscleColor("back"), 16f)
        oval(-36f, 112f, 32f, 28f, muscleColor("glutes"))
        oval(4f, 112f, 32f, 28f, muscleColor("glutes"))
        rounded(-52f, 48f, 24f, 24f, muscleColor("shoulders"), 12f)
        rounded(28f, 48f, 24f, 24f, muscleColor("shoulders"), 12f)
        rounded(-58f, 74f, 18f, 44f, muscleColor("triceps"), 9f)
        rounded(40f, 74f, 18f, 44f, muscleColor("triceps"), 9f)
    } else {
        oval(-32f, 52f, 32f, 30f, muscleColor("chest"))
        oval(0f, 52f, 32f, 30f, muscleColor("chest"))
        rounded(-14f, 82f, 28f, 42f, muscleColor("abs"), 8f)
        rounded(-52f, 48f, 24f, 24f, muscleColor("shoulders"), 12f)
        rounded(28f, 48f, 24f, 24f, muscleColor("shoulders"), 12f)
        rounded(-58f, 74f, 18f, 44f, muscleColor("biceps"), 9f)
        rounded(40f, 74f, 18f, 44f, muscleColor("biceps"), 9f)
    }

    rounded(-30f, 132f, 24f, 80f, muscleColor("legs"), 10f)
    rounded(6f, 132f, 24f, 80f, muscleColor("legs"), 10f)

    drawLine(
        color = outlineColor,
        start = Offset(centerX, top + 42f * scale),
        end = Offset(centerX, top + 216f * scale),
        strokeWidth = 1f * scale
    )
}
