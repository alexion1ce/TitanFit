package com.example.fitapp.data.local.entity

enum class Gender { MALE, FEMALE }

enum class FitnessGoal(val title: String, val description: String) {
    MUSCLE("Набор мышечной массы", "Увеличение объёма и гипертрофия мышц"),
    FAT_LOSS("Похудение и рельеф", "Сжигание жира с сохранением мышц"),
    STRENGTH("Сила и выносливость", "Максимальные силовые показатели в базовых движениях"),
    HEALTH("Здоровье и тонус", "Поддержание отличного самочувствия и формы")
}

enum class WorkoutLocation(val title: String, val description: String) {
    GYM("Фитнес-зал", "Полный арсенал: тренажёры, штанги, гантели и блоки"),
    HOME_DUMBBELLS("Дома с гантелями", "Тренировки дома с разборными или фиксированными гантелями"),
    HOME_BODYWEIGHT("Дома со своим весом", "Упражнения без специального снаряжения (турник, отжимания)")
}

enum class ExperienceLevel(val title: String, val description: String) {
    BEGINNER("Новичок", "Менее 6 месяцев системных тренировок"),
    INTERMEDIATE("Средний уровень", "От 6 месяцев до 2 лет регулярных занятий"),
    ADVANCED("Опытный", "Более 2 лет регулярных интенсивных тренировок")
}

enum class MuscleFocus(val title: String, val description: String) {
    FULL_BODY("Равномерно всё тело", "Гармоничное развитие всех основных мышечных групп"),
    ARM_CHEST("Грудь и Руки", "Усиленная нагрузка на грудные мышцы, бицепсы и трицепсы"),
    UPPER_BODY("Грудь и Спина", "Мощный верхний плечевой пояс и ровная осанка"),
    LEGS_GLUTES("Ноги и Ягодицы", "Акцент на квадрицепсы, бицепсы бедра и ягодичную зону"),
    CORE_ABS("Пресс и Кор", "Укрепление мышечного корсета и рельефный пресс")
}

enum class PreferredDuration(val title: String, val description: String, val minutesText: String) {
    SHORT("Экспресс", "Высокая интенсивность с коротким отдыхом", "30–40 мин"),
    MEDIUM("Стандарт", "Оптимальный баланс нагрузки и восстановления", "45–60 мин"),
    LONG("Объемная", "Глубокая проработка с полным отдыхом между подходами", "75–90 мин")
}

data class UserProfile(
    val gender: Gender = Gender.MALE,
    val age: Int = 25,
    val heightCm: Double = 175.0,
    val weightKg: Double = 75.0,
    val goal: FitnessGoal = FitnessGoal.MUSCLE,
    val location: WorkoutLocation = WorkoutLocation.GYM,
    val experience: ExperienceLevel = ExperienceLevel.BEGINNER,
    val daysPerWeek: Int = 3,
    val focus: MuscleFocus = MuscleFocus.FULL_BODY,
    val preferredDuration: PreferredDuration = PreferredDuration.MEDIUM,
    val onboardingCompleted: Boolean = false
) {
    val bmi: Double
        get() {
            val heightM = heightCm / 100.0
            return if (heightM > 0) weightKg / (heightM * heightM) else 0.0
        }

    val bmiCategory: String
        get() = when {
            bmi < 18.5 -> "Дефицит массы"
            bmi < 25.0 -> "Нормальный вес"
            bmi < 30.0 -> "Избыточный вес"
            else -> "Высокий ИМТ"
        }

    /** Расчёт BMR по формуле Миффлина-Сан Жеора */
    val bmrCalories: Int
        get() {
            val base = 10 * weightKg + 6.25 * heightCm - 5 * age
            val result = if (gender == Gender.MALE) base + 5 else base - 161
            return result.toInt().coerceAtLeast(1000)
        }

    /** Примерный суточный целевой калораж с учётом активности и цели */
    val dailyTargetCalories: Int
        get() {
            val activityMultiplier = when (daysPerWeek) {
                2 -> 1.375
                3 -> 1.55
                else -> 1.725
            }
            val tdee = bmrCalories * activityMultiplier
            return when (goal) {
                FitnessGoal.MUSCLE -> (tdee + 300).toInt()
                FitnessGoal.FAT_LOSS -> (tdee - 400).toInt().coerceAtLeast(1200)
                FitnessGoal.STRENGTH -> (tdee + 200).toInt()
                FitnessGoal.HEALTH -> tdee.toInt()
            }
        }
}
