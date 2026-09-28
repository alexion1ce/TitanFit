package com.example.fitapp.ui.journal

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.example.fitapp.ui.components.FitAccentRed
import com.example.fitapp.ui.components.FitAccentTeal
import com.example.fitapp.ui.components.FitCardBorder
import com.example.fitapp.ui.components.FitCardWhite
import com.example.fitapp.ui.components.FitInk
import com.example.fitapp.ui.components.FitMuted
import com.example.fitapp.ui.components.FitScreenBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(
    onEntryClick: (Long) -> Unit,
    onResumeWorkout: (workoutId: Long) -> Unit,
    onBack: (() -> Unit)? = null,
    viewModel: JournalViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var deleteTarget by remember { mutableStateOf<JournalEntry?>(null) }
    var finishTarget by remember { mutableStateOf<JournalEntry?>(null) }

    Scaffold(
        containerColor = FitScreenBackground,
        topBar = {
            TopAppBar(
                title = { Text("Журнал тренировок", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = FitInk) },
                navigationIcon = {
                    if (onBack != null) {
                        Box(
                            modifier = Modifier
                                .padding(start = 12.dp, end = 4.dp)
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.12f))
                                .border(1.dp, Color.White.copy(alpha = 0.20f), CircleShape)
                                .clickable(onClick = onBack),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Назад",
                                tint = FitInk,
                                modifier = Modifier.size(22.dp)
                            )
                        }
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = FitAccentRed)
            }

            state.entries.isEmpty() && state.unfinished.isEmpty() -> {
                Column(Modifier.padding(padding)) {
                    state.errorMessage?.let { JournalError(it, viewModel::clearError) }
                    EmptyJournal()
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    state.errorMessage?.let { message ->
                        item(key = "error") { JournalError(message, viewModel::clearError) }
                    }
                    if (state.unfinished.isNotEmpty()) {
                        item(key = "unfinished_header") { SectionTitle("Незавершённые тренировки") }
                        items(state.unfinished, key = { "unfinished_${it.log.id}" }) { entry ->
                            UnfinishedCard(
                                entry = entry,
                                onResume = { onResumeWorkout(entry.log.workoutId) },
                                onFinish = { finishTarget = entry },
                                onDelete = { deleteTarget = entry }
                            )
                        }
                        if (state.entries.isNotEmpty()) {
                            item(key = "finished_header") { SectionTitle("Завершённые") }
                        }
                    }
                    items(state.entries, key = { it.log.id }) { entry ->
                        JournalCard(
                            entry = entry,
                            onClick = { onEntryClick(entry.log.id) },
                            onDelete = { deleteTarget = entry }
                        )
                    }
                }
            }
        }

        finishTarget?.let { entry ->
            AlertDialog(
                onDismissRequest = { finishTarget = null },
                containerColor = FitCardWhite,
                title = { Text("Завершить тренировку?", color = FitInk, fontWeight = FontWeight.Bold) },
                text = { Text("Тренировка от ${entry.dateText} будет сохранена в журнал с уже отмеченными подходами.", color = FitMuted) },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.finishUnfinished(entry.log.id)
                        finishTarget = null
                    }) { Text("Завершить", color = FitAccentTeal, fontWeight = FontWeight.Bold) }
                },
                dismissButton = {
                    TextButton(onClick = { finishTarget = null }) { Text("Отмена", color = FitMuted) }
                }
            )
        }

        deleteTarget?.let { entry ->
            AlertDialog(
                onDismissRequest = { deleteTarget = null },
                containerColor = FitCardWhite,
                title = { Text("Удалить запись?", color = FitInk, fontWeight = FontWeight.Bold) },
                text = {
                    val what = if (entry.log.finishedAt == null) "Незавершённая тренировка" else "Запись"
                    Text("$what от ${entry.dateText} и все её подходы будут удалены безвозвратно.", color = FitMuted)
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteEntry(entry.log.id)
                        deleteTarget = null
                    }) { Text("Удалить", color = FitAccentRed, fontWeight = FontWeight.Bold) }
                },
                dismissButton = {
                    TextButton(onClick = { deleteTarget = null }) { Text("Отмена", color = FitMuted) }
                }
            )
        }
    }
}

@Composable
private fun JournalCard(
    entry: JournalEntry,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = Color.Transparent,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(
            1.dp,
            Brush.linearGradient(
                colors = listOf(Color(0xFF333B4A), FitAccentRed.copy(alpha = 0.35f))
            )
        ),
        shadowElevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF1B202A), Color(0xFF13171E))
                    )
                )
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Иконка журнала с неоновым градиентом
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(FitAccentRed.copy(alpha = 0.35f), Color(0xFF241416))
                            )
                        )
                        .border(1.dp, FitAccentRed.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.History,
                        contentDescription = null,
                        tint = FitAccentRed,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = entry.log.workoutName,
                        color = FitInk,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = entry.dateText,
                            fontSize = 12.sp,
                            color = FitMuted
                        )
                        Spacer(Modifier.width(8.dp))
                        Surface(
                            color = FitAccentTeal.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(0.5.dp, FitAccentTeal.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "⏱ ${entry.durationText}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = FitAccentTeal,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Удалить",
                        tint = FitAccentRed.copy(alpha = 0.75f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(Modifier.width(4.dp))

                // Кнопка со стрелкой детального просмотра
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(FitAccentRed.copy(alpha = 0.18f))
                        .border(1.dp, FitAccentRed.copy(alpha = 0.4f), CircleShape)
                        .clickable(onClick = onClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Перейти к деталям",
                        tint = FitAccentRed,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, color = FitInk, fontSize = 15.sp, fontWeight = FontWeight.Bold)
}

@Composable
private fun JournalError(message: String, onDismiss: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(message, color = FitAccentRed, fontSize = 13.sp, modifier = Modifier.weight(1f))
        TextButton(onClick = onDismiss) { Text("Скрыть", color = FitMuted) }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun UnfinishedCard(
    entry: JournalEntry,
    onResume: () -> Unit,
    onFinish: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF1B202A),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, FitAccentTeal.copy(alpha = 0.45f))
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(
                text = entry.log.workoutName,
                color = FitInk,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(4.dp))
            Text("Начата ${entry.dateText}", fontSize = 12.sp, color = FitMuted)
            Spacer(Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton(onClick = onResume) { Text("Продолжить", color = FitAccentTeal, fontWeight = FontWeight.Bold) }
                TextButton(onClick = onFinish) { Text("Завершить", color = FitInk) }
                TextButton(onClick = onDelete) { Text("Удалить", color = FitAccentRed) }
            }
        }
    }
}

@Composable
private fun EmptyJournal(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("📖", style = MaterialTheme.typography.displayMedium)
            Spacer(Modifier.height(12.dp))
            Text("Журнал пуст", color = FitInk, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(
                "Завершите тренировку, чтобы она появилась здесь",
                fontSize = 14.sp,
                color = FitMuted,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}
