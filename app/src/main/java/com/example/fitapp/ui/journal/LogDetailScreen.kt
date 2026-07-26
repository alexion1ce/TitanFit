package com.example.fitapp.ui.journal

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fitapp.data.local.entity.SetLog
import com.example.fitapp.data.repository.LoggedExerciseRow
import com.example.fitapp.ui.components.FitAccentRed
import com.example.fitapp.ui.components.FitAccentTeal
import com.example.fitapp.ui.components.FitCardBorder
import com.example.fitapp.ui.components.FitCardWhite
import com.example.fitapp.ui.components.FitInk
import com.example.fitapp.ui.components.FitMuted
import com.example.fitapp.ui.components.FitScreenBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogDetailScreen(
    onBack: () -> Unit,
    viewModel: LogDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = FitScreenBackground,
        topBar = {
            TopAppBar(
                title = { Text(state.workoutName.ifBlank { "Тренировка" }, maxLines = 1, fontWeight = FontWeight.Bold, color = FitInk) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = FitInk)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FitScreenBackground,
                    titleContentColor = FitInk
                )
            )
        }
    ) { padding ->
        when {
            state.isLoading -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator(color = FitAccentRed) }

            state.errorMessage != null -> Box(
                Modifier.fillMaxSize().padding(padding).padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(state.errorMessage!!, color = FitAccentRed)
            }

            else -> LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { StatsHeader(state) }

                items(state.exercises, key = { it.exerciseId }) { row ->
                    LoggedExerciseCard(row)
                }
            }
        }
    }
}

@Composable
private fun StatsHeader(state: LogDetailUiState) {
    Column {
        Text(
            state.workoutName,
            color = FitInk,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(4.dp))
        Text(
            state.dateText,
            fontSize = 14.sp,
            color = FitMuted
        )
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatBadge("⏱", state.durationText)
            StatBadge("✅", "${state.doneSets}/${state.totalSets} подходов")
            StatBadge("🏋️", "${formatVolume(state.totalVolume)} кг")
        }
    }
}

@Composable
private fun StatBadge(emoji: String, text: String) {
    Surface(
        color = FitCardWhite,
        border = BorderStroke(1.dp, FitCardBorder),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(
            "$emoji $text",
            color = FitInk,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun LoggedExerciseCard(row: LoggedExerciseRow) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = FitCardWhite,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, FitCardBorder),
        shadowElevation = 6.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(row.muscleEmoji, fontSize = 24.sp)
                Spacer(Modifier.width(10.dp))
                Text(
                    row.exerciseName,
                    color = FitInk,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }
            if (row.sets.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    row.sets.forEachIndexed { idx, setLog ->
                        SetLogRow(setIndex = idx + 1, setLog = setLog)
                    }
                }
            }
        }
    }
}

@Composable
private fun SetLogRow(setIndex: Int, setLog: SetLog) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(FitScreenBackground, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Подход $setIndex", fontSize = 13.sp, color = FitMuted)
        if (setLog.done) {
            Text(
                "${setLog.weight} кг × ${setLog.reps} повт",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = FitAccentTeal
            )
        } else {
            Text("Пропущен", fontSize = 13.sp, color = FitMuted)
        }
    }
}


private fun formatVolume(volume: Double): String {
    return if (volume >= 1000) {
        String.format("%.1f т", volume / 1000)
    } else {
        "${volume.toInt()}"
    }
}
