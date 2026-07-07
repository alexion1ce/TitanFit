package com.example.fitapp.ui.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitapp.data.local.entity.Difficulty
import com.example.fitapp.data.local.entity.Exercise
import com.example.fitapp.data.local.entity.ExerciseCatalogMeta
import com.example.fitapp.data.repository.DatabaseInitializer
import com.example.fitapp.data.repository.EquipmentRepository
import com.example.fitapp.data.repository.ExerciseCatalogRepository
import com.example.fitapp.data.repository.ExerciseRepository
import com.example.fitapp.data.repository.MuscleGroupRepository
import com.example.fitapp.data.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Locale
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private data class ReferenceData(
    val muscleName: Map<String, String>,
    val muscleEmoji: Map<String, String>,
    val equipmentName: Map<String, String>,
    val muscleChips: List<MuscleChip>,
    val equipmentChips: List<EquipmentChip>
)

data class MuscleChip(val code: String, val name: String, val emoji: String)
data class EquipmentChip(val code: String, val name: String)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    private val muscleGroupRepository: MuscleGroupRepository,
    private val equipmentRepository: EquipmentRepository,
    private val databaseInitializer: DatabaseInitializer,
    private val catalogRepository: ExerciseCatalogRepository,
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    private val _selectedMuscle = MutableStateFlow<String?>(null)
    private val _selectedEquipment = MutableStateFlow<String?>(null)
    private val _selectedDifficulty = MutableStateFlow<Difficulty?>(null)
    private val _selectedCollection = MutableStateFlow(CatalogCollection.ALL)
    private val _searchQuery = MutableStateFlow("")
    private val _errorMessage = MutableStateFlow<String?>(null)

    private val filteredExercises = combine(
        _selectedMuscle,
        _selectedEquipment,
        _selectedDifficulty
    ) { muscle, equipment, difficulty -> FilterState(muscle, equipment, difficulty) }
        .flatMapLatest { state ->
            exerciseRepository.observeFiltered(
                muscleCode = state.muscle,
                equipmentCode = state.equipment,
                difficulty = state.difficulty?.name
            )
        }

    private val references = MutableStateFlow(
        ReferenceData(emptyMap(), emptyMap(), emptyMap(), emptyList(), emptyList())
    )

    private val filterUi = combine(
        _selectedMuscle,
        _selectedEquipment,
        _selectedDifficulty,
        _selectedCollection,
        _searchQuery
    ) { muscle, equipment, difficulty, collection, search ->
        FilterUi(muscle, equipment, difficulty, collection, search)
    }

    val uiState: StateFlow<CatalogUiState> = combine(
        filteredExercises,
        references,
        catalogRepository.observeAllMeta(),
        workoutRepository.observeCustomWorkouts(),
        filterUi
    ) { exercises, refs, meta, workouts, filter ->
        val metaByExerciseId = meta.associateBy { it.exerciseId }
        val matchingCards = exercises
            .map { it.toCard(refs, metaByExerciseId[it.id]) }
            .filter { it.matchesSearch(filter.search) }

        val favoriteCards = matchingCards
            .filter { it.isFavorite }
            .sortedBy { it.exercise.name }

        val recentCards = matchingCards
            .filter { it.lastUsedAt != null }
            .sortedByDescending { it.lastUsedAt }

        val visibleCards = when (filter.collection) {
            CatalogCollection.ALL -> matchingCards
            CatalogCollection.FAVORITES -> favoriteCards
            CatalogCollection.RECENT -> recentCards
        }

        CatalogUiState(
            isLoading = false,
            exercises = visibleCards,
            recommendedExercises = matchingCards.recommended(),
            favoriteExercises = favoriteCards.take(8),
            recentExercises = recentCards.take(8),
            customWorkouts = workouts,
            muscleGroups = refs.muscleChips,
            equipment = refs.equipmentChips,
            selectedCollection = filter.collection,
            selectedMuscle = filter.muscle,
            selectedEquipment = filter.equipment,
            selectedDifficulty = filter.difficulty,
            searchQuery = filter.search,
            totalCount = matchingCards.size,
            recommendationTitle = recommendationTitle(filter, refs),
            recommendationSubtitle = recommendationSubtitle(filter, refs),
            errorMessage = _errorMessage.value
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CatalogUiState(isLoading = true)
    )

    init {
        viewModelScope.launch {
            try {
                databaseInitializer.initializeIfNeeded()
                val muscles = muscleGroupRepository.getAll()
                val equipment = equipmentRepository.getAll()
                references.value = ReferenceData(
                    muscleName = muscles.associate { it.code to it.name },
                    muscleEmoji = muscles.associate { it.code to it.emoji },
                    equipmentName = equipment.associate { it.code to it.name },
                    muscleChips = muscles.map { MuscleChip(it.code, it.name, it.emoji) },
                    equipmentChips = equipment.map { EquipmentChip(it.code, it.name) }
                )
            } catch (e: Exception) {
                _errorMessage.value = "Не удалось загрузить данные: ${e.message}"
            }
        }
    }

    fun onMuscleSelected(code: String?) {
        _selectedMuscle.value = code
    }

    fun onEquipmentSelected(code: String?) {
        _selectedEquipment.value = code
    }

    fun onDifficultySelected(difficulty: Difficulty?) {
        _selectedDifficulty.value = difficulty
    }

    fun onCollectionSelected(collection: CatalogCollection) {
        _selectedCollection.value = collection
    }

    fun onSearchChanged(query: String) {
        _searchQuery.value = query
    }

    fun onFavoriteToggled(exerciseId: Long) {
        viewModelScope.launch {
            catalogRepository.toggleFavorite(exerciseId)
        }
    }

    fun onQuickAdd(exerciseId: Long) {
        viewModelScope.launch {
            catalogRepository.markUsed(exerciseId)
        }
    }

    fun onExerciseOpened(exerciseId: Long) {
        viewModelScope.launch {
            catalogRepository.markUsed(exerciseId)
        }
    }

    fun clearFilters() {
        _selectedMuscle.value = null
        _selectedEquipment.value = null
        _selectedDifficulty.value = null
        _selectedCollection.value = CatalogCollection.ALL
        _searchQuery.value = ""
    }

    private fun Exercise.toCard(refs: ReferenceData, meta: ExerciseCatalogMeta?) = ExerciseCard(
        exercise = this,
        muscleGroupName = refs.muscleName[primaryMuscleCode] ?: "—",
        muscleEmoji = refs.muscleEmoji[primaryMuscleCode] ?: "🏋️",
        equipmentName = refs.equipmentName[equipmentCode] ?: "—",
        isFavorite = meta?.isFavorite == true,
        lastUsedAt = meta?.lastUsedAt,
        quickAddCount = meta?.quickAddCount ?: 0
    )

    private fun List<ExerciseCard>.recommended(): List<ExerciseCard> =
        sortedWith(
            compareByDescending<ExerciseCard> { it.isFavorite }
                .thenByDescending { it.quickAddCount }
                .thenBy { Difficulty.fromName(it.exercise.difficulty).ordinal }
                .thenBy { it.exercise.name }
        ).take(5)

    private fun ExerciseCard.matchesSearch(rawQuery: String): Boolean {
        val tokens = rawQuery.searchTokens()
        if (tokens.isEmpty()) return true

        val haystack = normalizeSearchText(
            listOf(
                exercise.name,
                exercise.code,
                exercise.description,
                exercise.technique,
                muscleGroupName,
                equipmentName,
                Difficulty.fromName(exercise.difficulty).displayName,
                if (isFavorite) "избранное favorite" else "",
                if (lastUsedAt != null) "недавние recent" else ""
            ).joinToString(" ")
        )

        return tokens.all { token ->
            val aliases = searchAliases[token].orEmpty()
            haystack.contains(token) || aliases.any { haystack.contains(it) }
        }
    }

    private fun String.searchTokens(): List<String> =
        normalizeSearchText(this)
            .split(" ")
            .filter { it.length >= 2 }

    private fun normalizeSearchText(value: String): String =
        value.lowercase(Locale.forLanguageTag("ru"))
            .replace('ё', 'е')
            .replace(Regex("[^a-zа-я0-9]+"), " ")
            .trim()

    private fun recommendationTitle(filter: FilterUi, refs: ReferenceData): String =
        when {
            filter.equipment != null -> "Подходит под ${refs.equipmentName[filter.equipment] ?: "оборудование"}"
            filter.muscle != null -> "Рекомендовано для ${refs.muscleName[filter.muscle] ?: "выбранной мышцы"}"
            filter.difficulty != null -> "Лучшее для уровня ${filter.difficulty.displayName.lowercase(Locale.forLanguageTag("ru"))}"
            else -> "Рекомендовано сегодня"
        }

    private fun recommendationSubtitle(filter: FilterUi, refs: ReferenceData): String =
        when {
            filter.collection == CatalogCollection.FAVORITES -> "Ваши сохраненные упражнения для быстрого выбора"
            filter.collection == CatalogCollection.RECENT -> "То, что вы недавно открывали или добавляли"
            filter.equipment != null || filter.muscle != null || filter.difficulty != null -> "Подборка уже учитывает активные фильтры"
            else -> "Быстрый старт без лишнего поиска"
        }

    private data class FilterState(
        val muscle: String?,
        val equipment: String?,
        val difficulty: Difficulty?
    )

    private data class FilterUi(
        val muscle: String?,
        val equipment: String?,
        val difficulty: Difficulty?,
        val collection: CatalogCollection,
        val search: String
    )

    private companion object {
        val searchAliases = mapOf(
            "бицуха" to listOf("бицепс", "curl"),
            "руки" to listOf("бицепс", "трицепс", "arms"),
            "плечи" to listOf("дельты", "shoulder", "press"),
            "дельты" to listOf("плечи", "shoulder"),
            "грудь" to listOf("грудные", "chest", "press"),
            "грудные" to listOf("грудь", "chest"),
            "спина" to listOf("тяга", "back", "row", "pull"),
            "ноги" to listOf("присед", "квадрицепс", "ягодицы", "leg", "squat"),
            "попа" to listOf("ягодицы", "glute", "hip"),
            "пресс" to listOf("abs", "core", "кор"),
            "кор" to listOf("пресс", "abs", "core"),
            "гантели" to listOf("гантель", "dumbbell"),
            "гантель" to listOf("гантели", "dumbbell"),
            "штанга" to listOf("barbell"),
            "тренажер" to listOf("машина", "machine"),
            "машина" to listOf("тренажер", "machine"),
            "блок" to listOf("кроссовер", "cable"),
            "кроссовер" to listOf("блок", "cable"),
            "жим" to listOf("press"),
            "тяга" to listOf("row", "pull"),
            "присед" to listOf("squat"),
            "молотки" to listOf("hammer", "curl"),
            "новичок" to listOf("beginner"),
            "средний" to listOf("intermediate"),
            "продвинутый" to listOf("advanced")
        )
    }
}
