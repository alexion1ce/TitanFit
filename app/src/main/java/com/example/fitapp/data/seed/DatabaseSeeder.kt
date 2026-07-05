package com.example.fitapp.data.seed

import com.example.fitapp.data.local.entity.Equipment
import com.example.fitapp.data.local.entity.Exercise
import com.example.fitapp.data.local.entity.MuscleGroup

/**
 * Начальные данные приложения: справочники групп мышц, оборудования
 * и список упражнений с описаниями и ссылками на видео YouTube.
 *
 * Содержит только «сырые» данные — заполнение БД выполняет репозиторий.
 */
object DatabaseSeeder {

    // ===================== Группы мышц =====================
    val muscleGroups = listOf(
        MuscleGroup(code = "chest", name = "Грудь", emoji = "🫁"),
        MuscleGroup(code = "back", name = "Спина", emoji = "🔙"),
        MuscleGroup(code = "legs", name = "Ноги", emoji = "🦵"),
        MuscleGroup(code = "shoulders", name = "Плечи", emoji = "💪"),
        MuscleGroup(code = "biceps", name = "Бицепс", emoji = "💪"),
        MuscleGroup(code = "triceps", name = "Трицепс", emoji = "💪"),
        MuscleGroup(code = "abs", name = "Пресс", emoji = "🎯"),
        MuscleGroup(code = "glutes", name = "Ягодицы", emoji = "🍑")
    )

    // ===================== Оборудование =====================
    val equipment = listOf(
        Equipment(code = "barbell", name = "Штанга"),
        Equipment(code = "dumbbell", name = "Гантели"),
        Equipment(code = "machine", name = "Тренажёр"),
        Equipment(code = "kettlebell", name = "Гиря"),
        Equipment(code = "bodyweight", name = "Свой вес"),
        Equipment(code = "cable", name = "Кроссовер / блоки")
    )

