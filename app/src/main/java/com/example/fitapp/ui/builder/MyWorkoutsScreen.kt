package com.example.fitapp.ui.builder

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fitapp.data.local.entity.Workout
import com.example.fitapp.ui.components.ExerciseArtworkThumbnail
import com.example.fitapp.ui.components.FitAccentRed
import com.example.fitapp.ui.components.FitAccentRedDark
import com.example.fitapp.ui.components.FitCardBorder
import com.example.fitapp.ui.components.FitCardWhite
import com.example.fitapp.ui.components.FitHeaderSoft
import com.example.fitapp.ui.components.FitInk
import com.example.fitapp.ui.components.FitMuted
import com.example.fitapp.ui.components.FitScreenBackground

private val CardPremium = Brush.linearGradient(
    listOf(Color(0xFF3A3444), Color(0xFF242732), Color(0xFF15191F))
)
private val RedGradient = Brush.linearGradient(
    listOf(Color(0xFFFF6656), Color(0xFFFF332D))
)
private val TealGradient = Brush.linearGradient(
    listOf(Color(0xFF19A98E), Color(0xFF075240))
)

@Composable
fun MyWorkoutsScreen(
    onCreateWorkout: () -> Unit,
    onEditWorkout: (Long) -> Unit,
    onStartWorkout: (Long) -> Unit,
    viewModel: MyWorkoutsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var deleteTarget by remember { mutableStateOf<Workout?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FitScreenBackground)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(236.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(FitHeaderSoft, Color(0xFF080B10), FitScreenBackground)
                    )
                )
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, top = 88.dp, end = 16.dp, bottom = 150.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Header(
                    workoutCount = state.workoutCards.size,
                    latestName = state.workoutCards.firstOrNull()?.workout?.name,
                    onCreateWorkout = onCreateWorkout
                )
            }

            if (state.workoutCards.isEmpty()) {
                item {
                    EmptyWorkouts(onCreateWorkout = onCreateWorkout)
                }
            } else {
                itemsIndexed(state.workoutCards, key = { _, card -> card.workout.id }) { index, card ->
                    WorkoutCard(
                        card = card,
                        isFeatured = index == 0,
                        onStart = { onStartWorkout(card.workout.id) },
                        onEdit = { onEditWorkout(card.workout.id) },
                        onDelete = { deleteTarget = card.workout }
                    )
                }
            }

            item {
                CreateWorkoutButton(onClick = onCreateWorkout)
            }
        }

        deleteTarget?.let { workout ->
            DeleteConfirmDialog(
                name = workout.name,
                onConfirm = {
                    viewModel.deleteWorkout(workout.id)
                    deleteTarget = null
                },
                onDismiss = { deleteTarget = null }
            )
        }
    }
}

@Composable
private fun Header(
    workoutCount: Int,
    latestName: String?,
    onCreateWorkout: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Column {
            Text(
                text = "Мои тренировки",
                color = Color.White,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Выбери план на сегодня",
                color = Color.White.copy(alpha = 0.72f),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
            )
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF10161E),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, Color(0xFF252D39))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(44.dp),
                    color = Color(0xFF26191A),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = FitAccentRed)
                    }
                }
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "$workoutCount тренировок",
                        color = FitInk,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = latestName?.let { "Последняя: $it" } ?: "Создай первую программу",
                        color = FitMuted,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                TextButton(onClick = onCreateWorkout) {
                    Text("+ Быстрый план", color = Color.White.copy(alpha = 0.88f))
                }
            }
        }
    }
}

@Composable
private fun WorkoutCard(
    card: MyWorkoutCardUi,
    isFeatured: Boolean,
    onStart: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val workout = card.workout

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        color = Color.Transparent,
        shadowElevation = if (isFeatured) 14.dp else 8.dp,
        border = BorderStroke(1.dp, if (isFeatured) Color(0xFF37313F) else FitCardBorder)
    ) {
        Box(
            modifier = Modifier
                .background(if (isFeatured) CardPremium else Brush.linearGradient(listOf(FitCardWhite, Color(0xFF15191F))))
                .padding(20.dp)
        ) {
            if (isFeatured) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .width(5.dp)
                        .height(150.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(RedGradient)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = if (isFeatured) 12.dp else 0.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(70.dp),
                        color = if (isFeatured) Color(0xFF0D5142) else Color(0xFF10262D),
                        shape = RoundedCornerShape(22.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (card.exerciseCode.isNotBlank() && card.primaryMuscleCode.isNotBlank()) {
                                ExerciseArtworkThumbnail(
                                    exerciseCode = card.exerciseCode,
                                    primaryMuscleCode = card.primaryMuscleCode,
                                    secondaryMuscleCode = card.secondaryMuscleCode,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Outlined.EditNote,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.width(18.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = workout.name,
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = workout.notes?.takeIf { it.isNotBlank() } ?: "${card.muscleEmoji} ${card.muscleSummary}",
                            color = FitMuted,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .size(58.dp)
                            .clickable(onClick = onStart),
                        color = Color.Transparent,
                        shape = CircleShape
                    ) {
                        Box(
                            modifier = Modifier.background(TealGradient),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Начать тренировку",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    InfoChip(text = "${card.exerciseCount} упр.")
                    InfoChip(text = card.muscleSummary)
                    InfoChip(text = if (workout.notes.isNullOrBlank()) "Без заметок" else "С заметками")
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ActionPill(
                        text = "Изменить",
                        icon = Icons.Default.Edit,
                        onClick = onEdit
                    )
                    ActionPill(
                        text = "Удалить",
                        icon = Icons.Default.Delete,
                        onClick = onDelete
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoChip(text: String) {
    Surface(
        color = Color(0xFF252B36),
        shape = RoundedCornerShape(18.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            color = FitMuted,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ActionPill(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        color = Color(0xFF2B1A1A),
        shape = RoundedCornerShape(19.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = FitAccentRed, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(7.dp))
            Text(
                text = text,
                color = FitAccentRed,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun CreateWorkoutButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(RedGradient),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(28.dp))
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "Создать тренировку",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun EmptyWorkouts(onCreateWorkout: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF15191F),
        shape = RoundedCornerShape(30.dp),
        border = BorderStroke(1.dp, FitCardBorder)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(72.dp),
                color = Color(0xFF10262D),
                shape = RoundedCornerShape(22.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.FitnessCenter, contentDescription = null, tint = Color.White)
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Нет тренировок",
                color = FitInk,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Создай первый план и начни тренировку из этого раздела",
                color = FitMuted,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 6.dp),
                maxLines = 2
            )
            Spacer(Modifier.height(18.dp))
            TextButton(onClick = onCreateWorkout) {
                Text("+ Создать тренировку", color = FitAccentRed, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun DeleteConfirmDialog(
    name: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Удалить тренировку?") },
        text = { Text("\"$name\" будет удалена без возврата.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Удалить", color = FitAccentRedDark)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        }
    )
}
