package com.example.fitapp.ui.navigation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Replay
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fitapp.ui.components.FitAccentRed
import com.example.fitapp.ui.components.FitCardBorder
import com.example.fitapp.ui.components.FitInk
import com.example.fitapp.ui.components.FitMutedLight
import com.example.fitapp.ui.components.FitNavDark
import com.example.fitapp.ui.today.TodayViewModel

/** Sheet behind the central bottom-bar button: every way to start a workout. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartWorkoutSheet(
    onDismiss: () -> Unit,
    onStartWorkout: (workoutId: Long) -> Unit,
    onOpenPrograms: () -> Unit,
    onOpenMyWorkouts: () -> Unit,
    onCreateWorkout: () -> Unit,
    viewModel: TodayViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snapshot = state.snapshot

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = FitNavDark) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Начать тренировку",
                color = FitInk,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            val unfinished = snapshot?.unfinished
            val next = snapshot?.suggested?.takeIf { it.workoutId != unfinished?.workoutId }
            if (unfinished != null) {
                StartOption(
                    icon = Icons.Outlined.Replay,
                    title = "Продолжить",
                    subtitle = unfinished.workoutName,
                    highlighted = true,
                    onClick = { onStartWorkout(unfinished.workoutId) }
                )
            }
            if (next != null) {
                StartOption(
                    icon = Icons.Outlined.PlayArrow,
                    title = "Следующая по плану",
                    subtitle = next.name,
                    highlighted = unfinished == null,
                    onClick = { onStartWorkout(next.workoutId) }
                )
            }
            StartOption(
                icon = Icons.AutoMirrored.Outlined.Assignment,
                title = "Выбрать программу",
                subtitle = "Готовые программы",
                onClick = onOpenPrograms
            )
            StartOption(
                icon = Icons.Outlined.Person,
                title = "Мои тренировки",
                subtitle = "Ваши шаблоны",
                onClick = onOpenMyWorkouts
            )
            StartOption(
                icon = Icons.Outlined.Add,
                title = "Создать свою тренировку",
                subtitle = "Собрать из каталога упражнений",
                onClick = onCreateWorkout
            )
        }
    }
}

@Composable
private fun StartOption(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    highlighted: Boolean = false
) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        color = if (highlighted) FitAccentRed else Color(0xFF1B202A),
        shape = RoundedCornerShape(16.dp),
        border = if (highlighted) null else BorderStroke(1.dp, FitCardBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (highlighted) Color.White else FitAccentRed,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(text = title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = subtitle,
                    color = if (highlighted) Color.White.copy(alpha = 0.85f) else FitMutedLight,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
