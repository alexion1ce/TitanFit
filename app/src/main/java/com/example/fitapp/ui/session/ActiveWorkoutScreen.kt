package com.example.fitapp.ui.session

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fitapp.data.local.entity.SetLog
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveWorkoutScreen(
    onBack: () -> Unit,
    onFinish: () -> Unit,
    viewModel: ActiveWorkoutViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showFinishDialog by remember { mutableStateOf(false) }
    var nowMillis by remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(state.startedAt, state.isFinished) {
        while (state.startedAt > 0L && !state.isFinished) {
            nowMillis = System.currentTimeMillis()
            delay(1_000)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(state.workoutName.ifBlank { "Тренировка" }, maxLines = 1)
                        if (state.startedAt > 0L && !state.isLoading) {
                            Text(
                                text = formatElapsedTime(nowMillis - state.startedAt),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
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
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            state.errorMessage != null -> Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(state.errorMessage!!, color = MaterialTheme.colorScheme.error)
            }

            else -> Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = 140.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        WorkoutSummaryCard(
                            groups = state.groups,
                            elapsedMillis = nowMillis - state.startedAt
                        )
                    }

                    items(state.groups, key = { it.exerciseId }) { group ->
                        ExerciseGroupCard(
                            group = group,
                            onToggleSet = viewModel::toggleSetDone,
                            onWeightChange = viewModel::onWeightChanged,
                            onRepsChange = viewModel::onRepsChanged,
                            onAddSets = viewModel::addSets
                        )
                    }

                    item {
                        Button(
                            onClick = { showFinishDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiary,
                                contentColor = MaterialTheme.colorScheme.onTertiary
                            )
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Завершить тренировку")
                        }
                    }
                }

                if (state.restTimer.isActive) {
                    RestTimerCard(
                        timer = state.restTimer,
                        onDismiss = viewModel::stopRestTimer,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                    )
                }
            }
        }
    }

    if (showFinishDialog) {
        AlertDialog(
            onDismissRequest = { showFinishDialog = false },
            title = { Text("Завершить тренировку?") },
            text = { Text("Тренировка будет сохранена в журнал.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showFinishDialog = false
                        viewModel.finishWorkout()
                    }
                ) { Text("Завершить") }
            },
            dismissButton = {
                TextButton(onClick = { showFinishDialog = false }) { Text("Отмена") }
            }
        )
    }

    if (state.isFinished) {
        LaunchedEffectFinished { onFinish() }
    }
}

@Composable
private fun WorkoutSummaryCard(
    groups: List<ExerciseSetGroup>,
    elapsedMillis: Long
) {
    val realTotalSets = groups.sumOf { it.sets.size }
    val totalSets = realTotalSets.coerceAtLeast(1)
    val doneSets = groups.sumOf { group -> group.sets.count { it.done } }
    val volume = groups.sumOf { group ->
        group.sets.filter { it.done }.sumOf { it.weight * it.reps }
    }
    val progress = doneSets.toFloat() / totalSets.toFloat()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        tonalElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Прогресс", style = MaterialTheme.typography.labelMedium)
                    Text(
                        "$doneSets/$realTotalSets подходов",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(formatElapsedTime(elapsedMillis), style = MaterialTheme.typography.titleMedium)
                    Text("${formatWeight(volume)} кг·повт.", style = MaterialTheme.typography.labelMedium)
                }
            }
            Spacer(Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.16f)
            )
        }
    }
}

@Composable
private fun ExerciseGroupCard(
    group: ExerciseSetGroup,
    onToggleSet: (SetLog) -> Unit,
    onWeightChange: (SetLog, Double) -> Unit,
    onRepsChange: (SetLog, Int) -> Unit,
    onAddSets: (Long, Int) -> Unit
) {
    val completedSets = group.sets.count { it.done }
    var showAddSetsDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(group.muscleEmoji, style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        group.exerciseName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        "${group.muscleName} · отдых ${group.restSeconds}с · $completedSets/${group.sets.size}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            group.sets.forEach { setLog ->
                SetRow(
                    setLog = setLog,
                    onToggle = { onToggleSet(setLog) },
                    onWeightChange = { onWeightChange(setLog, it) },
                    onRepsChange = { onRepsChange(setLog, it) }
                )
                Spacer(Modifier.height(8.dp))
            }

            OutlinedButton(
                onClick = { showAddSetsDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Добавить подходы")
            }
        }
    }

    if (showAddSetsDialog) {
        AddSetsDialog(
            exerciseName = group.exerciseName,
            onDismiss = { showAddSetsDialog = false },
            onConfirm = { count ->
                onAddSets(group.exerciseId, count)
                showAddSetsDialog = false
            }
        )
    }
}

