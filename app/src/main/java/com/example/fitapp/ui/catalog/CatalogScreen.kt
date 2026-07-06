package com.example.fitapp.ui.catalog

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fitapp.data.local.entity.Difficulty
import com.example.fitapp.ui.components.DifficultyChip
import com.example.fitapp.ui.components.ExerciseArtworkThumbnail
import com.example.fitapp.ui.components.FitAccentRed
import com.example.fitapp.ui.components.FitCardShape
import com.example.fitapp.ui.components.FitChipShape
import com.example.fitapp.ui.components.FitHybridScreen
import com.example.fitapp.ui.components.FitInk
import com.example.fitapp.ui.components.FitMuted
import com.example.fitapp.ui.components.FitScreenHeader
import com.example.fitapp.ui.components.FitSectionTitle
import com.example.fitapp.ui.components.FitSurfaceCard

@Composable
fun CatalogScreen(
    onExerciseClick: (Long) -> Unit,
    viewModel: CatalogViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    FitHybridScreen(headerHeight = 178.dp) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, top = 42.dp, end = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                FitScreenHeader(
                    title = "Каталог",
                    subtitle = "Быстрый выбор упражнения для тренировки"
                )
            }
            item {
                CatalogFilters(
                    state = state,
                    onSearchChanged = viewModel::onSearchChanged,
                    onMuscleSelected = viewModel::onMuscleSelected,
                    onEquipmentSelected = viewModel::onEquipmentSelected
                )
            }

            when {
                state.isLoading -> item { LoadingState() }
                state.errorMessage != null -> item { ErrorState(state.errorMessage) }
                state.exercises.isEmpty() -> item { EmptyState() }
                else -> {
                    item {
                        FitSectionTitle(
                            title = "Упражнения",
                            action = "${state.exercises.size}"
                        )
                    }
                    items(state.exercises, key = { it.exercise.id }) { card ->
                        ExerciseCardItem(card = card, onClick = { onExerciseClick(card.exercise.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun CatalogFilters(
    state: CatalogUiState,
    onSearchChanged: (String) -> Unit,
    onMuscleSelected: (String?) -> Unit,
    onEquipmentSelected: (String?) -> Unit
) {
    FitSurfaceCard(contentPadding = PaddingValues(12.dp)) {
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = onSearchChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Поиск упражнения") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            shape = FitCardShape
        )

        Spacer(Modifier.height(10.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.muscleGroups, key = { it.code }) { muscle ->
                val selected = state.selectedMuscle == muscle.code
                FilterChip(
                    selected = selected,
                    onClick = {
                        val newCode = if (selected) null else muscle.code
                        onMuscleSelected(newCode)
                    },
                    label = { Text("${muscle.emoji} ${muscle.name}") },
                    shape = FitChipShape,
                    colors = catalogChipColors()
                )
            }
        }

        Spacer(Modifier.height(6.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.equipment, key = { it.code }) { equipment ->
                val selected = state.selectedEquipment == equipment.code
                FilterChip(
                    selected = selected,
                    onClick = {
                        val newCode = if (selected) null else equipment.code
                        onEquipmentSelected(newCode)
                    },
                    label = { Text(equipment.name) },
                    shape = FitChipShape,
                    colors = catalogChipColors()
                )
            }
        }
    }
}

@Composable
private fun ExerciseCardItem(card: ExerciseCard, onClick: () -> Unit) {
    FitSurfaceCard(
        modifier = Modifier
            .fillMaxWidth()
            .clip(FitCardShape)
            .clickable(onClick = onClick),
        contentPadding = PaddingValues(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(78.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                ExerciseArtworkThumbnail(
                    exerciseCode = card.exercise.code,
                    primaryMuscleCode = card.exercise.primaryMuscleCode,
                    secondaryMuscleCode = card.exercise.secondaryMuscleCode,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = card.exercise.name,
                    color = FitInk,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${card.muscleGroupName} · ${card.equipmentName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = FitMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(6.dp))
                DifficultyChip(difficulty = Difficulty.fromName(card.exercise.difficulty))
            }

            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = null,
                tint = FitMuted
            )
        }
    }
}

@Composable
private fun LoadingState() {
    FitSurfaceCard {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = FitAccentRed)
        }
    }
}

@Composable
private fun ErrorState(message: String?) {
    FitSurfaceCard {
        Text(
            text = "Не удалось загрузить каталог",
            color = FitInk,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = message ?: "Попробуйте открыть экран позже",
            color = FitMuted,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun EmptyState() {
    FitSurfaceCard {
        Text(
            text = "Ничего не найдено",
            color = FitInk,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Попробуйте изменить поиск или фильтры",
            color = FitMuted,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun catalogChipColors() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = FitAccentRed,
    selectedLabelColor = Color.White,
    containerColor = Color(0xFF171B21),
    labelColor = FitInk,
    disabledContainerColor = Color(0xFF171B21),
    disabledLabelColor = FitMuted
)
