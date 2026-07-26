package com.example.fitapp.ui.catalog

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fitapp.data.local.entity.Difficulty
import com.example.fitapp.data.local.entity.Workout
import com.example.fitapp.ui.components.DifficultyChip
import com.example.fitapp.ui.components.ExerciseArtworkThumbnail
import com.example.fitapp.ui.components.FitAccentRed
import com.example.fitapp.ui.components.FitAccentRedDark
import com.example.fitapp.ui.components.FitAccentTeal
import com.example.fitapp.ui.components.FitCardBorder
import com.example.fitapp.ui.components.FitCardShape
import com.example.fitapp.ui.components.FitCardWhite
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
    onQuickAddExercise: (Long, Long?) -> Unit,
    viewModel: CatalogViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var pendingQuickAdd by remember { mutableStateOf<ExerciseCard?>(null) }

    FitHybridScreen(headerHeight = 210.dp) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, top = 42.dp, end = 16.dp, bottom = 118.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                FitScreenHeader(
                    title = "Каталог",
                    subtitle = "Быстрый выбор упражнения для тренировки"
                )
            }

            if (state.recommendedExercises.isNotEmpty()) {
                item {
                    RecommendedPanel(
                        state = state,
                        onExerciseClick = onExerciseClick,
                        onQuickAdd = { pendingQuickAdd = it }
                    )
                }
            }

            item {
                CatalogFilters(
                    state = state,
                    onSearchChanged = viewModel::onSearchChanged,
                    onCollectionSelected = viewModel::onCollectionSelected,
                    onMuscleSelected = viewModel::onMuscleSelected,
                    onEquipmentSelected = viewModel::onEquipmentSelected,
                    onDifficultySelected = viewModel::onDifficultySelected,
                    onClearFilters = viewModel::clearFilters
                )
            }

            if (
                state.selectedCollection == CatalogCollection.ALL &&
                state.searchQuery.isBlank() &&
                state.favoriteExercises.isNotEmpty()
            ) {
                item {
                    HorizontalExerciseStrip(
                        title = "Избранное",
                        exercises = state.favoriteExercises,
                        onExerciseClick = onExerciseClick,
                        onQuickAdd = { pendingQuickAdd = it }
                    )
                }
            }

            if (
                state.selectedCollection == CatalogCollection.ALL &&
                state.searchQuery.isBlank() &&
                state.recentExercises.isNotEmpty()
            ) {
                item {
                    HorizontalExerciseStrip(
                        title = "Недавние",
                        exercises = state.recentExercises,
                        onExerciseClick = onExerciseClick,
                        onQuickAdd = { pendingQuickAdd = it }
                    )
                }
            }

            when {
                state.isLoading -> item { LoadingState() }
                state.errorMessage != null -> item { ErrorState(state.errorMessage) }
                state.exercises.isEmpty() -> item { EmptyState(onClearFilters = viewModel::clearFilters) }
                else -> {
                    item {
                        FitSectionTitle(
                            title = when (state.selectedCollection) {
                                CatalogCollection.ALL -> "Упражнения"
                                CatalogCollection.FAVORITES -> "Избранное"
                                CatalogCollection.RECENT -> "Недавние"
                            },
                            action = "${state.totalCount}"
                        )
                    }
                    items(state.exercises, key = { it.exercise.id }) { card ->
                        ExerciseCardItem(
                            card = card,
                            onClick = { onExerciseClick(card.exercise.id) },
                            onFavoriteToggle = { viewModel.onFavoriteToggled(card.exercise.id) },
                            onQuickAdd = { pendingQuickAdd = card }
                        )
                    }
                }
            }
        }
    }

    pendingQuickAdd?.let { card ->
        QuickAddTargetDialog(
            card = card,
            workouts = state.customWorkouts,
            onDismiss = { pendingQuickAdd = null },
            onCreateNew = {
                viewModel.onQuickAdd(card.exercise.id)
                onQuickAddExercise(card.exercise.id, null)
                pendingQuickAdd = null
            },
            onAddToWorkout = { workoutId ->
                viewModel.onQuickAdd(card.exercise.id)
                onQuickAddExercise(card.exercise.id, workoutId)
                pendingQuickAdd = null
            }
        )
    }
}

