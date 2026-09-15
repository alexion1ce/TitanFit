package com.example.fitapp.ui.session

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.Info
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
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fitapp.data.local.entity.SetLog
import com.example.fitapp.data.repository.calculateWorkoutProgress
import com.example.fitapp.ui.components.ExerciseArtworkThumbnail
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveWorkoutScreen(
    onBack: () -> Unit,
    onFinish: () -> Unit,
    onOpenTechnique: (Long) -> Unit,
    viewModel: ActiveWorkoutViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.setForeground(true)
            if (event == Lifecycle.Event.ON_PAUSE) viewModel.setForeground(false)
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        viewModel.setForeground(lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED))
        onDispose { viewModel.setForeground(false); lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    var showFinishDialog by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }
    var nowMillis by remember { mutableStateOf(System.currentTimeMillis()) }

    BackHandler(enabled = !state.isLoading && !state.isFinished && !state.shouldExit) {
        showExitDialog = true
    }

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
                        Text(
                            state.workoutName.ifBlank { "Тренировка" },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
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
                    IconButton(onClick = { showExitDialog = true }) {
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
                    .imePadding()
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
                        if (state.saveError != null) {
                            Text(state.saveError!!, color = MaterialTheme.colorScheme.error)
                            TextButton(onClick = viewModel::retrySave) { Text("Повторить сохранение") }
                        } else if (state.isSaving) Text("Сохранение…")
                        if (state.restTimer.isActive && state.timerNotice != null) {
                            Text(state.timerNotice!!, style = MaterialTheme.typography.bodySmall)
                            TextButton(onClick = { RestTimerNotifications.openSignalSettings(context) }) { Text("Настройки сигнала") }
                        }
                        WorkoutSummaryCard(
                            groups = state.groups,
                            elapsedMillis = nowMillis - state.startedAt
                        )
                    }

                    items(state.groups, key = { "${it.exerciseOrder}:${it.exerciseId}" }) { group ->
                        ExerciseGroupCard(
                            group = group,
                            onToggleSet = viewModel::toggleSetDone,
                            drafts = state.drafts,
                            enabled = !state.isClosing,
                            onWeightChange = viewModel::onWeightTextChanged,
                            onRepsChange = viewModel::onCountTextChanged,
                            onAddSets = viewModel::addSets,
                            onOpenTechnique = { onOpenTechnique(group.exerciseId) }
                        )
                    }

                    item {
                        Button(
                            onClick = { showFinishDialog = true },
                            enabled = !state.isClosing,
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

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Выйти из тренировки?") },
            text = {
                Text("Можно продолжить позже, сохранить отмеченные подходы или отменить тренировку без сохранения.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExitDialog = false
                        viewModel.saveWorkoutAndExit()
                    }
                ) { Text("Сохранить выполненные и выйти") }
            },
            dismissButton = {
                Row {
                    TextButton(
                        onClick = {
                            showExitDialog = false
                            viewModel.continueLater()
                        }
                    ) { Text("Продолжить позже") }
                    TextButton(
                        onClick = {
                            showExitDialog = false
                            viewModel.cancelWorkout()
                        }
                    ) { Text("Отменить без сохранения") }
                }
            }
        )
    }

    if (state.shouldExit) {
        LaunchedEffectFinished { onBack() }
    } else if (state.isFinished) {
        LaunchedEffectFinished { onFinish() }
    }
}

