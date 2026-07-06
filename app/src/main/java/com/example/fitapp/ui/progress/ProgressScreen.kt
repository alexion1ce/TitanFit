package com.example.fitapp.ui.progress

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Scale
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fitapp.data.repository.PersonalRecord
import com.example.fitapp.data.repository.RecentWorkoutSummary
import com.example.fitapp.data.repository.WeeklyVolume
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max

private val ScreenBackground = Color(0xFF020304)
private val HeaderSoft = Color(0xFF171B21)
private val AccentRed = Color(0xFFFF4738)
private val AccentRedDark = Color(0xFFE82319)
private val AccentTeal = Color(0xFF35D6B0)
private val Card = Color(0xFF101419)
private val CardAlt = Color(0xFF171B21)
private val CardEdge = Color(0xFF2B3038)
private val Ink = Color(0xFFF5F6FA)
private val Muted = Color(0xFFC1C5CF)

@Composable
fun ProgressScreen(
    onEntryClick: (Long) -> Unit = {},
    viewModel: ProgressViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showResetDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(255.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(ScreenBackground, HeaderSoft, ScreenBackground),
                        endY = 520f
                    )
                )
        )

        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AccentRed)
            }

            state.errorMessage != null -> CenterMessage(
                title = "Не удалось загрузить прогресс",
                body = state.errorMessage ?: "Попробуйте открыть экран позже"
            )

            state.stats.totalWorkouts == 0 -> CenterMessage(
                title = "Нет данных для прогресса",
                body = "Завершите тренировку, чтобы увидеть статистику"
            )

            else -> ProgressContent(
                state = state,
                onEntryClick = onEntryClick,
                onResetClick = { showResetDialog = true },
                onPeriodSelected = viewModel::onPeriodSelected,
                onToggleRecent = viewModel::toggleAllRecent,
                onToggleRecords = viewModel::toggleAllRecords
            )
        }

        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = { Text("Сбросить прогресс?") },
                text = { Text("Будут удалены записи журнала и статистика прогресса. Программы и упражнения останутся.") },
                confirmButton = {
                    Button(
                        enabled = !state.isResetting,
                        onClick = {
                            showResetDialog = false
                            viewModel.resetProgress()
                        }
                    ) {
                        Text("Сбросить")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) {
                        Text("Отмена")
                    }
                }
            )
        }
    }
}

@Composable
private fun ProgressContent(
    state: ProgressUiState,
    onEntryClick: (Long) -> Unit,
    onResetClick: () -> Unit,
    onPeriodSelected: (ProgressPeriod) -> Unit,
    onToggleRecent: () -> Unit,
    onToggleRecords: () -> Unit
) {
    val visibleRecent = if (state.showAllRecent) state.recentWorkouts else state.recentWorkouts.take(3)
    val visibleRecords = if (state.showAllRecords) state.records else state.records.take(2)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, top = 42.dp, end = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { ProgressHeader(onResetClick = onResetClick, resetEnabled = !state.isResetting) }
        item { PeriodTabs(state.selectedPeriod, onPeriodSelected) }
        item {
            VolumeAnalyticsCard(
                weekly = state.weeklyVolume,
                totalVolume = state.stats.totalVolume,
                totalWorkouts = state.stats.totalWorkouts,
                totalMinutes = state.stats.totalMinutes
            )
        }
        if (visibleRecent.isNotEmpty()) {
            item {
                SectionTitle(
                    title = "Последние тренировки",
                    action = if (state.showAllRecent) "Скрыть" else "См. все",
                    onAction = onToggleRecent
                )
            }
            items(visibleRecent, key = { it.logId }) { workout ->
                RecentWorkoutRow(workout, onClick = { onEntryClick(workout.logId) })
            }
        }
        if (visibleRecords.isNotEmpty()) {
            item {
                SectionTitle(
                    title = "Личные рекорды",
                    action = if (state.showAllRecords) "Скрыть" else "См. все",
                    onAction = onToggleRecords
                )
            }
            item { RecordsRow(visibleRecords) }
        }
        item {
            KpiCards(
                totalWorkouts = state.stats.totalWorkouts,
                records = state.records.size,
                totalVolume = state.stats.totalVolume
            )
        }
    }
}

@Composable
private fun ProgressHeader(onResetClick: () -> Unit, resetEnabled: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Прогресс",
            color = Color.White,
            fontSize = 34.sp,
            lineHeight = 38.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        TextButton(enabled = resetEnabled, onClick = onResetClick) {
            Icon(Icons.Outlined.Delete, contentDescription = null, tint = AccentRed, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(6.dp))
            Text("Сброс", color = AccentRed)
        }
    }
}

