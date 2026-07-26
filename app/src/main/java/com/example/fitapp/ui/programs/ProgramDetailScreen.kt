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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fitapp.data.repository.WorkoutExerciseItem
import com.example.fitapp.ui.components.ExerciseArtworkThumbnail

private val DetailBackground = Color(0xFF020304)
private val DetailCard = Color(0xFF101419)
private val DetailCardEdge = Color(0xFF2B3038)
private val DetailText = Color(0xFFF5F6FA)
private val DetailTextSecondary = Color(0xFFC1C5CF)
private val DetailTextMuted = Color(0xFF8E949F)
private val DetailRed = Color(0xFFFF4738)
private val DetailRedDark = Color(0xFFE82319)
private val DetailTeal = Color(0xFF35D8B1)
private val DetailBlue = Color(0xFF58A6FF)

@Composable
fun ProgramDetailScreen(
    onBack: () -> Unit,
    onStartWorkout: (Long) -> Unit,
    onExerciseClick: (Long) -> Unit,
    viewModel: ProgramDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DetailBackground)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF171B21), DetailBackground),
                        center = Offset(380f, 40f),
                        radius = 560f
                    )
                )
        )

        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = DetailRed)
            }

            state.errorMessage != null -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.errorMessage ?: "Не удалось загрузить программу",
                    color = DetailText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            else -> ProgramDetailContent(
                state = state,
                onBack = onBack,
                onStartWorkout = { onStartWorkout(state.workoutId) },
                onExerciseClick = onExerciseClick
            )
        }
    }
}

@Composable
private fun ProgramDetailContent(
    state: ProgramDetailUiState,
    onBack: () -> Unit,
    onStartWorkout: () -> Unit,
    onExerciseClick: (Long) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, top = 32.dp, end = 16.dp, bottom = 118.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                ProgramHeader(
                    state = state,
                    onBack = onBack
                )
            }

            item {
                SectionTitle("Упражнения")
            }

            itemsIndexed(state.exercises, key = { _, item -> item.workoutExerciseId }) { index, item ->
                ExerciseRow(
                    index = index + 1,
                    item = item,
                    onClick = { onExerciseClick(item.exerciseId) }
                )
            }
        }

        if (state.exercises.isNotEmpty()) {
            StartWorkoutBar(
                onStartWorkout = onStartWorkout,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun ProgramHeader(
    state: ProgramDetailUiState,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        Surface(
            modifier = Modifier.size(46.dp),
            color = Color.White.copy(alpha = 0.08f),
            shape = CircleShape,
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Назад",
                    tint = DetailText
                )
            }
        }

        Spacer(Modifier.height(26.dp))

        Text(
            text = state.name.ifBlank { "Программа" },
            color = DetailText,
            fontSize = 34.sp,
            lineHeight = 38.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        if (state.description.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = state.description,
                color = DetailTextSecondary,
                fontSize = 16.sp,
                lineHeight = 22.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SummaryChip(
                icon = Icons.AutoMirrored.Outlined.FormatListBulleted,
                text = "${state.exercises.size} упражнений",
                tint = DetailTeal,
                modifier = Modifier.weight(1f)
            )
            SummaryChip(
                icon = Icons.Outlined.FitnessCenter,
                text = "${state.totalSets} подходов",
                tint = DetailRed,
                modifier = Modifier.weight(1f)
            )
            SummaryChip(
                icon = Icons.Outlined.AccessTime,
                text = estimateDuration(state.totalSets),
                tint = DetailBlue,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SummaryChip(
    icon: ImageVector,
    text: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = DetailCard.copy(alpha = 0.94f),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, DetailCardEdge)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text(
                text = text,
                color = DetailText,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(22.dp)
                .background(DetailRed, RoundedCornerShape(4.dp))
        )
        Spacer(Modifier.width(10.dp))
        Text(title, color = DetailText, fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ExerciseRow(index: Int, item: WorkoutExerciseItem, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = DetailCard.copy(alpha = 0.94f),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, DetailCardEdge),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$index.",
                color = DetailRed,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(34.dp)
            )

            ExerciseArtworkThumbnail(
                exerciseCode = item.exerciseCode,
                primaryMuscleCode = item.primaryMuscleCode,
                secondaryMuscleCode = item.secondaryMuscleCode,
                modifier = Modifier.size(width = 74.dp, height = 58.dp)
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.exerciseName,
                    color = DetailText,
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = item.muscleName,
                    color = DetailTextSecondary,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.width(10.dp))

            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.FitnessCenter,
                        contentDescription = null,
                        tint = DetailTextSecondary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "${item.sets} × ${item.reps}",
                        color = DetailText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
                Text(
                    text = "отдых ${item.restSeconds}с",
                    color = DetailTextSecondary,
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun StartWorkoutBar(
    onStartWorkout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = DetailBackground.copy(alpha = 0.96f),
        shadowElevation = 12.dp
    ) {
        Box(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 14.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .background(
                        Brush.horizontalGradient(listOf(Color(0xFFFF6A57), DetailRedDark)),
                        RoundedCornerShape(18.dp)
                    )
                    .clickable(onClick = onStartWorkout),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Начать тренировку",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

private fun estimateDuration(totalSets: Int): String =
    "${(totalSets * 4).coerceIn(35, 75)} мин"