    // ===================== Упражнения =====================
    val exercises = listOf(
        // ---------- ГРУДЬ ----------
        Exercise(
            code = "bench_press",
            name = "Жим лёжа штангой",
            description = "Базовое упражнение для развития грудных мышц. Лягте на скамью, хват чуть шире плеч, опустите штангу до груди и выжмите вверх.",
            primaryMuscleCode = "chest",
            secondaryMuscleCode = "triceps",
            equipmentCode = "barbell",
            difficulty = "INTERMEDIATE",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=rT7DgCr-3pg",
            technique = "Лопатки сведены и опущены, стопы плотно на полу, контролируйте опускание 2 счёта."
        ),
        Exercise(
            code = "incline_dumbbell_press",
            name = "Жим гантелей на наклонной скамье",
            description = "Акцент на верхнюю часть груди. Угол скамьи 30–45°, выжимайте гантели вверх до лёгкого касания.",
            primaryMuscleCode = "chest",
            secondaryMuscleCode = "shoulders",
            equipmentCode = "dumbbell",
            difficulty = "INTERMEDIATE",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=8iPEnn-ltC8",
            technique = "Не выпрямляйте руки до конца, сохраняйте напряжение в груди."
        ),
        Exercise(
            code = "pushup",
            name = "Отжимания от пола",
            description = "Универсальное упражнение со своим весом для груди, трицепсов и плеч. Тело — прямая линия.",
            primaryMuscleCode = "chest",
            secondaryMuscleCode = "triceps",
            equipmentCode = "bodyweight",
            difficulty = "BEGINNER",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=IODxDxX7oi4",
            technique = "Не проваливайте поясницу, опускайтесь до касания грудью пола."
        ),
        Exercise(
            code = "cable_fly",
            name = "Сведения рук в кроссовере",
            description = "Изолирующее упражнение для груди. Сводите рукояти перед собой по дуге.",
            primaryMuscleCode = "chest",
            secondaryMuscleCode = null,
            equipmentCode = "cable",
            difficulty = "INTERMEDIATE",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=eozdVDA78K0",
            technique = "Чуть согните локти, акцент на сокращении грудных в точке сведения."
        ),

        // ---------- СПИНА ----------
        Exercise(
            code = "deadlift",
            name = "Становая тяга",
            description = "Король базовых упражнений: работает вся задняя цепь, спина, ягодицы и ноги. Поднимите штангу с пола, выпрямившись.",
            primaryMuscleCode = "back",
            secondaryMuscleCode = "legs",
            equipmentCode = "barbell",
            difficulty = "ADVANCED",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=op9kVnSso6Q",
            technique = "Спина прямая, гриф скользит вдоль голеней, не округляйте поясницу."
        ),
        Exercise(
            code = "pullup",
            name = "Подтягивания",
            description = "Лучшее упражнение для ширины спины со своим весом. Поднимайте подбородок выше перекладины.",
            primaryMuscleCode = "back",
            secondaryMuscleCode = "biceps",
            equipmentCode = "bodyweight",
            difficulty = "INTERMEDIATE",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=eGo4IYlbE5g",
            technique = "Начинайте тяги с сведения лопаток, без раскачки."
        ),
        Exercise(
            code = "barbell_row",
            name = "Тяга штанги в наклоне",
            description = "Базовая тяга для толщины спины. Наклон корпуса ~45°, тяните гриф к поясу.",
            primaryMuscleCode = "back",
            secondaryMuscleCode = "biceps",
            equipmentCode = "barbell",
            difficulty = "INTERMEDIATE",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=kBWAon7ItDw",
            technique = "Держите спину прямой, локти прижаты к телу."
        ),
        Exercise(
            code = "lat_pulldown",
            name = "Тяга верхнего блока",
            description = "Альтернатива подтягиваниям в тренажёре. Тяните рукоять к верхней части груди.",
            primaryMuscleCode = "back",
            secondaryMuscleCode = "biceps",
            equipmentCode = "cable",
            difficulty = "BEGINNER",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=CAwf7n6Luuc",
            technique = "Не отклоняйтесь сильно назад, работайте широчайшими."
        ),

        // ---------- НОГИ ----------
        Exercise(
            code = "squat",
            name = "Приседания со штангой",
            description = "Главное упражнение для ног и ягодиц. Опустите таз ниже параллели бёдер с полом.",
            primaryMuscleCode = "legs",
            secondaryMuscleCode = "glutes",
            equipmentCode = "barbell",
            difficulty = "INTERMEDIATE",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=ultWZbUMPL8",
            technique = "Колени по направлению носков, грудь поднята, вес на середину стопы."
        ),
        Exercise(
            code = "leg_press",
            name = "Жим ногами в тренажёре",
            description = "Безопасная альтернатива приседаниям. Толкайте платформу ногами.",
            primaryMuscleCode = "legs",
            secondaryMuscleCode = "glutes",
            equipmentCode = "machine",
            difficulty = "BEGINNER",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=IZxyjW7MPJQ",
            technique = "Не выпрямляйте колени до упора, поясница прижата."
        ),
        Exercise(
            code = "lunge",
            name = "Выпады",
            description = "Унилатеральное упражнение для ног и ягодиц. Можно с гантелями или своим весом.",
            primaryMuscleCode = "legs",
            secondaryMuscleCode = "glutes",
            equipmentCode = "dumbbell",
            difficulty = "BEGINNER",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=QOVaHwm-Q6U",
            technique = "Колено задней ноги почти касается пола, торс вертикально."
        ),
        Exercise(
            code = "leg_extension",
            name = "Разгибания ног в тренажёре",
            description = "Изоляция на квадрицепс. Разгибайте ноги в коленях против сопротивления.",
            primaryMuscleCode = "legs",
            secondaryMuscleCode = null,
            equipmentCode = "machine",
            difficulty = "BEGINNER",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=YyvSfVjQeL0",
            technique = "Плавное движение, задержка на 1 счёт в верхней точке."
        ),
        Exercise(
            code = "leg_curl",
            name = "Сгибания ног в тренажёре",
            description = "Изоляция задней поверхности бедра. Сгибайте ноги, скользя валиком к ягодицам.",
            primaryMuscleCode = "legs",
            secondaryMuscleCode = null,
            equipmentCode = "machine",
            difficulty = "BEGINNER",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=1Tq3QdYuk2A",
            technique = "Не отрывайте таз от скамьи рывком."
        ),
        Exercise(
            code = "romanian_deadlift",
            name = "Румынская тяга",
            description = "Вариация становой с акцентом на заднюю поверхность бедра и ягодицы. " +
                "Штанга опускается вдоль ног до середины голени, колени чуть согнуты.",
            primaryMuscleCode = "legs",
            secondaryMuscleCode = "glutes",
            equipmentCode = "barbell",
            difficulty = "INTERMEDIATE",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=jEy_czb3RKA",
            technique = "Таз отводите назад, спина прямая, ощущайте растяжение бицепса бедра."
        ),
        Exercise(
            code = "calf_raise",
            name = "Подъёмы на носки",
            description = "Изоляция икроножных мышц. Поднимайтесь на носки стоя (со штангой или в тренажёре).",
            primaryMuscleCode = "legs",
            secondaryMuscleCode = null,
            equipmentCode = "barbell",
            difficulty = "BEGINNER",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=ZDoGANmGPRc",
            technique = "Полная амплитуда — опускайтесь ниже уровня пола, пауза в верхней точке."
        ),

        // ---------- ПЛЕЧИ ----------
        Exercise(
            code = "overhead_press",
            name = "Армейский жим (жим над головой)",
            description = "Базовый жим для плеч. Выжмите штангу над головой из положения стоя.",
            primaryMuscleCode = "shoulders",
            secondaryMuscleCode = "triceps",
            equipmentCode = "barbell",
            difficulty = "INTERMEDIATE",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=2yjwXTZQDDI",
            technique = "Напрягите пресс, не прогибайтесь в пояснице."
        ),
        Exercise(
            code = "lateral_raise",
            name = "Махи гантелями в стороны",
            description = "Изоляция средней дельты. Поднимайте гантели через стороны до горизонтали.",
            primaryMuscleCode = "shoulders",
            secondaryMuscleCode = null,
            equipmentCode = "dumbbell",
            difficulty = "BEGINNER",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=3VcKaXpzqRo",
            technique = "Чуть согните локти, не помогайте корпусом."
        ),
        Exercise(
            code = "face_pull",
            name = "Тяга к лицу (face pull)",
            description = "Здоровье задней дельты и ротаторной манжеты. Тяните канат к лицу.",
            primaryMuscleCode = "shoulders",
            secondaryMuscleCode = "back",
            equipmentCode = "cable",
            difficulty = "BEGINNER",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=rep-qVOkqgk",
            technique = "Локти высоко, разводите рукояти в стороны."
        ),

        // ---------- БИЦЕПС ----------
        Exercise(
            code = "barbell_curl",
            name = "Подъём штанги на бицепс",
            description = "Классика для бицепса. Сгибайте руки со штангой, стоя прямо.",
            primaryMuscleCode = "biceps",
            secondaryMuscleCode = null,
            equipmentCode = "barbell",
            difficulty = "BEGINNER",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=kwG2ipFRgfo",
            technique = "Локти прижаты, без раскачки корпусом."
        ),
        Exercise(
            code = "dumbbell_curl",
            name = "Подъём гантелей на бицепс",
            description = "Подъём гантелей с супинацией (поворотом кисти).",
            primaryMuscleCode = "biceps",
            secondaryMuscleCode = null,
            equipmentCode = "dumbbell",
            difficulty = "BEGINNER",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=ykJmrZ5v0Oo",
            technique = "Разворачивайте кисть ладонью вверх в средней точке."
        ),
        Exercise(
            code = "hammer_curl",
            name = "«Молотки» (hammer curl)",
            description = "Подъём гантелей нейтральным хватом — акцент на брахиалис и предплечья.",
            primaryMuscleCode = "biceps",
            secondaryMuscleCode = null,
            equipmentCode = "dumbbell",
            difficulty = "BEGINNER",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=zC3nLlEvin4",
            technique = "Гантели вертикально, без поворота кисти."
        ),

        // ---------- ТРИЦЕПС ----------
        Exercise(
            code = "triceps_pushdown",
            name = "Разгибания рук на блоке",
            description = "Изоляция трицепса. Разгибайте руки с канатом или рукоятью верхнего блока.",
            primaryMuscleCode = "triceps",
            secondaryMuscleCode = null,
            equipmentCode = "cable",
            difficulty = "BEGINNER",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=2-LAMcpzODU",
            technique = "Локти зафиксированы у корпуса."
        ),
        Exercise(
            code = "skullcrusher",
            name = "Французский жим лёжа",
            description = "Сгибания рук со штангой или EZ-грифом ко лбу/за голову лёжа.",
            primaryMuscleCode = "triceps",
            secondaryMuscleCode = null,
            equipmentCode = "barbell",
            difficulty = "INTERMEDIATE",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=eL_d4ATAA4w",
            technique = "Локти вверх и неподвижны, опускайте плавно."
        ),
        Exercise(
            code = "dips",
            name = "Отжимания на брусьях",
            description = "Базовое для трицепса и груди. Наклон корпуса вперёд смещает акцент на грудь.",
            primaryMuscleCode = "triceps",
            secondaryMuscleCode = "chest",
            equipmentCode = "bodyweight",
            difficulty = "INTERMEDIATE",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=2z8Jmcr5-2A",
            technique = "Опускайтесь до параллели предплечий с полом."
        ),

        // ---------- ПРЕСС ----------
        Exercise(
            code = "plank",
            name = "Планка",
            description = "Статическое упражнение для кора. Удерживайте тело прямой линией.",
            primaryMuscleCode = "abs",
            secondaryMuscleCode = null,
            equipmentCode = "bodyweight",
            difficulty = "BEGINNER",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=ASdvN_XEl_c",
            technique = "Напрягите пресс и ягодицы, не проваливайтесь."
        ),
        Exercise(
            code = "crunch",
            name = "Скручивания",
            description = "Базовое упражнение на прямую мышцу живота. Приподнимайте лопатки.",
            primaryMuscleCode = "abs",
            secondaryMuscleCode = null,
            equipmentCode = "bodyweight",
            difficulty = "BEGINNER",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=Xyd_fa5zoEU",
            technique = "Подбородок не прижимайте к груди, тянитесь ребрами к тазу."
        ),
        Exercise(
            code = "leg_raise",
            name = "Подъём ног лёжа",
            description = "Акцент на нижнюю часть пресса. Поднимайте прямые ноги до 90°.",
            primaryMuscleCode = "abs",
            secondaryMuscleCode = null,
            equipmentCode = "bodyweight",
            difficulty = "BEGINNER",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=IYM4nd-yITA",
            technique = "Поясница прижата к полу."
        ),

        // ---------- ЯГОДИЦЫ ----------
        Exercise(
            code = "hip_thrust",
            name = "Ягодичный мостик со штангой",
            description = "Лучшее упражнение для ягодиц. Выталкивайте таз вверх, гриф на сгибе таза.",
            primaryMuscleCode = "glutes",
            secondaryMuscleCode = "legs",
            equipmentCode = "barbell",
            difficulty = "INTERMEDIATE",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=SEdqd1n0cvg",
            technique = "В верхней точке напрягите ягодицы, не разгибайте спину."
        ),
        Exercise(
            code = "glute_bridge",
            name = "Ягодичный мостик (свой вес)",
            description = "Лёгкая версия мостика без веса. Поднимайте таз вверх.",
            primaryMuscleCode = "glutes",
            secondaryMuscleCode = null,
            equipmentCode = "bodyweight",
            difficulty = "BEGINNER",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=OUgsrma5uLk",
            technique = "Упор на пятки, пауза-сокращение наверху."
        ),
        Exercise(
            code = "kettlebell_swing",
            name = "Махи гирей",
            description = "Динамичное упражнение для ягодиц и задней цепи. Мах гирей за счёт таза.",
            primaryMuscleCode = "glutes",
            secondaryMuscleCode = "back",
            equipmentCode = "kettlebell",
            difficulty = "INTERMEDIATE",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=YSxHifyIq-4",
            technique = "Мах руками, толчок тазом, спина прямая."
        ),
        Exercise(
            code = "dumbbell_bench_press",
            name = "Жим гантелей лёжа",
            description = "Вариант жима для груди с большей амплитудой и независимой работой рук.",
            primaryMuscleCode = "chest",
            secondaryMuscleCode = "triceps",
            equipmentCode = "dumbbell",
            difficulty = "INTERMEDIATE",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=VmB1G1K7v94",
            technique = "Лопатки сведены, гантели опускайте контролируемо до уровня груди."
        ),
        Exercise(
            code = "machine_chest_press",
            name = "Жим в тренажёре на грудь",
            description = "Безопасный вариант жима для груди в тренажёре с фиксированной траекторией.",
            primaryMuscleCode = "chest",
            secondaryMuscleCode = "triceps",
            equipmentCode = "machine",
            difficulty = "BEGINNER",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=sqNwDkUU_Ps",
            technique = "Спина прижата к спинке, локти не поднимайте слишком высоко."
        ),
        Exercise(
            code = "incline_barbell_press",
            name = "Жим штанги на наклонной скамье",
            description = "Базовый жим с акцентом на верх груди.",
            primaryMuscleCode = "chest",
            secondaryMuscleCode = "shoulders",
            equipmentCode = "barbell",
            difficulty = "INTERMEDIATE",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=SrqOu55lrYU",
            technique = "Угол скамьи 30-45°, штангу опускайте к верхней части груди."
        ),
        Exercise(
            code = "pec_deck",
            name = "Бабочка в тренажёре",
            description = "Изолирующее упражнение для груди в тренажёре pec deck.",
            primaryMuscleCode = "chest",
            secondaryMuscleCode = null,
            equipmentCode = "machine",
            difficulty = "BEGINNER",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=Z57CtFmRMxA",
            technique = "Не заводите локти слишком далеко назад, сводите руки плавно."
        ),
        Exercise(
            code = "dumbbell_fly",
            name = "Разводка гантелей лёжа",
            description = "Изоляция груди с растяжением в нижней точке.",
            primaryMuscleCode = "chest",
            secondaryMuscleCode = null,
            equipmentCode = "dumbbell",
            difficulty = "INTERMEDIATE",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=eozdVDA78K0",
            technique = "Локти слегка согнуты, движение по дуге, без рывков."
        ),
        Exercise(
            code = "seated_cable_row",
            name = "Горизонтальная тяга блока",
            description = "Тяга к поясу для широчайших и середины спины.",
            primaryMuscleCode = "back",
            secondaryMuscleCode = "biceps",
            equipmentCode = "cable",
            difficulty = "BEGINNER",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=GZbfZ033f74",
            technique = "Начинайте движение со сведения лопаток, корпус не раскачивайте."
        ),
        Exercise(
            code = "one_arm_dumbbell_row",
            name = "Тяга гантели одной рукой",
            description = "Односторонняя тяга для широчайших и контроля дисбаланса.",
            primaryMuscleCode = "back",
            secondaryMuscleCode = "biceps",
            equipmentCode = "dumbbell",
            difficulty = "BEGINNER",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=pYcpY20QaE8",
            technique = "Тяните локоть назад к тазу, не разворачивайте корпус."
        ),
        Exercise(
            code = "t_bar_row",
            name = "Тяга Т-грифа",
            description = "Мощная тяга для толщины спины.",
            primaryMuscleCode = "back",
            secondaryMuscleCode = "biceps",
            equipmentCode = "barbell",
            difficulty = "INTERMEDIATE",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=j3Igk5nyZE4",
            technique = "Спина прямая, тяните рукоять к нижней части груди."
        ),
        Exercise(
            code = "machine_row",
            name = "Тяга в тренажёре",
            description = "Тяга для спины с фиксированной траекторией.",
            primaryMuscleCode = "back",
            secondaryMuscleCode = "biceps",
            equipmentCode = "machine",
            difficulty = "BEGINNER",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=TeFo51Q_Nsc",
            technique = "Не поднимайте плечи к ушам, сводите лопатки в конце тяги."
        ),
        Exercise(
            code = "straight_arm_pulldown",
            name = "Пуловер на верхнем блоке",
            description = "Изоляция широчайших прямыми руками на блоке.",
            primaryMuscleCode = "back",
            secondaryMuscleCode = null,
            equipmentCode = "cable",
            difficulty = "INTERMEDIATE",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=AjCCGN2tU3Q",
            technique = "Локти слегка согнуты, тяните рукоять к бёдрам за счёт спины."
        ),
        Exercise(
            code = "back_extension",
            name = "Гиперэкстензия",
            description = "Упражнение для разгибателей спины, ягодиц и задней поверхности бедра.",
            primaryMuscleCode = "back",
            secondaryMuscleCode = "glutes",
            equipmentCode = "bodyweight",
            difficulty = "BEGINNER",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=ph3pddpKzzw",
            technique = "Поднимайтесь до прямой линии корпуса, не переразгибайте поясницу."
        ),
        Exercise(
            code = "hack_squat",
            name = "Гакк-приседания",
            description = "Приседания в тренажёре с акцентом на квадрицепсы.",
            primaryMuscleCode = "legs",
            secondaryMuscleCode = "glutes",
            equipmentCode = "machine",
            difficulty = "INTERMEDIATE",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=0tn5K9NlCfo",
            technique = "Колени направлены по носкам, поясница прижата к платформе."
        ),
        Exercise(
            code = "smith_squat",
            name = "Приседания в Смите",
            description = "Приседания в машине Смита с фиксированной вертикальной траекторией.",
            primaryMuscleCode = "legs",
            secondaryMuscleCode = "glutes",
            equipmentCode = "machine",
            difficulty = "BEGINNER",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=fEuYM-miK5U",
            technique = "Не выводите стопы слишком далеко, держите колени стабильными."
        ),
        Exercise(
            code = "bulgarian_split_squat",
            name = "Болгарские выпады",
            description = "Одностороннее упражнение для ног и ягодиц с задней ногой на опоре.",
            primaryMuscleCode = "legs",
            secondaryMuscleCode = "glutes",
            equipmentCode = "dumbbell",
            difficulty = "INTERMEDIATE",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=2C-uNgKwPLE",
            technique = "Передняя стопа стоит устойчиво, корпус слегка наклонён вперёд."
        ),
        Exercise(
            code = "goblet_squat",
            name = "Гоблет-приседания",
            description = "Приседания с гантелью или гирей перед грудью.",
            primaryMuscleCode = "legs",
            secondaryMuscleCode = "glutes",
            equipmentCode = "dumbbell",
            difficulty = "BEGINNER",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=MeIiIdhvXT4",
            technique = "Держите вес у груди, локти направляйте вниз между коленями."
        ),
        Exercise(
            code = "front_squat",
            name = "Фронтальные приседания",
            description = "Приседания со штангой на передних дельтах, акцент на квадрицепсы.",
            primaryMuscleCode = "legs",
            secondaryMuscleCode = "glutes",
            equipmentCode = "barbell",
            difficulty = "ADVANCED",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=uYumuL_G_V0",
            technique = "Локти держите высоко, корпус максимально вертикально."
        ),
        Exercise(
            code = "walking_lunge",
            name = "Выпады в ходьбе",
            description = "Динамический вариант выпадов для ног и ягодиц.",
            primaryMuscleCode = "legs",
            secondaryMuscleCode = "glutes",
            equipmentCode = "dumbbell",
            difficulty = "INTERMEDIATE",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=L8fvypPrzzs",
            technique = "Шагайте достаточно широко, колено задней ноги опускайте контролируемо."
        ),
        Exercise(
            code = "seated_calf_raise",
            name = "Подъёмы на носки сидя",
            description = "Изоляция камбаловидной мышцы голени в тренажёре.",
            primaryMuscleCode = "legs",
            secondaryMuscleCode = null,
            equipmentCode = "machine",
            difficulty = "BEGINNER",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=JbyjNymZOt0",
            technique = "Работайте в полной амплитуде, делайте паузу в верхней точке."
        ),
        Exercise(
            code = "arnold_press",
            name = "Жим Арнольда",
            description = "Жим гантелей с разворотом кистей для плеч.",
            primaryMuscleCode = "shoulders",
            secondaryMuscleCode = "triceps",
            equipmentCode = "dumbbell",
            difficulty = "INTERMEDIATE",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=6Z15_WdXmVw",
            technique = "Разворачивайте кисти плавно, не прогибайтесь в пояснице."
        ),
        Exercise(
            code = "seated_dumbbell_press",
            name = "Жим гантелей сидя",
            description = "Базовый жим для плеч с гантелями.",
            primaryMuscleCode = "shoulders",
            secondaryMuscleCode = "triceps",
            equipmentCode = "dumbbell",
            difficulty = "INTERMEDIATE",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=B-aVuyhvLHU",
            technique = "Спина прижата, гантели выжимайте вверх без удара друг о друга."
        ),
        Exercise(
            code = "machine_shoulder_press",
            name = "Жим плеч в тренажёре",
            description = "Жим для дельт в тренажёре с фиксированной траекторией.",
            primaryMuscleCode = "shoulders",
            secondaryMuscleCode = "triceps",
            equipmentCode = "machine",
            difficulty = "BEGINNER",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=WvLMauqrnK8",
            technique = "Сиденье настройте так, чтобы рукояти начинались примерно на уровне ушей."
        ),
        Exercise(
            code = "rear_delt_fly",
            name = "Разведения на заднюю дельту",
            description = "Изоляция задней дельты с гантелями или в наклоне.",
            primaryMuscleCode = "shoulders",
            secondaryMuscleCode = "back",
            equipmentCode = "dumbbell",
            difficulty = "BEGINNER",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=ttvfGg9d76c",
            technique = "Движение ведите локтями в стороны, не включайте трапеции."
        ),
        Exercise(
            code = "reverse_pec_deck",
            name = "Обратная бабочка",
            description = "Тренажёр для задней дельты и верхней части спины.",
            primaryMuscleCode = "shoulders",
            secondaryMuscleCode = "back",
            equipmentCode = "machine",
            difficulty = "BEGINNER",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=6fM4Jm0VDkU",
            technique = "Грудь прижата к спинке, руки разводите до линии корпуса."
        ),
        Exercise(
            code = "preacher_curl",
            name = "Сгибания на скамье Скотта",
            description = "Изоляция бицепса на специальной скамье.",
            primaryMuscleCode = "biceps",
            secondaryMuscleCode = null,
            equipmentCode = "barbell",
            difficulty = "BEGINNER",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=fIWP-FRFNU0",
            technique = "Не разгибайте локти рывком до упора, поднимайте вес плавно."
        ),
        Exercise(
            code = "cable_curl",
            name = "Сгибания рук на нижнем блоке",
            description = "Бицепс на блоке с постоянным натяжением.",
            primaryMuscleCode = "biceps",
            secondaryMuscleCode = null,
            equipmentCode = "cable",
            difficulty = "BEGINNER",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=NFzTWp2qpiE",
            technique = "Локти фиксированы у корпуса, не отклоняйтесь назад."
        ),
        Exercise(
            code = "incline_dumbbell_curl",
            name = "Сгибания гантелей на наклонной скамье",
            description = "Бицепс с сильным растяжением в нижней точке.",
            primaryMuscleCode = "biceps",
            secondaryMuscleCode = null,
            equipmentCode = "dumbbell",
            difficulty = "INTERMEDIATE",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=soxrZlIl35U",
            technique = "Плечи остаются назад, локти не выводите вперёд."
        ),
        Exercise(
            code = "concentration_curl",
            name = "Концентрированные сгибания",
            description = "Изолированная работа бицепса сидя с упором локтя.",
            primaryMuscleCode = "biceps",
            secondaryMuscleCode = null,
            equipmentCode = "dumbbell",
            difficulty = "BEGINNER",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=0AUGkch3tzc",
            technique = "Поднимайте гантель без раскачки, контролируйте опускание."
        ),
        Exercise(
            code = "overhead_triceps_extension",
            name = "Разгибание гантели из-за головы",
            description = "Трицепс с акцентом на длинную головку.",
            primaryMuscleCode = "triceps",
            secondaryMuscleCode = null,
            equipmentCode = "dumbbell",
            difficulty = "BEGINNER",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=_gsUck-7M74",
            technique = "Локти направлены вверх, не разводите их слишком широко."
        ),
        Exercise(
            code = "rope_pushdown",
            name = "Разгибания рук с канатом",
            description = "Вариант разгибаний на блоке с канатной рукоятью.",
            primaryMuscleCode = "triceps",
            secondaryMuscleCode = null,
            equipmentCode = "cable",
            difficulty = "BEGINNER",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=vB5OHsJ3EME",
            technique = "Внизу разводите концы каната в стороны, локти держите у корпуса."
        ),
        Exercise(
            code = "close_grip_bench_press",
            name = "Жим лёжа узким хватом",
            description = "Базовое упражнение для трицепса с участием груди.",
            primaryMuscleCode = "triceps",
            secondaryMuscleCode = "chest",
            equipmentCode = "barbell",
            difficulty = "INTERMEDIATE",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=nEF0bv2FW94",
            technique = "Хват чуть уже плеч, локти не разводите слишком широко."
        ),
        Exercise(
            code = "bench_dips",
            name = "Обратные отжимания от скамьи",
            description = "Упражнение для трицепса со своим весом от скамьи.",
            primaryMuscleCode = "triceps",
            secondaryMuscleCode = "chest",
            equipmentCode = "bodyweight",
            difficulty = "BEGINNER",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=0326dy_-CzM",
            technique = "Плечи не поднимайте к ушам, опускайтесь контролируемо."
        ),
        Exercise(
            code = "hanging_leg_raise",
            name = "Подъём ног в висе",
            description = "Упражнение для пресса с акцентом на нижнюю часть.",
            primaryMuscleCode = "abs",
            secondaryMuscleCode = null,
            equipmentCode = "bodyweight",
            difficulty = "INTERMEDIATE",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=Pr1ieGZ5atk",
            technique = "Не раскачивайтесь, подкручивайте таз в верхней точке."
        ),
        Exercise(
            code = "cable_crunch",
            name = "Скручивания на блоке",
            description = "Пресс на верхнем блоке с регулируемой нагрузкой.",
            primaryMuscleCode = "abs",
            secondaryMuscleCode = null,
            equipmentCode = "cable",
            difficulty = "BEGINNER",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=AV5PmZJIrrw",
            technique = "Скручивайте корпус, а не просто тяните руками канат вниз."
        ),
        Exercise(
            code = "russian_twist",
            name = "Русские скручивания",
            description = "Повороты корпуса для косых мышц живота.",
            primaryMuscleCode = "abs",
            secondaryMuscleCode = null,
            equipmentCode = "bodyweight",
            difficulty = "BEGINNER",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=wkD8rjkodUI",
            technique = "Держите корпус устойчиво, поворачивайтесь грудной клеткой."
        ),
        Exercise(
            code = "ab_wheel_rollout",
            name = "Ролик для пресса",
            description = "Сложное упражнение для кора с роликом.",
            primaryMuscleCode = "abs",
            secondaryMuscleCode = "back",
            equipmentCode = "bodyweight",
            difficulty = "ADVANCED",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=A3uK5TPzHq8",
            technique = "Не проваливайте поясницу, возвращайтесь за счёт напряжения пресса."
        ),
        Exercise(
            code = "side_plank",
            name = "Боковая планка",
            description = "Статическое упражнение для косых мышц и стабилизаторов корпуса.",
            primaryMuscleCode = "abs",
            secondaryMuscleCode = null,
            equipmentCode = "bodyweight",
            difficulty = "BEGINNER",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=K2VljzCC16g",
            technique = "Тело держите прямой линией, таз не опускайте."
        ),
        Exercise(
            code = "cable_kickback",
            name = "Отведение ноги назад в кроссовере",
            description = "Изоляция ягодиц на нижнем блоке.",
            primaryMuscleCode = "glutes",
            secondaryMuscleCode = "legs",
            equipmentCode = "cable",
            difficulty = "BEGINNER",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=ifP5Xap0qd8",
            technique = "Не прогибайтесь в пояснице, движение ведите пяткой назад."
        ),
        Exercise(
            code = "hip_abduction_machine",
            name = "Разведение ног в тренажёре",
            description = "Тренажёр для средней ягодичной мышцы.",
            primaryMuscleCode = "glutes",
            secondaryMuscleCode = null,
            equipmentCode = "machine",
            difficulty = "BEGINNER",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=G_8LItOI8f0",
            technique = "Не раскачивайтесь, задерживайтесь на секунду в разведении."
        ),
        Exercise(
            code = "sumo_deadlift",
            name = "Становая тяга сумо",
            description = "Вариант становой с широкой постановкой ног и акцентом на ягодицы/приводящие.",
            primaryMuscleCode = "glutes",
            secondaryMuscleCode = "legs",
            equipmentCode = "barbell",
            difficulty = "ADVANCED",
            imageUrl = null,
            videoUrl = "https://www.youtube.com/watch?v=9ZuXKqRbT9k",
            technique = "Колени направляйте по носкам, спину держите прямой."
        )
    )
}
