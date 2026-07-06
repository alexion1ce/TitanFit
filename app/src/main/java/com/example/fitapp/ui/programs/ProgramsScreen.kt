package com.example.fitapp.ui.programs

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fitapp.data.repository.WorkoutExerciseItem
import com.example.fitapp.ui.components.ExerciseArtworkThumbnail

private val Background = Color(0xFF020304)
private val Card = Color(0xFF101419)
private val CardEdge = Color(0xFF2B3038)
private val TextPrimary = Color(0xFFF5F6FA)
private val TextSecondary = Color(0xFFC1C5CF)
private val TextMuted = Color(0xFF8E949F)
private val Red = Color(0xFFFF4738)
private val RedDark = Color(0xFFE82319)
private val Teal = Color(0xFF35D8B1)
private val Green = Color(0xFF6CD05C)
private val Blue = Color(0xFF58A6FF)

@Composable
fun ProgramsScreen(
    onProgramClick: (Long) -> Unit,
    onStartProgram: (Long) -> Unit = onProgramClick,
    onMyWorkoutsClick: () -> Unit = {},
    viewModel: ProgramsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF171B21), Background),
                        center = Offset(420f, 80f),
                        radius = 520f
                    )
                )
        )

        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Red)
            }

            state.errorMessage != null -> EmptyPrograms(state.errorMessage ?: "Не удалось загрузить программы")

            state.programs.isEmpty() -> EmptyPrograms("Программы пока не добавлены")

            else -> {
                val today = state.programs.first()
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, top = 44.dp, end = 16.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Header(
                            title = "Программы"
                        )
                    }
                    item { TodayTitle() }
                    item {
                        TodayProgramCard(
                            today,
                            onClick = { onProgramClick(today.workout.id) },
                            onStartClick = { onStartProgram(today.workout.id) }
                        )
                    }
                    item { ProgramProgressCard() }
                    item { MyProgramsTitle(onAddClick = onMyWorkoutsClick) }
                    items(state.programs, key = { it.workout.id }) { card ->
                        CompactProgramCard(
                            card = card,
                            progress = progressFor(card.workout.id),
                            onClick = { onProgramClick(card.workout.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Header(title: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 38.sp,
            lineHeight = 42.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun TodayTitle() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(22.dp)
                .background(Red, RoundedCornerShape(4.dp))
        )
        Spacer(Modifier.width(10.dp))
        Text("Сегодня", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun TodayProgramCard(card: ProgramCard, onClick: () -> Unit, onStartClick: () -> Unit) {
    GlassCard(modifier = Modifier.clickable(onClick = onClick)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(58.dp),
                    color = Color.Black.copy(alpha = 0.35f),
                    shape = CircleShape,
                    border = BorderStroke(1.dp, Red)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(Icons.Outlined.FitnessCenter, contentDescription = null, tint = Red, modifier = Modifier.size(32.dp))
                    }
                }
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(card.workout.name, color = TextPrimary, fontSize = 23.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(card.workout.notes ?: "Плановая тренировка", color = TextSecondary, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        InlineMeta(Icons.Outlined.FormatListBulleted, "${card.exerciseCount} упражнения")
                        Text("|", color = TextMuted)
                        InlineMeta(Icons.Outlined.AccessTime, estimateDuration(card))
                    }
                }
                Surface(
                    color = Color.Transparent,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Red.copy(alpha = 0.35f)),
                    modifier = Modifier
                        .height(54.dp)
                        .clickable(onClick = onStartClick)
                        .background(Brush.horizontalGradient(listOf(Color(0xFFFF6A57), RedDark)), RoundedCornerShape(14.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Начать", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(18.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.White.copy(alpha = 0.14f))
            )
            Spacer(Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Упражнения", color = TextSecondary, fontSize = 15.sp, modifier = Modifier.weight(1f))
                Text("Посмотреть все", color = Red, fontSize = 15.sp)
                Icon(Icons.Outlined.KeyboardArrowRight, contentDescription = null, tint = Red)
            }
            Spacer(Modifier.height(10.dp))
            ExerciseStrip(card.exercises)
        }
    }
}

@Composable
private fun ExerciseStrip(exercises: List<WorkoutExerciseItem>) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(exercises.take(6), key = { it.workoutExerciseId }) { exercise ->
            Column(modifier = Modifier.width(128.dp)) {
                ExerciseArtworkThumbnail(
                    exerciseCode = exercise.exerciseCode,
                    primaryMuscleCode = exercise.primaryMuscleCode,
                    secondaryMuscleCode = exercise.secondaryMuscleCode,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(88.dp)
                )
                Spacer(Modifier.height(7.dp))
                Text(
                    exercise.exerciseName,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ProgramProgressCard() {
    GlassCard {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Прогресс программы", color = TextSecondary, fontSize = 16.sp, modifier = Modifier.weight(1f))
                Text("Подробнее", color = Red, fontSize = 15.sp)
                Icon(Icons.Outlined.KeyboardArrowRight, contentDescription = null, tint = Red)
            }
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                ProgressRing(progress = 0.68f, label = "68%")
                Spacer(Modifier.width(18.dp))
                ProgressMetric(Icons.Outlined.CalendarMonth, "12", "тренировок", Teal, Modifier.weight(1f))
                ProgressMetric(Icons.Outlined.LocalFireDepartment, "3 240", "ккал", Red, Modifier.weight(1f))
                ProgressMetric(Icons.Outlined.AccessTime, "8 ч 45 мин", "время", Blue, Modifier.weight(1f))
                ProgressMetric(Icons.Outlined.TrendingUp, "+12%", "прирост", Teal, Modifier.weight(1f))
            }
            Spacer(Modifier.height(18.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.68f)
                        .height(6.dp)
                        .background(Teal, RoundedCornerShape(6.dp))
                )
            }
        }
    }
}

@Composable
private fun ProgressMetric(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
        Spacer(Modifier.height(7.dp))
        Text(value, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        Text(label, color = TextSecondary, fontSize = 12.sp, maxLines = 1)
    }
}

@Composable
private fun ProgressRing(progress: Float, label: String) {
    Box(modifier = Modifier.size(82.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawArc(
                color = Color.White.copy(alpha = 0.12f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 7.dp.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                color = Teal,
                startAngle = -90f,
                sweepAngle = progress * 360f,
                useCenter = false,
                style = Stroke(width = 7.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("выполнено", color = TextSecondary, fontSize = 10.sp)
        }
    }
}

@Composable
private fun MyProgramsTitle(onAddClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Мои программы", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier.clickable(onClick = onAddClick),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Добавить", color = Red, fontSize = 16.sp)
            Spacer(Modifier.width(8.dp))
            Icon(Icons.Filled.Add, contentDescription = "Добавить", tint = Red)
        }
    }
}

@Composable
private fun CompactProgramCard(card: ProgramCard, progress: Float, onClick: () -> Unit) {
    GlassCard(modifier = Modifier.clickable(onClick = onClick)) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProgramBadge(card.workout.name)
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(card.workout.name, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(card.workout.notes ?: "Для роста мышц", color = TextSecondary, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    InlineMeta(Icons.Outlined.FormatListBulleted, "${maxOf(2, card.exerciseCount / 2)} дня в неделю")
                    Text("|", color = TextMuted)
                    InlineMeta(Icons.Outlined.AccessTime, estimateDuration(card))
                }
            }
            SmallRing(progress)
            Spacer(Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(52.dp)
                    .background(Color.White.copy(alpha = 0.14f))
            )
            Spacer(Modifier.width(10.dp))
            Icon(Icons.Outlined.MoreHoriz, contentDescription = null, tint = TextMuted)
        }
    }
}

