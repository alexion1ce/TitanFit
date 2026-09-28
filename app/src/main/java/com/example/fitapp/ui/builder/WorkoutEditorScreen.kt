package com.example.fitapp.ui.builder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fitapp.data.repository.WorkoutExerciseItem
import com.example.fitapp.data.repository.isDurationExercise

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutEditorScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    onAddExercise: () -> Unit,
    pickedExerciseIds: LongArray? = null,
    initialExerciseId: Long? = null,
    onPickedExerciseIdsConsumed: () -> Unit = {},
    viewModel: WorkoutEditorViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(pickedExerciseIds) {
        val ids = pickedExerciseIds
        if (ids != null && ids.isNotEmpty()) {
            viewModel.addPickedExerciseIds(ids.toSet())
            onPickedExerciseIdsConsumed()
        }
    }

    LaunchedEffect(initialExerciseId, state.isLoading) {
        initialExerciseId?.takeIf { it > 0L && !state.isLoading }?.let { id ->
            viewModel.addInitialExercise(id)
        }
    }

    // При успешном сохранении — возвращаемся назад
    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            viewModel.clearSaveSuccess()
            onSaved()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isNewWorkout) "Новая тренировка" else "Редактирование") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    if (state.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(24.dp)
                                .padding(2.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        IconButton(onClick = viewModel::save, enabled = !state.isLoading) {
                            Icon(Icons.Default.Check, contentDescription = "Сохранить")
                        }
                    }
                }
            )
        }
    ) { padding ->
        when {
            state.isLoading -> LoadingState()
            else -> EditorContent(state, viewModel, onAddExercise, Modifier.padding(padding))
        }
    }
}

@Composable
private fun EditorContent(
    state: WorkoutEditorUiState,
    viewModel: WorkoutEditorViewModel,
    onAddExercise: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (state.errorMessage != null) {
            item { Text(state.errorMessage, color = MaterialTheme.colorScheme.error) }
        }
        // Название
        item {
            OutlinedTextField(
                value = state.workoutName,
                onValueChange = viewModel::onNameChanged,
                label = { Text("Название тренировки") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        // Заметки
        item {
            OutlinedTextField(
                value = state.workoutNotes,
                onValueChange = viewModel::onNotesChanged,
                label = { Text("Заметки (необязательно)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )
        }

        // Заголовок упражнений
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Упражнения (${state.exercises.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                FilledTonalButton(onClick = onAddExercise) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Добавить")
                }
            }
        }

        // Список упражнений
        if (state.exercises.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Нажмите «Добавить», чтобы включить упражнения",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            itemsIndexed(state.exercises, key = { _, item -> item.editorKey }) { index, item ->
                ExerciseInWorkoutCard(
                    item = item,
                    index = index + 1,
                    draft = state.drafts[item.editorKey] ?: item.parameterDraft(),
                    onDraftChange = { viewModel.onExerciseParametersChanged(index, it) },
                    onMoveUp = { viewModel.onMoveExercise(index, index - 1) },
                    onMoveDown = { viewModel.onMoveExercise(index, index + 1) },
                    canMoveUp = index > 0,
                    canMoveDown = index < state.exercises.lastIndex,
                    onRemove = { viewModel.onRemoveExercise(index) }
                )
            }
        }
    }
}

@Composable
private fun ExerciseInWorkoutCard(
    item: WorkoutExerciseItem,
    index: Int,
    draft: ExerciseParameterDraft,
    onDraftChange: (ExerciseParameterDraft) -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Заголовок: номер, название, кнопка удаления
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "$index.",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        item.exerciseName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        "${item.muscleEmoji} ${item.muscleName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onMoveUp, enabled = canMoveUp) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Выше")
                }
                IconButton(onClick = onMoveDown, enabled = canMoveDown) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Ниже")
                }
                IconButton(onClick = onRemove) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Убрать",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Параметры: подходы, повторения, отдых
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ParamField(
                    label = "Подходы",
                    value = draft.sets,
                    onValueChange = { onDraftChange(draft.copy(sets = it)) },
                    isError = !draft.validSets,
                    modifier = Modifier.weight(1f)
                )
                ParamField(
                    label = if (isDurationExercise(item.exerciseCode)) "Секунды" else "Повторения",
                    value = draft.reps,
                    onValueChange = { onDraftChange(draft.copy(reps = it)) },
                    isError = !draft.validReps,
                    modifier = Modifier.weight(1f)
                )
                ParamField(
                    label = "Отдых (с)",
                    value = draft.rest,
                    onValueChange = { onDraftChange(draft.copy(rest = it)) },
                    isError = !draft.validRest,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ParamField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        isError = isError,
        label = { Text(label, fontSize = 12.sp) },
        modifier = modifier.height(56.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        textStyle = MaterialTheme.typography.bodyMedium
    )
}

@Composable
private fun LoadingState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorWithRetry(message: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ошибка") },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("OK") }
        }
    )
}
