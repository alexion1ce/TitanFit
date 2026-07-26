package com.example.fitapp.ui.builder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitapp.data.local.entity.Exercise
import com.example.fitapp.data.repository.ExerciseRepository
import com.example.fitapp.data.repository.MuscleGroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MuscleGroupOption(val code: String, val name: String, val emoji: String)

data class ExercisePickerUiState(
    val isLoading: Boolean = true,
    val exercises: List<Exercise> = emptyList(),
    val muscleGroups: List<MuscleGroupOption> = emptyList(),
    val selectedMuscle: String? = null,
    val searchQuery: String = "",
    val checkedIds: Set<Long> = emptySet()  // выбранные ID упражнений
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ExercisePickerViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    private val muscleGroupRepository: MuscleGroupRepository
) : ViewModel() {

    private val _selectedMuscle = MutableStateFlow<String?>(null)
    private val _searchQuery = MutableStateFlow("")
    private val _checkedIds = MutableStateFlow<Set<Long>>(emptySet())
    private val _muscleOptions = MutableStateFlow<List<MuscleGroupOption>>(emptyList())

    private val filteredExercises = _selectedMuscle
        .flatMapLatest { muscle ->
            exerciseRepository.observeFiltered(muscle, null, null)
        }

    val uiState: StateFlow<ExercisePickerUiState> =
        combine(
            filteredExercises,
            _muscleOptions,
            _selectedMuscle,
            _searchQuery,
            _checkedIds
        ) { exercises, groups, muscle, search, checked ->
            ExercisePickerUiState(
                isLoading = false,
                exercises = exercises.filter { ex ->
                    search.isBlank() || ex.name.contains(search, ignoreCase = true)
                },
                muscleGroups = groups,
                selectedMuscle = muscle,
                searchQuery = search,
                checkedIds = checked
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ExercisePickerUiState()
        )

    init {
        viewModelScope.launch {
            val muscles = muscleGroupRepository.getAll()
            _muscleOptions.value = muscles.map {
                MuscleGroupOption(it.code, it.name, it.emoji)
            }
        }
    }

    fun onMuscleSelected(code: String?) {
        _selectedMuscle.value = code
    }

    fun onSearchChanged(query: String) {
        _searchQuery.value = query
    }

    fun toggleExercise(id: Long) {
        val current = _checkedIds.value.toMutableSet()
        if (current.contains(id)) current.remove(id) else current.add(id)
        _checkedIds.value = current
    }

    fun isChecked(id: Long): Boolean = _checkedIds.value.contains(id)

    /** Возвращает список ID отмеченных упражнений и очищает состояние. */
    fun getCheckedAndClear(): Set<Long> {
        val result = _checkedIds.value
        _checkedIds.value = emptySet()
        return result
    }
}