@Composable
private fun AddSetsDialog(
    exerciseName: String,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var count by remember { mutableStateOf(1) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Добавить подходы") },
        text = {
            Column {
                Text(
                    text = exerciseName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(14.dp))
                Text(
                    text = "Сколько дополнительных подходов добавить?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { count = (count - 1).coerceAtLeast(1) },
                        enabled = count > 1,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Уменьшить")
                    }
                    Text(
                        text = count.toString(),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedButton(
                        onClick = { count = (count + 1).coerceAtMost(20) },
                        enabled = count < 20,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Увеличить")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(count) }) {
                Text("Добавить: $count")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

@Composable
private fun SetRow(
    setLog: SetLog,
    onToggle: () -> Unit,
    onWeightChange: (Double) -> Unit,
    onRepsChange: (Int) -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = if (setLog.done) {
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.75f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        tonalElevation = if (setLog.done) 1.dp else 0.dp
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Подход ${setLog.setNumber}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        if (setLog.done) "Выполнен" else "Вес и повторы можно менять кнопками",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                FilledTonalButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onToggle()
                    }
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(if (setLog.done) "Снять" else "Готово")
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricStepper(
                    title = "Вес",
                    minusEnabled = setLog.weight > 0.0,
                    onMinus = { onWeightChange((setLog.weight - 2.5).coerceAtLeast(0.0)) },
                    onPlus = { onWeightChange(setLog.weight + 2.5) },
                    field = {
                        WeightField(
                            value = setLog.weight,
                            onValueChange = onWeightChange,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
                MetricStepper(
                    title = "Повт.",
                    minusEnabled = setLog.reps > 0,
                    onMinus = { onRepsChange((setLog.reps - 1).coerceAtLeast(0)) },
                    onPlus = { onRepsChange(setLog.reps + 1) },
                    field = {
                        RepsField(
                            value = setLog.reps,
                            onValueChange = onRepsChange,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MetricStepper(
    title: String,
    minusEnabled: Boolean,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    field: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(4.dp))
        field()
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            OutlinedButton(
                onClick = onMinus,
                enabled = minusEnabled,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp)
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Уменьшить", modifier = Modifier.size(18.dp))
            }
            OutlinedButton(
                onClick = onPlus,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Увеличить", modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun WeightField(
    value: Double,
    onValueChange: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf(if (value == 0.0) "" else formatWeight(value)) }

    LaunchedEffect(value) {
        val formatted = if (value == 0.0) "" else formatWeight(value)
        val currentNumber = text.replace(',', '.').toDoubleOrNull()
        if (currentNumber != value && text != formatted) {
            text = formatted
        }
    }

    OutlinedTextField(
        value = text,
        onValueChange = { str ->
            text = str
            val parsed = str.replace(',', '.').toDoubleOrNull() ?: 0.0
            onValueChange(parsed)
        },
        modifier = modifier.height(52.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        textStyle = MaterialTheme.typography.bodyMedium
    )
}

@Composable
private fun RepsField(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf(value.toString()) }

    LaunchedEffect(value) {
        val formatted = value.toString()
        val currentNumber = text.toIntOrNull()
        if (currentNumber != value && text != formatted) {
            text = formatted
        }
    }

    OutlinedTextField(
        value = text,
        onValueChange = { str ->
            text = str
            str.toIntOrNull()?.let { if (it >= 0) onValueChange(it) }
        },
        modifier = modifier.height(52.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        textStyle = MaterialTheme.typography.bodyMedium
    )
}

@Composable
private fun RestTimerCard(
    timer: RestTimerState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        tonalElevation = 6.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Timer, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Column {
                Text("Отдых", style = MaterialTheme.typography.labelSmall)
                Text(
                    text = "${timer.remainingSeconds}с",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.weight(1f))
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(Icons.Default.Close, contentDescription = "Пропустить", modifier = Modifier.size(20.dp))
            }
        }
    }
}

private fun formatWeight(value: Double): String =
    if (value == value.toLong().toDouble()) value.toLong().toString()
    else "%.1f".format(value).replace(',', '.')

private fun formatElapsedTime(elapsedMillis: Long): String {
    val totalSeconds = (elapsedMillis.coerceAtLeast(0L) / 1_000L).toInt()
    val hours = totalSeconds / 3_600
    val minutes = (totalSeconds % 3_600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        "%d:%02d:%02d".format(hours, minutes, seconds)
    } else {
        "%02d:%02d".format(minutes, seconds)
    }
}

@Composable
private fun LaunchedEffectFinished(action: () -> Unit) {
    androidx.compose.runtime.LaunchedEffect(Unit) { action() }
}
