package com.example.fitapp.data.seed

/**
 * Описание готовой программы тренировок (preset).
 *
 * [code] — уникальный стабильный код программы.
 * [exerciseCodes] — коды упражнений в порядке выполнения с параметрами подходов.
 */
data class PresetExercise(
    val code: String,
    val sets: Int,
    val reps: String,
    val restSeconds: Int
)

data class WorkoutPreset(
    val code: String,
    val name: String,
    val description: String,
    val exercises: List<PresetExercise>
)

/**
 * Предустановленные программы тренировок.
 * Ссылаются на упражнения по [code], определённым в [DatabaseSeeder.exercises].
 */
object WorkoutPresets {

    val presets = listOf(
        WorkoutPreset(
            code = "fullbody_beginner",
            name = "Full Body для начинающих",
            description = "Тренировка на всё тело, 2-3 раза в неделю. Идеальна для старта: " +
                "крупные базовые движения, умеренный объём.",
            exercises = listOf(
                PresetExercise("squat", 3, "10", 90),
                PresetExercise("bench_press", 3, "10", 90),
                PresetExercise("barbell_row", 3, "10", 90),
                PresetExercise("overhead_press", 2, "10", 60),
                PresetExercise("plank", 3, "30", 45)
            )
        ),
        WorkoutPreset(
            code = "push_pull_legs",
            name = "Push / Pull / Legs",
            description = "Классический сплит на 3 дня: жимовые (толкающие), тяговые " +
                "и ножные мышцы. Для среднего и продвинутого уровня.",
            exercises = listOf(
                // PUSH
                PresetExercise("bench_press", 4, "8", 120),
                PresetExercise("overhead_press", 3, "10", 90),
                PresetExercise("incline_dumbbell_press", 3, "10", 90),
                PresetExercise("triceps_pushdown", 3, "12", 60),
                // PULL
                PresetExercise("deadlift", 3, "5", 180),
                PresetExercise("pullup", 4, "8", 90),
                PresetExercise("barbell_row", 4, "8", 90),
                PresetExercise("barbell_curl", 3, "12", 60),
                // LEGS
                PresetExercise("squat", 4, "8", 120),
                PresetExercise("leg_press", 3, "12", 90),
                PresetExercise("leg_curl", 3, "12", 60),
                PresetExercise("hip_thrust", 3, "12", 90)
            )
        ),
        WorkoutPreset(
            code = "upper_lower_split",
            name = "Upper / Lower сплит",
            description = "Сплит на 4 дня: верх и низ тела чередуются. " +
                "Хороший баланс объёма и восстановления.",
            exercises = listOf(
                // UPPER
                PresetExercise("bench_press", 4, "8", 120),
                PresetExercise("barbell_row", 4, "8", 120),
                PresetExercise("overhead_press", 3, "10", 90),
                PresetExercise("lat_pulldown", 3, "10", 90),
                PresetExercise("barbell_curl", 3, "12", 60),
                PresetExercise("triceps_pushdown", 3, "12", 60),
                // LOWER
                PresetExercise("squat", 4, "8", 120),
                PresetExercise("romanian_deadlift", 3, "10", 90),
                PresetExercise("leg_press", 3, "12", 90),
                PresetExercise("leg_curl", 3, "12", 60),
                PresetExercise("calf_raise", 4, "15", 45)
            )
        ),
        WorkoutPreset(
            code = "home_bodyweight",
            name = "Домашняя (свой вес)",
            description = "Тренировка без оборудования, в домашних условиях. " +
                "Для поддержания формы, когда нет зала.",
            exercises = listOf(
                PresetExercise("pushup", 4, "12", 60),
                PresetExercise("pullup", 4, "8", 90),
                PresetExercise("lunge", 3, "12", 60),
                PresetExercise("glute_bridge", 3, "15", 45),
                PresetExercise("plank", 3, "45", 45),
                PresetExercise("crunch", 3, "20", 45)
            )
        )
    )
}
