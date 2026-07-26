package com.example.fitapp.ui.programs

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fitapp.data.local.entity.WorkoutLocation
import com.example.fitapp.data.repository.WorkoutExerciseItem

import com.example.fitapp.ui.components.ExerciseArtworkThumbnail

private val Background = Color(0xFF0D0F12)
private val Card = Color(0xFF171B21)
private val CardEdge = Color(0xFF2B3038)
private val TextPrimary = Color(0xFFF5F6FA)
private val TextSecondary = Color(0xFFC1C5CF)
private val TextMuted = Color(0xFF8E949F)
private val Red = Color(0xFFFF4738)
private val RedDark = Color(0xFFE82319)
private val Blue = Color(0xFF58A6FF)

@Composable
fun ProgramsScreen(
    onProgramClick: (Long) -> Unit,
    onStartProgram: (Long) -> Unit = onProgramClick,
    onMyWorkoutsClick: () -> Unit = {},
    onOpenOnboarding: () -> Unit = {},
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
                var showSelectDayDialog by remember { mutableStateOf(false) }

                if (showSelectDayDialog) {
                    WorkoutSelectionDialog(
                        programs = state.programs,
                        onDismiss = { showSelectDayDialog = false },
                        onSelect = { selectedId ->
                            showSelectDayDialog = false
                            onStartProgram(selectedId)
                        }
                    )
                }

                val today = state.programs.first()
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, top = 44.dp, end = 16.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        val currentLocation by viewModel.currentLocation.collectAsStateWithLifecycle()
                        Header(
                            title = "Программы",
                            currentLocation = currentLocation,
                            onToggleLocation = { viewModel.toggleLocation() },
                            onOpenOnboarding = onOpenOnboarding
                        )
                    }

                    item { TodayTitle(onChangeDayClick = { showSelectDayDialog = true }) }
                    item {
                        TodayProgramCard(
                            card = today,
                            onClick = { onProgramClick(today.workout.id) },
                            onStartClick = { showSelectDayDialog = true }
                        )
                    }
                    item { MyProgramsTitle(onAddClick = onMyWorkoutsClick) }
                    items(state.programs, key = { it.workout.id }) { card ->
                        CompactProgramCard(
                            card = card,
                            onClick = { onProgramClick(card.workout.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Header(
    title: String,
    currentLocation: WorkoutLocation,
    onToggleLocation: () -> Unit,
    onOpenOnboarding: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val locLabel = when (currentLocation) {
                    WorkoutLocation.GYM -> "🏋️ Зал"
                    WorkoutLocation.HOME_DUMBBELLS -> "🏋️ Гантели"
                    WorkoutLocation.HOME_BODYWEIGHT -> "🏠 Свой вес"
                }
                Surface(
                    modifier = Modifier
                        .clickable(onClick = onToggleLocation)
                        .background(Color(0xFF171B21), RoundedCornerShape(12.dp)),
                    color = Color.Transparent,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, CardEdge)
                ) {
                    Text(
                        locLabel,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                    )
                }

                Surface(
                    modifier = Modifier
                        .clickable(onClick = onOpenOnboarding)
                        .background(Color(0xFF171B21), RoundedCornerShape(12.dp)),
                    color = Color.Transparent,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, CardEdge)
                ) {
                    Text(
                        "Анкета",
                        color = Red,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TodayTitle(onChangeDayClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(22.dp)
                .background(Red, RoundedCornerShape(4.dp))
        )
        Spacer(Modifier.width(10.dp))
        Text("Сегодня", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))

        Surface(
            modifier = Modifier
                .clickable(onClick = onChangeDayClick)
                .background(Color(0xFF171B21), RoundedCornerShape(10.dp)),
            color = Color.Transparent,
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, CardEdge)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.CalendarMonth, contentDescription = null, tint = Red, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Выбрать день", color = Red, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun TodayProgramCard(card: ProgramCard, onClick: () -> Unit, onStartClick: () -> Unit) {
    GlassCard(modifier = Modifier.clickable(onClick = onClick)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    modifier = Modifier.size(52.dp),
                    color = Color.Black.copy(alpha = 0.35f),
                    shape = CircleShape,
                    border = BorderStroke(1.dp, Red)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(Icons.Outlined.FitnessCenter, contentDescription = null, tint = Red, modifier = Modifier.size(28.dp))
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        card.workout.name,
                        color = TextPrimary,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        card.workout.notes ?: "Плановая тренировка",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    InlineMeta(Icons.AutoMirrored.Outlined.FormatListBulleted, "${card.exerciseCount} упр.")
                    Text("·", color = TextMuted)
                    InlineMeta(Icons.Outlined.AccessTime, estimateDuration(card))
                }

                Surface(
                    color = Color.Transparent,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Red.copy(alpha = 0.35f)),
                    modifier = Modifier
                        .height(44.dp)
                        .clickable(onClick = onStartClick)
                        .background(Brush.horizontalGradient(listOf(Color(0xFFFF6A57), RedDark)), RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Начать", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.White.copy(alpha = 0.12f))
            )
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Упражнения", color = TextSecondary, fontSize = 14.sp, modifier = Modifier.weight(1f))
                Text("Посмотреть все", color = Red, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, contentDescription = null, tint = Red)
            }
            Spacer(Modifier.height(10.dp))
            ExerciseStrip(card.exercises)
        }
    }
}