@Composable
private fun WorkoutSummaryCard(
    groups: List<ExerciseSetGroup>,
    elapsedMillis: Long
) {
    val progress = calculateWorkoutProgress(groups.flatMap { it.sets })

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
                        "${progress.doneSets}/${progress.totalSets} подходов",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(formatElapsedTime(elapsedMillis), style = MaterialTheme.typography.titleMedium)
                    Text("${formatWeight(progress.completedVolume)} кг·повт.", style = MaterialTheme.typography.labelMedium)
                }
            }
            Spacer(Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { progress.fraction },
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
    drafts: Map<Long, SetInputDraft>,
    enabled: Boolean,
    onWeightChange: (SetLog, String) -> Unit,
    onRepsChange: (SetLog, String) -> Unit,
    onAddSets: (Long, Int, Int) -> Unit,
    onOpenTechnique: () -> Unit
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
                ExerciseArtworkThumbnail(
                    exerciseCode = group.exerciseCode,
                    primaryMuscleCode = group.primaryMuscleCode,
                    secondaryMuscleCode = group.secondaryMuscleCode,
                    modifier = Modifier.size(52.dp)
                )
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
                IconButton(onClick = onOpenTechnique) {
                    Icon(Icons.Outlined.Info, contentDescription = "Открыть технику")
                }
            }

            if (group.equipmentCode == "barbell" && group.sets.none { it.weight > 0.0 }) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Олимпийский гриф обычно весит 20 кг. Укажите фактический общий вес; где применимо, можно оставить 0 кг.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(8.dp))

            group.sets.forEach { setLog ->
                SetRow(
                    setLog = setLog,
                    draft = drafts[setLog.id] ?: SetInputDraft.from(setLog),
                    enabled = enabled,
                    onToggle = { onToggleSet(setLog) },
                    onWeightChange = { onWeightChange(setLog, it) },
                    onRepsChange = { onRepsChange(setLog, it) }
                )
                Spacer(Modifier.height(8.dp))
            }

            OutlinedButton(
                onClick = { showAddSetsDialog = true },
                enabled = enabled,
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
                onAddSets(group.exerciseId, group.exerciseOrder, count)
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
    draft: SetInputDraft,
    enabled: Boolean,
    onToggle: () -> Unit,
    onWeightChange: (String) -> Unit,
    onRepsChange: (String) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val timed = setLog.durationSeconds != null
    val count = parseSetCount(draft.count)
    val weight = parseSetWeight(draft.weight)
    Surface(
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
        color = if (setLog.done) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.75f)
            else MaterialTheme.colorScheme.surface
    ) {
        Column(Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    if (setLog.done) "Подход ${setLog.setNumber} · " +
                        (if (timed) "${setLog.durationSeconds} с" else "${formatWeight(setLog.weight)} кг × ${setLog.reps}")
                    else "Подход ${setLog.setNumber}",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f)
                )
                FilledTonalButton(
                    enabled = enabled && (setLog.done || draft.isValid(timed)),
                    onClick = { haptic.performHapticFeedback(HapticFeedbackType.LongPress); onToggle() }
                ) { Text(if (setLog.done) "Отменить" else "Выполнить") }
            }
            if (!setLog.done) {
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (!timed) MetricStepper(
                        title = "Вес",
                        minusEnabled = enabled && weight != null && weight > 0,
                        onMinus = { onWeightChange(((weight ?: 0.0) - 2.5).coerceAtLeast(0.0).toString()) },
                        onPlus = { if (enabled) onWeightChange(((weight ?: 0.0) + 2.5).toString()) },
                        field = { SetInputField(draft.weight, onWeightChange, weight == null, enabled, "кг", true) },
                        modifier = Modifier.weight(1f)
                    )
                    MetricStepper(
                        title = if (timed) "Длительность" else "Повт.",
                        minusEnabled = enabled && count != null && count > 1,
                        onMinus = { onRepsChange(((count ?: 1) - 1).coerceAtLeast(1).toString()) },
                        onPlus = { if (enabled && (count ?: 0) < Int.MAX_VALUE) onRepsChange(((count ?: 0) + 1).toString()) },
                        field = { SetInputField(draft.count, onRepsChange, count == null, enabled, if (timed) "с" else "повт.", false) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun SetInputField(
    text: String, onChange: (String) -> Unit, invalid: Boolean,
    enabled: Boolean, unit: String, decimal: Boolean
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    OutlinedTextField(
        value = text, onValueChange = onChange, enabled = enabled,
        modifier = Modifier.fillMaxWidth(), singleLine = true,
        isError = invalid,
        supportingText = if (invalid) ({ Text(if (decimal) "Вес ≥ 0" else "Целое число > 0") }) else null,
        suffix = { Text(unit) },
        keyboardOptions = KeyboardOptions(keyboardType = if (decimal) KeyboardType.Decimal else KeyboardType.Number, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
    )
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
