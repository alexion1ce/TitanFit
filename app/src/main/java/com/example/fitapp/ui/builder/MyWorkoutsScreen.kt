package com.example.fitapp.ui.builder

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fitapp.data.local.entity.Workout
import com.example.fitapp.ui.components.ExerciseArtworkThumbnail
import com.example.fitapp.ui.components.FitAccentRed
import com.example.fitapp.ui.components.FitAccentRedDark
import com.example.fitapp.ui.components.FitAccentTeal
import com.example.fitapp.ui.components.FitCardBorder
import com.example.fitapp.ui.components.FitCardWhite
import com.example.fitapp.ui.components.FitHeaderSoft
import com.example.fitapp.ui.components.FitInk
import com.example.fitapp.ui.components.FitMuted
import com.example.fitapp.ui.components.FitScreenBackground

private val RedGradient = Brush.horizontalGradient(
    listOf(FitAccentRed, FitAccentRedDark)
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
            contentPadding = PaddingValues(start = 16.dp, top = 42.dp, end = 16.dp, bottom = 130.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Header(workoutCount = state.workoutCards.size)
            }

            item {
                CreateWorkoutHeroCard(onCreateWorkout = onCreateWorkout)
            }

            if (state.workoutCards.isEmpty()) {
                item {
                    EmptyWorkouts()
                }
            } else {
                item {
                    Text(
                        text = "Ваши программы",
                        color = FitInk,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                    )
                }

                itemsIndexed(state.workoutCards, key = { _, card -> card.workout.id }) { index, card ->
                    WorkoutCard(
                        card = card,
                        onStart = { onStartWorkout(card.workout.id) },
                        onEdit = { onEditWorkout(card.workout.id) },
                        onDelete = { deleteTarget = card.workout }
                    )
                }
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
private fun Header(workoutCount: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Column {
            Text(
                text = "Мои тренировки",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Ваши личные программы и шаблоны",
                color = Color.White.copy(alpha = 0.72f),
                fontSize = 14.sp,
                maxLines = 1
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetallicChip(text = "⚡ Уровень: Титан", tint = FitAccentTeal)
            MetallicChip(text = "🏋️ Всех программ: $workoutCount", tint = FitInk)
        }
    }
}

@Composable
private fun CreateWorkoutHeroCard(onCreateWorkout: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCreateWorkout),
        color = Color.Transparent,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(
            1.dp,
            Brush.linearGradient(
                listOf(Color(0xFF3B4352), FitAccentRed.copy(alpha = 0.5f))
            )
        ),
        shadowElevation = 10.dp
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF1F242F), Color(0xFF13171E))
                    )
                )
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(RedGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Создать свою программу",
                        color = FitInk,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "Собственный набор упражнений и подходов",
                        color = FitMuted,
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkoutCard(
    card: MyWorkoutCardUi,
    onStart: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val workout = card.workout

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(
            1.dp,
            Brush.linearGradient(
                listOf(Color(0xFF353C4A), FitAccentRed.copy(alpha = 0.35f))
            )
        ),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF1B202A), Color(0xFF13171E))
                    )
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(64.dp),
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
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
                                tint = FitScreenBackground,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = workout.name,
                        color = FitInk,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = workout.notes?.takeIf { it.isNotBlank() } ?: "${card.muscleEmoji} ${card.muscleSummary}",
                        color = FitMuted,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(RedGradient)
                        .clickable(onClick = onStart),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Начать тренировку",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MetallicChip(text = "🟢 ${card.exerciseCount} упр.", tint = FitAccentTeal)
                MetallicChip(text = "💪 ${card.muscleSummary}", tint = FitInk)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Редактировать",
                        tint = FitMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Удалить",
                        tint = FitAccentRed.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MetallicChip(text: String, tint: Color) {
    Surface(
        color = Color.Transparent,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color(0xFF333B4A))
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF1F2530), Color(0xFF141820))
                    )
                )
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = text,
                color = tint,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun EmptyWorkouts() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, FitCardBorder),
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF1B202A), Color(0xFF13171E))
                    )
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(64.dp),
                color = FitAccentRed.copy(alpha = 0.15f),
                shape = CircleShape,
                border = BorderStroke(1.dp, FitAccentRed.copy(alpha = 0.3f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.FitnessCenter, contentDescription = null, tint = FitAccentRed, modifier = Modifier.size(32.dp))
                }
            }
            Spacer(Modifier.height(14.dp))
            Text(
                text = "У вас пока нет своих программ",
                color = FitInk,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Нажмите «Создать свою программу» выше, чтобы составить индивидуальный план тренировок",
                color = FitMuted,
                fontSize = 13.sp,
                maxLines = 2
            )
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
        containerColor = FitCardWhite,
        title = { Text("Удалить тренировку?", color = FitInk, fontWeight = FontWeight.Bold) },
        text = { Text("\"$name\" будет удалена безвозвратно.", color = FitMuted) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Удалить", color = FitAccentRed, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена", color = FitMuted) }
        }
    )
}
