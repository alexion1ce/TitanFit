package com.example.fitapp.ui.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitapp.data.local.entity.Exercise
import com.example.fitapp.data.repository.DatabaseInitializer
import com.example.fitapp.data.repository.EquipmentRepository
import com.example.fitapp.data.repository.ExerciseRepository
import com.example.fitapp.data.repository.MuscleGroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Кэш человекочитаемых названий для построения карточек и чипов фильтров. */
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
    private val databaseInitializer: DatabaseInitializer
) : ViewModel() {

    private val _selectedMuscle = MutableStateFlow<String?>(null)
    private val _selectedEquipment = MutableStateFlow<String?>(null)
    private val _searchQuery = MutableStateFlow("")
    private val _errorMessage = MutableStateFlow<String?>(null)

    /** Переподписывается на запрос при смене фильтров мышц/оборудования. */
    private val filteredExercises = combine(
        _selectedMuscle,
        _selectedEquipment
    ) { muscle, equipment -> FilterState(muscle, equipment) }
        .flatMapLatest { state ->
            exerciseRepository.observeFiltered(
                muscleCode = state.muscle,
                equipmentCode = state.equipment,
                difficulty = null
            )
        }

    /** Справочники подгружаются один раз и кладутся в StateFlow. */
    private val references = MutableStateFlow(
        ReferenceData(emptyMap(), emptyMap(), emptyMap(), emptyList(), emptyList())
    )

    val uiState: StateFlow<CatalogUiState> = combine(
        filteredExercises,
        references,
        combine(_selectedMuscle, _selectedEquipment, _searchQuery) { m, e, s -> FilterUi(m, e, s) }
    ) { exercises, refs, filter ->
        CatalogUiState(
            isLoading = false,
            exercises = exercises
                .filter { filter.search.isBlank() || it.name.contains(filter.search.trim(), ignoreCase = true) }
                .map { it.toCard(refs) },
            muscleGroups = refs.muscleChips,
            equipment = refs.equipmentChips,
            selectedMuscle = filter.muscle,
            selectedEquipment = filter.equipment,
            searchQuery = filter.search,
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

    fun onMuscleSelected(code: String?) { _selectedMuscle.value = code }
    fun onEquipmentSelected(code: String?) { _selectedEquipment.value = code }
    fun onSearchChanged(query: String) { _searchQuery.value = query }

    private fun Exercise.toCard(refs: ReferenceData) = ExerciseCard(
        exercise = this,
        muscleGroupName = refs.muscleName[primaryMuscleCode] ?: "—",
        muscleEmoji = refs.muscleEmoji[primaryMuscleCode] ?: "🏋️",
        equipmentName = refs.equipmentName[equipmentCode] ?: "—"
    )

    private data class FilterState(val muscle: String?, val equipment: String?)
    private data class FilterUi(val muscle: String?, val equipment: String?, val search: String)
}
