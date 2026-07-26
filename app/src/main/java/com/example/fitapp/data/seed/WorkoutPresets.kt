package com.example.fitapp.data.seed

/**
 * Описание готовой программы тренировок (preset).
 *
 * [code] — уникальный стабильный код программы.
 * [groupCode] — код группы/комплекса (например "ppl", "upper_lower", "fullbody", "home").
 * [groupName] — название комплекса.
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
    val groupCode: String,
    val groupName: String,
    val name: String,
    val description: String,
    val exercises: List<PresetExercise>
)

/**
 * Предустановленные программы тренировок, разбитые по отдельным дням.
 * Ссылаются на упражнения по [code], определённым в [DatabaseSeeder.exercises].
 */
object WorkoutPresets {

    val presets = listOf(
        // ================= PUSH / PULL / LEGS =================
        WorkoutPreset(
            code = "ppl_push",
            groupCode = "ppl",
            groupName = "Push / Pull / Legs (Сплит 3 дня)",
            name = "PPL — День 1: Push (Толкающие)",
            description = "Проработка груди, плеч и трицепса. Базовые жимовые движения.",
            exercises = listOf(
                PresetExercise("bench_press", 4, "8", 120),
                PresetExercise("overhead_press", 3, "10", 90),
                PresetExercise("incline_dumbbell_press", 3, "10", 90),
                PresetExercise("triceps_pushdown", 3, "12", 60)
            )
        ),
        WorkoutPreset(
            code = "ppl_pull",
            groupCode = "ppl",
            groupName = "Push / Pull / Legs (Сплит 3 дня)",
            name = "PPL — День 2: Pull (Тяговые)",
            description = "Проработка спины, задних дельт и бицепса. Становая тяга и подтягивания.",
            exercises = listOf(
                PresetExercise("deadlift", 3, "5", 180),
                PresetExercise("pullup", 4, "8", 90),
                PresetExercise("barbell_row", 4, "8", 90),
                PresetExercise("barbell_curl", 3, "12", 60),
                PresetExercise("face_pull", 3, "12", 60)
            )
        ),
        WorkoutPreset(
            code = "ppl_legs",
            groupCode = "ppl",
            groupName = "Push / Pull / Legs (Сплит 3 дня)",
            name = "PPL — День 3: Legs (День ног)",
            description = "Мощная тренировка ног и ягодиц: приседания, жим ногами и мостик.",
            exercises = listOf(
                PresetExercise("squat", 4, "8", 120),
                PresetExercise("leg_press", 3, "12", 90),
                PresetExercise("leg_curl", 3, "12", 60),
                PresetExercise("hip_thrust", 3, "12", 90),
                PresetExercise("calf_raise", 4, "15", 45)
            )
        ),

        // ================= UPPER / LOWER =================
        WorkoutPreset(
            code = "split_upper",
            groupCode = "upper_lower",
            groupName = "Upper / Lower (Сплит 2–4 дня)",
            name = "Upper / Lower — День 1: Верх тела",
            description = "Комплексная тренировка мышц верхней части тела.",
            exercises = listOf(
                PresetExercise("bench_press", 4, "8", 120),
                PresetExercise("barbell_row", 4, "8", 120),
                PresetExercise("overhead_press", 3, "10", 90),
                PresetExercise("lat_pulldown", 3, "10", 90),
                PresetExercise("barbell_curl", 3, "12", 60),
                PresetExercise("triceps_pushdown", 3, "12", 60)
            )
        ),
        WorkoutPreset(
            code = "split_lower",
            groupCode = "upper_lower",
            groupName = "Upper / Lower (Сплит 2–4 дня)",
            name = "Upper / Lower — День 2: Низ тела",
            description = "Интенсивная тренировка квадрицепсов, бицепсов бедра и икр.",
            exercises = listOf(
                PresetExercise("squat", 4, "8", 120),
                PresetExercise("romanian_deadlift", 3, "10", 90),
                PresetExercise("leg_press", 3, "12", 90),
                PresetExercise("leg_curl", 3, "12", 60),
                PresetExercise("calf_raise", 4, "15", 45)
            )
        ),

        // ================= FULL BODY =================
        WorkoutPreset(
            code = "fullbody_a",
            groupCode = "fullbody",
            groupName = "Full Body (Все тело 2–3 дня)",
            name = "Full Body — День A (Присед + Жим)",
            description = "Базовая тренировка на всё тело с акцентом на приседания и жим.",
            exercises = listOf(
                PresetExercise("squat", 3, "10", 90),
                PresetExercise("bench_press", 3, "10", 90),
                PresetExercise("barbell_row", 3, "10", 90),
                PresetExercise("overhead_press", 2, "10", 60),
                PresetExercise("plank", 3, "30", 45)
            )
        ),
        WorkoutPreset(
            code = "fullbody_b",
            groupCode = "fullbody",
            groupName = "Full Body (Все тело 2–3 дня)",
            name = "Full Body — День B (Становая + Тяга)",
            description = "Разнообразная тренировка на всё тело с акцентом на спину и плечи.",
            exercises = listOf(
                PresetExercise("deadlift", 3, "6", 120),
                PresetExercise("incline_dumbbell_press", 3, "10", 90),
                PresetExercise("lat_pulldown", 3, "10", 90),
                PresetExercise("lateral_raise", 3, "12", 60),
                PresetExercise("crunch", 3, "15", 45)
            )
        ),

        // ================= HOME WORKOUTS =================
        WorkoutPreset(
            code = "home_bodyweight_upper",
            groupCode = "home_bodyweight",
            groupName = "Домашняя (со своим весом)",
            name = "Дом (свой вес) — Верх тела",
            description = "Отжимания, подтягивания и удержание корпуса без оборудования.",
            exercises = listOf(
                PresetExercise("pushup", 4, "12", 60),
                PresetExercise("pullup", 4, "8", 90),
                PresetExercise("plank", 3, "45", 45),
                PresetExercise("crunch", 3, "20", 45)
            )
        ),
        WorkoutPreset(
            code = "home_bodyweight_lower",
            groupCode = "home_bodyweight",
            groupName = "Домашняя (со своим весом)",
            name = "Дом (свой вес) — Низ тела и Кор",
            description = "Выпады, ягодичный мостик и боковая планка для работы дома.",
            exercises = listOf(
                PresetExercise("lunge", 4, "12", 60),
                PresetExercise("glute_bridge", 4, "15", 45),
                PresetExercise("side_plank", 3, "30", 45),
                PresetExercise("crunch", 3, "20", 45)
            )
        ),
        WorkoutPreset(
            code = "home_dumbbells_upper",
            groupCode = "home_dumbbells",
            groupName = "Домашняя с гантелями",
            name = "Дом (гантели) — Верх тела",
            description = "Упражнения с гантелями на грудь, плечи и руки.",
            exercises = listOf(
                PresetExercise("dumbbell_bench_press", 4, "10", 90),
                PresetExercise("one_arm_dumbbell_row", 4, "10", 90),
                PresetExercise("lateral_raise", 3, "12", 60),
                PresetExercise("hammer_curl", 3, "12", 60),
                PresetExercise("overhead_triceps_extension", 3, "12", 60)
            )
        ),
        WorkoutPreset(
            code = "home_dumbbells_lower",
            groupCode = "home_dumbbells",
            groupName = "Домашняя с гантелями",
            name = "Дом (гантели) — Низ тела",
            description = "Гоблет-приседания, румынская тяга с гантелями и ягодицы.",
            exercises = listOf(
                PresetExercise("goblet_squat", 4, "12", 90),
                PresetExercise("romanian_deadlift", 3, "10", 90),
                PresetExercise("lunge", 3, "12", 60),
                PresetExercise("glute_bridge", 3, "15", 45)
            )
        )
    )
}
