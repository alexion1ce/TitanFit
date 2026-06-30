package com.example.fitapp.ui.journal

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fitapp.data.local.entity.SetLog
import com.example.fitapp.data.repository.LoggedExerciseRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogDetailScreen(
    onBack: () -> Unit,
    viewModel: LogDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.workoutName.ifBlank { "Тренировка" }, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        when {
            state.isLoading -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            state.errorMessage != null -> Box(
                Modifier.fillMaxSize().padding(padding).padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(state.errorMessage!!, color = MaterialTheme.colorScheme.error)
            }

            else -> LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Шапка со статистикой
                item { StatsHeader(state) }

                // Упражнения
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
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(4.dp))
        Text(
            state.dateText,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatBadge("⏱", state.durationText)
            StatBadge("✅", "${state.doneSets}/${state.totalSets} подходов")
            StatBadge("🏋️", "${formatVolume(state.totalVolume)} кг·повт")
        }
    }
}

@Composable
private fun StatBadge(emoji: String, text: String) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            "$emoji $text",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun LoggedExerciseCard(row: LoggedExerciseRow) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(row.muscleEmoji, style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        row.exerciseName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        "Макс: ${formatWeight(row.topWeight)} кг · Объём: ${formatVolume(row.totalVolume)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            // Таблица подходов
            Row(modifier = Modifier.fillMaxWidth()) {
                Text("№", modifier = Modifier.width(32.dp), style = MaterialTheme.typography.labelMedium)
                Text("Вес (кг)", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium)
                Text("Повт.", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium)
                Text("Статус", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium)
            }
            Spacer(Modifier.height(4.dp))
            row.sets.forEach { set -> SetLine(set) }
        }
    }
}

@Composable
private fun SetLine(set: SetLog) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "${set.setNumber}",
            modifier = Modifier.width(32.dp),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            formatWeight(set.weight),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            "${set.reps}",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            if (set.done) "✅ Выполнен" else "⏸ Пропущен",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.labelSmall,
            color = if (set.done) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun formatWeight(value: Double): String =
    if (value == 0.0) "—" else if (value == value.toLong().toDouble()) value.toLong().toString()
    else value.toString()

private fun formatVolume(value: Double): String =
    if (value == value.toLong().toDouble()) value.toLong().toString() else value.toString()