@Composable
private fun ExerciseStrip(exercises: List<WorkoutExerciseItem>) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        items(exercises.take(6), key = { it.workoutExerciseId }) { exercise ->
            Column(modifier = Modifier.width(112.dp)) {
                ExerciseArtworkThumbnail(
                    exerciseCode = exercise.exerciseCode,
                    primaryMuscleCode = exercise.primaryMuscleCode,
                    secondaryMuscleCode = exercise.secondaryMuscleCode,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(82.dp)
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    exercise.exerciseName,
                    color = TextPrimary,
                    fontSize = 12.sp,
                    lineHeight = 15.sp,
                    maxLines = 2,
                    minLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
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
        Text("Мои программы", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier.clickable(onClick = onAddClick),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Добавить", color = Red, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.width(6.dp))
            Icon(Icons.Filled.Add, contentDescription = "Добавить", tint = Red)
        }
    }
}

@Composable
private fun CompactProgramCard(card: ProgramCard, onClick: () -> Unit) {
    GlassCard(modifier = Modifier.clickable(onClick = onClick)) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProgramBadge(card.workout.name)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    card.workout.name,
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    card.workout.notes ?: "Для роста мышц",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    InlineMeta(Icons.AutoMirrored.Outlined.FormatListBulleted, "${card.exerciseCount} упр. (${card.totalSets} сет.)")
                    Text("·", color = TextMuted)
                    InlineMeta(Icons.Outlined.AccessTime, estimateDuration(card))
                }
            }
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
        modifier = Modifier.size(54.dp),
        color = color.copy(alpha = 0.18f),
        shape = CircleShape,
        border = BorderStroke(1.dp, color.copy(alpha = 0.55f))
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Icon(Icons.Outlined.FitnessCenter, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
private fun InlineMeta(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(4.dp))
        Text(text, color = TextSecondary, fontSize = 13.sp, maxLines = 1)
    }
}

@Composable
private fun GlassCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.Transparent,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(
            1.dp,
            Brush.linearGradient(
                colors = listOf(Color(0xFF3B4352), Red.copy(alpha = 0.35f))
            )
        ),
        shadowElevation = 8.dp
    ) {
        Box(
            modifier = Modifier.background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF1B202A), Color(0xFF13171E))
                )
            )
        ) {
            content()
        }
    }
}


@Composable
private fun EmptyPrograms(message: String) {
    Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Text(message, color = TextPrimary, style = MaterialTheme.typography.titleMedium)
    }
}

private fun estimateDuration(card: ProgramCard): String =
    "${(card.totalSets * 4).coerceIn(35, 75)} мин"

@Composable
private fun WorkoutSelectionDialog(
    programs: List<ProgramCard>,
    onDismiss: () -> Unit,
    onSelect: (Long) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Какая сегодня тренировка?",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Выберите тренировочный день из вашего текущего сплита:",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(14.dp))
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.heightIn(max = 320.dp)
                ) {
                    items(programs, key = { it.workout.id }) { card ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(card.workout.id) },
                            color = Color(0xFF171B21),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, CardEdge)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        card.workout.name,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(Modifier.height(3.dp))
                                    Text(
                                        "${card.exerciseCount} упражнений • ${estimateDuration(card)}",
                                        color = TextSecondary,
                                        fontSize = 13.sp
                                    )
                                }
                                Icon(
                                    Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                                    contentDescription = null,
                                    tint = Red
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена", color = Red, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = Background,
        shape = RoundedCornerShape(20.dp)
    )
}