@Composable
private fun ProgramBadge(name: String) {
    val color = when {
        name.contains("ног", ignoreCase = true) -> Blue
        name.contains("body", ignoreCase = true) || name.contains("тело", ignoreCase = true) -> Color(0xFFE1A733)
        else -> Red
    }

    Surface(
        modifier = Modifier.size(62.dp),
        color = color.copy(alpha = 0.18f),
        shape = CircleShape,
        border = BorderStroke(1.dp, color.copy(alpha = 0.55f))
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Icon(Icons.Outlined.FitnessCenter, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
        }
    }
}

@Composable
private fun SmallRing(progress: Float) {
    Box(modifier = Modifier.size(54.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawArc(
                color = Color.White.copy(alpha = 0.12f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                color = Green,
                startAngle = -90f,
                sweepAngle = progress * 360f,
                useCenter = false,
                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        Text("${(progress * 100).toInt()}%", color = TextPrimary, fontSize = 13.sp)
    }
}

@Composable
private fun InlineMeta(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(5.dp))
        Text(text, color = TextSecondary, fontSize = 14.sp, maxLines = 1)
    }
}

@Composable
private fun GlassCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Card.copy(alpha = 0.94f),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, CardEdge),
        shadowElevation = 8.dp,
        content = content
    )
}

@Composable
private fun EmptyPrograms(message: String) {
    Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Text(message, color = TextPrimary, style = MaterialTheme.typography.titleMedium)
    }
}

private fun estimateDuration(card: ProgramCard): String =
    "${(card.totalSets * 4).coerceIn(35, 75)} мин"

private fun progressFor(id: Long): Float =
    when ((id % 3).toInt()) {
        0 -> 0.75f
        1 -> 0.60f
        else -> 0.40f
    }