@Composable
private fun QuickAddTargetDialog(
    card: ExerciseCard,
    workouts: List<Workout>,
    onDismiss: () -> Unit,
    onCreateNew: () -> Unit,
    onAddToWorkout: (Long) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = FitCardWhite,
        titleContentColor = FitInk,
        textContentColor = FitMuted,
        title = {
            Text(
                text = "Куда добавить?",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = card.exercise.name,
                    color = FitInk,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(12.dp))

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable(onClick = onCreateNew),
                    color = FitAccentRed.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, FitAccentRed.copy(alpha = 0.42f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 13.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = FitAccentRed)
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = "Создать новую тренировку",
                            color = FitInk,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                if (workouts.isEmpty()) {
                    Text(
                        text = "Существующих тренировок пока нет.",
                        color = FitMuted,
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    Text(
                        text = "Добавить в существующую",
                        color = FitMuted,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(8.dp))
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 270.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(workouts, key = { it.id }) { workout ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable { onAddToWorkout(workout.id) },
                                color = Color(0xFF171B21),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, FitCardBorder)
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp)) {
                                    Text(
                                        text = workout.name,
                                        color = FitInk,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    if (!workout.notes.isNullOrBlank()) {
                                        Spacer(Modifier.height(2.dp))
                                        Text(
                                            text = workout.notes,
                                            color = FitMuted,
                                            style = MaterialTheme.typography.bodySmall,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена", color = FitAccentRed)
            }
        }
    )
}

@Composable
private fun RecommendedPanel(
    state: CatalogUiState,
    onExerciseClick: (Long) -> Unit,
    onQuickAdd: (ExerciseCard) -> Unit
) {
    val primary = state.recommendedExercises.first()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = FitCardShape,
        color = Color(0xFF111820),
        border = BorderStroke(1.dp, Color(0xFF263241)),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = state.recommendationTitle,
                        color = FitInk,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = state.recommendationSubtitle,
                        color = FitMuted,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Surface(
                    color = FitAccentTeal.copy(alpha = 0.16f),
                    contentColor = FitAccentTeal,
                    shape = RoundedCornerShape(99.dp)
                ) {
                    Text(
                        text = "${state.recommendedExercises.size}",
                        modifier = Modifier.padding(horizontal = 11.dp, vertical = 5.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .clickable { onExerciseClick(primary.exercise.id) }
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ExerciseThumb(card = primary, size = 72)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = primary.exercise.name,
                        color = FitInk,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${primary.muscleEmoji} ${primary.muscleGroupName} · ${primary.equipmentName}",
                        color = FitMuted,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                FilledIconButton(
                    onClick = { onQuickAdd(primary) },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = FitAccentRed,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.size(46.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Добавить")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CatalogFilters(
    state: CatalogUiState,
    onSearchChanged: (String) -> Unit,
    onCollectionSelected: (CatalogCollection) -> Unit,
    onMuscleSelected: (String?) -> Unit,
    onEquipmentSelected: (String?) -> Unit,
    onDifficultySelected: (Difficulty?) -> Unit,
    onClearFilters: () -> Unit
) {
    val hasFilters =
        state.searchQuery.isNotBlank() ||
            state.selectedMuscle != null ||
            state.selectedEquipment != null ||
            state.selectedDifficulty != null ||
            state.selectedCollection != CatalogCollection.ALL

    FitSurfaceCard(contentPadding = PaddingValues(14.dp)) {
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = onSearchChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Поиск: грудь, гантели, молотки...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(18.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FitAccentRed,
                unfocusedBorderColor = FitCardBorder,
                focusedTextColor = FitInk,
                unfocusedTextColor = FitInk,
                focusedPlaceholderColor = FitMuted,
                unfocusedPlaceholderColor = FitMuted,
                focusedLeadingIconColor = FitAccentRed,
                unfocusedLeadingIconColor = FitMuted,
                cursorColor = FitAccentRed
            )
        )

        Spacer(Modifier.height(12.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(CatalogCollection.entries.toList(), key = { it.name }) { collection ->
                FilterChip(
                    selected = state.selectedCollection == collection,
                    onClick = { onCollectionSelected(collection) },
                    label = { Text(collection.label) },
                    shape = FitChipShape,
                    colors = catalogChipColors()
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.Tune,
                contentDescription = null,
                tint = FitMuted,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "Фильтры",
                color = FitMuted,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            if (hasFilters) {
                TextButton(onClick = onClearFilters) {
                    Text("Сбросить", color = FitAccentRed)
                }
            }
        }

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.muscleGroups, key = { it.code }) { muscle ->
                val selected = state.selectedMuscle == muscle.code
                FilterChip(
                    selected = selected,
                    onClick = { onMuscleSelected(if (selected) null else muscle.code) },
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
                    onClick = { onEquipmentSelected(if (selected) null else equipment.code) },
                    label = { Text(equipment.name) },
                    shape = FitChipShape,
                    colors = catalogChipColors()
                )
            }
        }

        Spacer(Modifier.height(6.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.difficulties, key = { it.name }) { difficulty ->
                val selected = state.selectedDifficulty == difficulty
                FilterChip(
                    selected = selected,
                    onClick = { onDifficultySelected(if (selected) null else difficulty) },
                    label = { Text(difficulty.displayName) },
                    shape = FitChipShape,
                    colors = catalogChipColors()
                )
            }
        }
    }
}

@Composable
private fun HorizontalExerciseStrip(
    title: String,
    exercises: List<ExerciseCard>,
    onExerciseClick: (Long) -> Unit,
    onQuickAdd: (ExerciseCard) -> Unit
) {
    Column {
        FitSectionTitle(title = title, action = "${exercises.size}")
        Spacer(Modifier.height(10.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(exercises, key = { it.exercise.id }) { card ->
                MiniExerciseCard(
                    card = card,
                    onClick = { onExerciseClick(card.exercise.id) },
                    onQuickAdd = { onQuickAdd(card) }
                )
            }
        }
    }
}

@Composable
private fun MiniExerciseCard(
    card: ExerciseCard,
    onClick: () -> Unit,
    onQuickAdd: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(196.dp)
            .height(152.dp)
            .clip(FitCardShape)
            .clickable(onClick = onClick),
        shape = FitCardShape,
        color = FitCardWhite,
        border = BorderStroke(1.dp, FitCardBorder),
        shadowElevation = 6.dp
    ) {

        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ExerciseThumb(card = card, size = 48)
                Spacer(Modifier.width(9.dp))
                FilledIconButton(
                    onClick = onQuickAdd,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = FitAccentRed,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Добавить", modifier = Modifier.size(19.dp))
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = card.exercise.name,
                color = FitInk,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = "${card.muscleEmoji} ${card.muscleGroupName}",
                color = FitMuted,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ExerciseCardItem(
    card: ExerciseCard,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onQuickAdd: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(FitCardShape)
            .clickable(onClick = onClick),
        shape = FitCardShape,
        color = FitCardWhite,
        border = BorderStroke(1.dp, FitCardBorder),
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ExerciseThumb(card = card, size = 78)
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
                    text = "${card.muscleEmoji} ${card.muscleGroupName} · ${card.equipmentName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = FitMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DifficultyChip(difficulty = Difficulty.fromName(card.exercise.difficulty))
                    if (card.lastUsedAt != null) {
                        MetaChip("Недавнее", FitAccentTeal)
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(onClick = onFavoriteToggle, modifier = Modifier.size(40.dp)) {
                    Icon(
                        imageVector = if (card.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Избранное",
                        tint = if (card.isFavorite) FitAccentRed else FitMuted
                    )
                }
                FilledIconButton(
                    onClick = onQuickAdd,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = FitAccentRed,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Добавить")
                }
            }
        }
    }
}

@Composable
private fun ExerciseThumb(card: ExerciseCard, size: Int) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
    ) {
        ExerciseArtworkThumbnail(
            exerciseCode = card.exercise.code,
            primaryMuscleCode = card.exercise.primaryMuscleCode,
            secondaryMuscleCode = card.exercise.secondaryMuscleCode,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun MetaChip(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.14f),
        contentColor = color,
        shape = RoundedCornerShape(99.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.35f))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium
        )
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
private fun EmptyState(onClearFilters: () -> Unit) {
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
            text = "Попробуйте другой запрос, мышцу, оборудование или уровень сложности",
            color = FitMuted,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onClearFilters, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Сбросить фильтры", color = FitAccentRed)
        }
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