@Composable
private fun PeriodTabs(
    selectedPeriod: ProgressPeriod,
    onPeriodSelected: (ProgressPeriod) -> Unit
) {
    val tabs = listOf(
        ProgressPeriod.WEEK to "Эта неделя",
        ProgressPeriod.FOUR_WEEKS to "4 недели",
        ProgressPeriod.TWELVE_WEEKS to "12 недель",
        ProgressPeriod.YEAR to "Год"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        tabs.forEach { (period, label) ->
            val selected = selectedPeriod == period
            Surface(
                modifier = Modifier.clickable { onPeriodSelected(period) },
                color = if (selected) AccentRed else Color.Transparent,
                shape = RoundedCornerShape(22.dp)
            ) {
                Text(
                    label,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    color = if (selected) Color.White else Color.White.copy(alpha = 0.88f),
                    fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                    fontSize = 15.sp,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun VolumeAnalyticsCard(
    weekly: List<WeeklyVolume>,
    totalVolume: Double,
    totalWorkouts: Int,
    totalMinutes: Int
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Card,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 5.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Объем", color = Ink, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "${formatNumber(totalVolume)} кг",
                        color = AccentRedDark,
                        fontSize = 32.sp,
                        lineHeight = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Всего за выбранный период", color = Muted, style = MaterialTheme.typography.bodyMedium)
                }
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = CardAlt,
                    border = BorderStroke(1.dp, CardEdge)
                ) {
                    Text(
                        "кг",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        color = Ink,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(Modifier.height(18.dp))
            WeeklyVolumeChart(weekly)
            Spacer(Modifier.height(14.dp))
            AnalyticsStatsRow(
                totalVolume = totalVolume,
                totalWorkouts = totalWorkouts,
                calories = (totalVolume * 0.08).toInt(),
                totalMinutes = totalMinutes
            )
        }
    }
}

@Composable
private fun WeeklyVolumeChart(weekly: List<WeeklyVolume>) {
    val visible = weekly.takeLast(7)
    val maxValue = max(visible.maxOfOrNull { it.totalVolume } ?: 1.0, 1.0)
    val labels = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")

    Column {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            val count = 7
            val chartHeight = size.height - 24f
            val step = size.width / count
            val barWidth = step * 0.42f

            repeat(4) { line ->
                val y = chartHeight * line / 3f
                drawLine(
                    color = CardEdge,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1f
                )
            }

            repeat(count) { index ->
                val volume = visible.getOrNull(index)?.totalVolume ?: 0.0
                val barHeight = ((volume / maxValue) * (chartHeight - 8f)).toFloat()
                val x = index * step + (step - barWidth) / 2f
                val y = chartHeight - barHeight
                if (volume > 0.0) {
                    drawRoundRect(
                        brush = Brush.verticalGradient(listOf(Color(0xFFFF7A70), AccentRedDark)),
                        topLeft = Offset(x, y),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(5f, 5f)
                    )
                } else {
                    drawLine(
                        color = Color(0xFF8E949F),
                        start = Offset(x + barWidth * 0.25f, chartHeight - 2f),
                        end = Offset(x + barWidth * 0.75f, chartHeight - 2f),
                        strokeWidth = 2f
                    )
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            labels.forEach { label ->
                Text(label, color = Ink, fontSize = 12.sp, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
private fun AnalyticsStatsRow(
    totalVolume: Double,
    totalWorkouts: Int,
    calories: Int,
    totalMinutes: Int
) {
    Surface(
        color = CardAlt,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, CardEdge)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            AnalyticsMetric(Icons.Outlined.Scale, formatNumber(totalVolume), "Объем (кг)", AccentRedDark)
            AnalyticsMetric(Icons.Outlined.FitnessCenter, totalWorkouts.toString(), "Тренировок", Color(0xFFF28B1D))
            AnalyticsMetric(Icons.Outlined.LocalFireDepartment, calories.toString(), "Ккал", AccentRed)
            AnalyticsMetric(Icons.Outlined.Timer, formatDuration(totalMinutes), "Длительность", Color(0xFF24BFA3))
        }
    }
}

@Composable
private fun AnalyticsMetric(icon: ImageVector, value: String, label: String, tint: Color) {
    Column(
        modifier = Modifier.width(76.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(23.dp))
        Spacer(Modifier.height(5.dp))
        Text(value, color = Ink, fontWeight = FontWeight.Bold, maxLines = 1, fontSize = 15.sp)
        Text(label, color = Muted, maxLines = 1, fontSize = 11.sp, textAlign = TextAlign.Center)
    }
}

@Composable
private fun SectionTitle(title: String, action: String, onAction: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = Ink, fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        Text(
            action,
            modifier = Modifier
                .clickable(onClick = onAction)
                .padding(8.dp),
            color = AccentRedDark,
            fontSize = 15.sp
        )
    }
}

@Composable
private fun RecentWorkoutRow(workout: RecentWorkoutSummary, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = Card,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 3.dp
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                color = Color(0xFF020304),
                shape = RoundedCornerShape(10.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(Icons.Outlined.FitnessCenter, contentDescription = null, tint = AccentRed, modifier = Modifier.size(30.dp))
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(workout.workoutName, color = Ink, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${workout.exerciseCount} упражнений", color = Muted, fontSize = 13.sp)
                Text(formatDuration(workout.durationMin), color = Muted, fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${formatNumber(workout.totalVolume)} кг", color = Ink, fontWeight = FontWeight.Medium)
                Text("${workout.caloriesEstimate} ккал", color = Muted, fontSize = 13.sp)
            }
            Icon(Icons.Outlined.KeyboardArrowRight, contentDescription = null, tint = Muted)
        }
    }
}

@Composable
private fun RecordsRow(records: List<PersonalRecord>) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(records, key = { it.exerciseId }) { record ->
            RecordMiniCard(record)
        }
    }
}

@Composable
private fun RecordMiniCard(record: PersonalRecord) {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale("ru"))

    Surface(
        modifier = Modifier.width(190.dp),
        color = CardAlt,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(62.dp),
                color = Color(0xFF331816),
                shape = CircleShape,
                border = BorderStroke(1.dp, AccentRed.copy(alpha = 0.35f))
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(record.muscleEmoji, fontSize = 26.sp)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(record.exerciseName, color = Ink, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${formatWeight(record.maxWeight)} кг", color = AccentRedDark, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("1ПМ", color = Ink, fontSize = 13.sp)
                Text(dateFormat.format(Date(record.date)), color = Muted, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun KpiCards(totalWorkouts: Int, records: Int, totalVolume: Double) {
    val completion = if (totalVolume > 0.0) (60 + (totalVolume / 1000).toInt()).coerceAtMost(95) else 0

    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        item { DarkKpiCard(Icons.Outlined.TrendingUp, "$totalWorkouts", "Тренировок", AccentRed) }
        item { DarkKpiCard(Icons.Outlined.FitnessCenter, "$completion%", "Выполнение", AccentTeal) }
        item { DarkKpiCard(Icons.Outlined.WorkspacePremium, records.toString(), "Рекордов", Color(0xFFF4B63F)) }
        item { DarkKpiCard(Icons.Outlined.Scale, formatNumber(totalVolume), "Объем", Color(0xFF4EA1FF)) }
    }
}

@Composable
private fun DarkKpiCard(icon: ImageVector, value: String, label: String, accent: Color) {
    Surface(
        modifier = Modifier.width(145.dp),
        color = Color(0xFF15191F),
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(26.dp))
                Spacer(Modifier.width(10.dp))
                Text(value, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            }
            Text(label, color = Color.White.copy(alpha = 0.86f), fontSize = 12.sp)
            Spacer(Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(if (value.endsWith("%")) 0.85f else 0.7f)
                        .height(5.dp)
                        .background(accent, RoundedCornerShape(8.dp))
                )
            }
        }
    }
}

@Composable
private fun CenterMessage(title: String, body: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF15191F),
            shape = RoundedCornerShape(18.dp),
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressRing(0.68f)
                Spacer(Modifier.height(18.dp))
                Text(title, color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Text(body, color = Color.White.copy(alpha = 0.74f), textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
private fun CircularProgressRing(progress: Float) {
    Box(modifier = Modifier.size(86.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawArc(
                color = Color.White.copy(alpha = 0.14f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 9.dp.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                color = AccentTeal,
                startAngle = -90f,
                sweepAngle = progress * 360f,
                useCenter = false,
                style = Stroke(width = 9.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        Text("${(progress * 100).toInt()}%", color = Color.White, fontWeight = FontWeight.Bold)
    }
}

private fun formatDuration(totalMinutes: Int): String {
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return if (hours > 0) "${hours} ч ${minutes} мин" else "${minutes} мин"
}

private fun formatWeight(value: Double): String =
    if (value == value.toLong().toDouble()) value.toLong().toString() else "%.1f".format(Locale.US, value)

private fun formatNumber(value: Double): String {
    val longVal = value.toLong()
    val raw = if (value == longVal.toDouble()) longVal.toString() else "%.1f".format(Locale.US, value)
    return raw.reversed().chunked(3).joinToString(" ").reversed()
}
