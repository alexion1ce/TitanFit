package com.example.fitapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val FitScreenBackground = Color(0xFF020304)
val FitHeaderDark = Color(0xFF020304)
val FitHeaderSoft = Color(0xFF171B21)
val FitNavDark = Color(0xFF101115)
val FitCardWhite = Color(0xFF101419)
val FitCardBorder = Color(0xFF2B3038)
val FitInk = Color(0xFFF5F6FA)
val FitMuted = Color(0xFFC1C5CF)
val FitMutedLight = Color(0xFFB9BBC2)
val FitAccentRed = Color(0xFFFF4738)
val FitAccentRedDark = Color(0xFFE82319)
val FitAccentTeal = Color(0xFF35D8B1)
val FitAccentGreen = Color(0xFF3BBF66)
val FitAccentBlue = Color(0xFF58A6FF)

val FitCardShape = RoundedCornerShape(18.dp)
val FitChipShape = RoundedCornerShape(10.dp)

@Composable
fun FitHybridScreen(
    modifier: Modifier = Modifier,
    headerHeight: Dp = 190.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(FitScreenBackground)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(headerHeight)
                .background(
                    Brush.radialGradient(
                        colors = listOf(FitHeaderSoft, FitHeaderDark),
                        center = androidx.compose.ui.geometry.Offset(420f, 80f),
                        radius = 540f
                    )
                )
        )
        content()
    }
}

@Composable
fun FitScreenHeader(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Color.White,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitle != null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.72f),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, content = actions)
    }
}

@Composable
fun FitSurfaceCard(
    modifier: Modifier = Modifier,
    color: Color = FitCardWhite,
    borderColor: Color = FitCardBorder,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = color,
        shape = FitCardShape,
        border = BorderStroke(1.dp, borderColor),
        shadowElevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(contentPadding), content = content)
    }
}

@Composable
fun FitSectionTitle(
    title: String,
    modifier: Modifier = Modifier,
    action: String? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = FitInk,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        if (action != null) {
            Text(
                text = action,
                color = FitAccentRedDark,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
