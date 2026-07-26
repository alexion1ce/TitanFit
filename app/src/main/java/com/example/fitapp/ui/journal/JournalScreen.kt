package com.example.fitapp.ui.journal

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
    viewModel: JournalViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var deleteTarget by remember { mutableStateOf<JournalEntry?>(null) }

    Scaffold(
        containerColor = FitScreenBackground,
        topBar = {
            TopAppBar(
                title = { Text("Журнал тренировок", fontWeight = FontWeight.Bold, color = FitInk) },
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

            state.entries.isEmpty() -> {
                EmptyJournal(Modifier.padding(padding))
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
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

        deleteTarget?.let { entry ->
            AlertDialog(
                onDismissRequest = { deleteTarget = null },
                containerColor = FitCardWhite,
                title = { Text("Удалить запись?", color = FitInk) },
                text = { Text("Запись от ${entry.dateText} будет удалена безвозвратно.", color = FitMuted) },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteEntry(entry.log.id)
                        deleteTarget = null
                    }) { Text("Удалить", color = FitAccentRed) }
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
        color = FitCardWhite,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, FitCardBorder),
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = FitAccentRed.copy(alpha = 0.16f),
                shape = CircleShape,
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        Icons.Outlined.History,
                        contentDescription = null,
                        tint = FitAccentRed,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    entry.log.workoutName,
                    color = FitInk,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        entry.dateText,
                        fontSize = 12.sp,
                        color = FitMuted
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "⏱ ${entry.durationText}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = FitAccentTeal
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Удалить",
                    tint = FitAccentRed.copy(alpha = 0.8f),
                    modifier = Modifier.size(20.dp)
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Детали",

                tint = FitMuted,
                modifier = Modifier.size(22.dp)
            )
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
