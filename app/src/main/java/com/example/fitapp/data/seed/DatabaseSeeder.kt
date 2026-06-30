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
            name = "Сгибания ног (бицепс бедра)",
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
        )
    )
}
