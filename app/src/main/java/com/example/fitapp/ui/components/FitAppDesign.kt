package com.example.fitapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ===== Cyber Titanium & Crimson Glow (Вариант 1) =====
val FitScreenBackground = Color(0xFF0D0F12)
val FitHeaderDark = Color(0xFF0D0F12)
val FitHeaderSoft = Color(0xFF1E2430)
val FitNavDark = Color(0xFF12151B)
val FitCardWhite = Color(0xFF171B21)
val FitCardBorder = Color(0xFF2B3038)
val FitInk = Color(0xFFF5F6FA)
val FitMuted = Color(0xFFC1C5CF)
val FitMutedLight = Color(0xFF9EA3AF)
val FitAccentRed = Color(0xFFFF3B30)
val FitAccentRedDark = Color(0xFFD32F2F)
val FitAccentTeal = Color(0xFF00E5FF)
val FitAccentGreen = Color(0xFF30D158)
val FitAccentBlue = Color(0xFF0A84FF)

val FitCardShape = RoundedCornerShape(20.dp)
val FitChipShape = RoundedCornerShape(10.dp)

// ===== Brushed Metal Plate Design Tokens (Отполированный стальной титан из Скрина 1) =====
val FitMetalPlateGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFE5EAEE), // Верхний светлый отблик отполированной стали
        Color(0xFFB7BFCB), // Серый металлик
        Color(0xFF88909D), // Шлифованная сталь
        Color(0xFF5E6573)  // Тёмный нижний контур метала
    )
)

val FitMetalPlateBorder = BorderStroke(
    1.5.dp,
    Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFFFFFF).copy(alpha = 0.9f),
            Color(0xFF808998),
            Color(0xFF353B47)
        )
    )
)

@Composable
fun FitBrushedSteelCard(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        color = Color.Transparent,
        shape = FitCardShape,
        border = FitMetalPlateBorder,
        shadowElevation = 12.dp
    ) {
        Column(
            modifier = Modifier
                .background(FitMetalPlateGradient)
                .padding(contentPadding),
            content = content
        )
    }
}

@Composable
fun FitCyanPill(text: String, modifier: Modifier = Modifier) {
    Surface(
        color = FitAccentTeal,
        shape = CircleShape,
        shadowElevation = 4.dp,
        modifier = modifier
    ) {
        Text(
            text = text,
            color = Color(0xFF07131B),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun FitDarkPill(text: String, modifier: Modifier = Modifier) {
    Surface(
        color = Color(0xFF171B22),
        shape = CircleShape,
        shadowElevation = 3.dp,
        modifier = modifier
    ) {
        Text(
            text = text,
            color = Color(0xFFEEF2F8),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun FitSubPill(text: String, modifier: Modifier = Modifier) {
    Surface(
        color = Color(0xFF282F3B).copy(alpha = 0.9f),
        shape = CircleShape,
        shadowElevation = 2.dp,
        modifier = modifier
    ) {
        Text(
            text = text,
            color = Color(0xFFF0F4FA),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
        )
    }
}

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
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitle != null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.72f),
                    fontSize = 14.sp,
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
        color = Color.Transparent,
        shape = FitCardShape,
        border = BorderStroke(
            1.dp,
            Brush.linearGradient(
                colors = listOf(Color(0xFF353C4A), FitAccentRed.copy(alpha = 0.35f))
            )
        ),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF1B202A), Color(0xFF13171E))
                    )
                )
                .padding(contentPadding),
            content = content
        )
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
        Box(
            modifier = Modifier
                .height(20.dp)
                .background(FitAccentRed, RoundedCornerShape(4.dp))
                .padding(horizontal = 2.dp)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = title,
            color = FitInk,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        )
        if (action != null) {
            Surface(
                color = FitAccentRed.copy(alpha = 0.16f),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(0.5.dp, FitAccentRed.copy(alpha = 0.3f))
            ) {
                Text(
                    text = action,
                    color = FitAccentRed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}
