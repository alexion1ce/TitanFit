package com.example.fitapp.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fitapp.data.local.entity.ExperienceLevel
import com.example.fitapp.data.local.entity.FitnessGoal
import com.example.fitapp.data.local.entity.Gender
import com.example.fitapp.data.local.entity.MuscleFocus
import com.example.fitapp.data.local.entity.PreferredDuration
import com.example.fitapp.data.local.entity.WorkoutLocation
import com.example.fitapp.ui.programs.ProgramCard
import java.util.Locale

private val Background = Color(0xFF020304)
private val CardBg = Color(0xFF0D0F12)
private val CardEdge = Color(0xFF1F242D)
private val AccentRed = Color(0xFFFF3B30)
private val AccentTeal = Color(0xFF00E5FF)
private val TextPrimary = Color(0xFFFFFFFF)
private val TextSecondary = Color(0xFF9EA4B0)

@Composable
fun OnboardingScreen(
    onFinish: (selectedProgramId: Long?) -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            if (state.currentStep != 6) {
                HeaderProgress(
                    currentStep = state.currentStep,
                    totalSteps = state.totalSteps,
                    onBack = { if (state.currentStep > 1) viewModel.prevStep() }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Box(modifier = Modifier.weight(1f)) {
                AnimatedContent(
                    targetState = state.currentStep,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "onboarding_step"
                ) { step ->
                    when (step) {
                        1 -> Step1Bio(state, viewModel)
                        2 -> Step2Goal(state, viewModel)
                        3 -> Step3Location(state, viewModel)
                        4 -> Step4Focus(state, viewModel)
                        5 -> Step5ExperienceAndDuration(state, viewModel)
                        6 -> Step6PlanGeneration(state)
                        7 -> Step7Result(state, viewModel, onFinish)
                    }
                }
            }

            if (state.currentStep < 6) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.nextStep() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
                ) {
                    Text("Далее", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White)
                }
            }
        }
    }
}

@Composable
private fun HeaderProgress(currentStep: Int, totalSteps: Int, onBack: () -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (currentStep > 1 && currentStep < 6) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = TextPrimary)
                }
            } else {
                Spacer(Modifier.width(48.dp))
            }
            Text(
                "Шаг $currentStep из $totalSteps",
                color = TextSecondary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.width(48.dp))
        }
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { currentStep.toFloat() / totalSteps.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = AccentRed,
            trackColor = CardEdge
        )
    }
}

// ================= STEP 1: BIO =================
@Composable
private fun Step1Bio(state: OnboardingUiState, viewModel: OnboardingViewModel) {
    val profile = state.tempProfile

    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            StepHeader(
                title = "Ваши параметры",
                subtitle = "Эти данные помогут рассчитать суточную норму калорий и ИМТ"
            )
        }
        item {
            Text("Пол", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SelectCard(
                    title = "Мужской",
                    selected = state.gender == Gender.MALE,
                    onClick = { viewModel.setGender(Gender.MALE) },
                    modifier = Modifier.weight(1f)
                )
                SelectCard(
                    title = "Женский",
                    selected = state.gender == Gender.FEMALE,
                    onClick = { viewModel.setGender(Gender.FEMALE) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        item {
            ValueInputSliderCard(
                label = "Возраст",
                unit = "лет",
                value = state.age.toFloat(),
                range = 14f..80f,
                isDecimal = false,
                onValueChange = { viewModel.setAge(it.toInt()) }
            )
        }
        item {
            ValueInputSliderCard(
                label = "Рост",
                unit = "см",
                value = state.heightCm.toFloat(),
                range = 130f..220f,
                isDecimal = false,
                onValueChange = { viewModel.setHeight(it.toDouble()) }
            )
        }
        item {
            ValueInputSliderCard(
                label = "Вес",
                unit = "кг",
                value = state.weightKg.toFloat(),
                range = 40f..160f,
                isDecimal = true,
                onValueChange = { viewModel.setWeight(it.toDouble()) }
            )
        }
        item {
            MetricsPreviewChip(bmi = profile.bmi, bmiCategory = profile.bmiCategory, bmr = profile.bmrCalories)
        }
    }
}

// ================= STEP 2: GOAL =================

@Composable
private fun Step2Goal(state: OnboardingUiState, viewModel: OnboardingViewModel) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        StepHeader(
            title = "Какая ваша главная цель?",
            subtitle = "Мы подберём программу с подходящим объёмом и нагрузкой"
        )
        Spacer(Modifier.height(16.dp))
        FitnessGoal.entries.forEach { goal ->
            SelectCardFull(
                title = goal.title,
                description = goal.description,
                selected = state.goal == goal,
                onClick = { viewModel.setGoal(goal) }
            )
            Spacer(Modifier.height(12.dp))
        }
    }
}

// ================= STEP 3: LOCATION =================
@Composable
private fun Step3Location(state: OnboardingUiState, viewModel: OnboardingViewModel) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        StepHeader(
            title = "Где будете тренироваться?",
            subtitle = "Выберите вашу основную локацию и снаряжение"
        )
        Spacer(Modifier.height(16.dp))
        WorkoutLocation.entries.forEach { loc ->
            val icon = when (loc) {
                WorkoutLocation.GYM -> Icons.Outlined.FitnessCenter
                WorkoutLocation.HOME_DUMBBELLS -> Icons.Outlined.FitnessCenter
                WorkoutLocation.HOME_BODYWEIGHT -> Icons.Outlined.Home
            }
            SelectCardFullWithIcon(
                title = loc.title,
                description = loc.description,
                icon = icon,
                selected = state.location == loc,
                onClick = { viewModel.setLocation(loc) }
            )
            Spacer(Modifier.height(12.dp))
        }
    }
}

// ================= STEP 4: MUSCLE FOCUS =================
@Composable
private fun Step4Focus(state: OnboardingUiState, viewModel: OnboardingViewModel) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        StepHeader(
            title = "Акцентные группы мышц",
            subtitle = "На какую мышечную зону вы хотите сделать максимальный упор?"
        )
        Spacer(Modifier.height(16.dp))
        MuscleFocus.entries.forEach { focus ->
            SelectCardFull(
                title = focus.title,
                description = focus.description,
                selected = state.focus == focus,
                onClick = { viewModel.setFocus(focus) }
            )
            Spacer(Modifier.height(12.dp))
        }
    }
}


// ================= STEP 5: EXPERIENCE & DURATION =================
@Composable
private fun Step5ExperienceAndDuration(state: OnboardingUiState, viewModel: OnboardingViewModel) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            StepHeader(
                title = "Опыт и время",
                subtitle = "Настроим оптимальный уровень сложности и длительность занятия"
            )
        }
        item {
            Text("Ваш тренировочный стаж", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            ExperienceLevel.entries.forEach { exp ->
                SelectCardFull(
                    title = exp.title,
                    description = exp.description,
                    selected = state.experience == exp,
                    onClick = { viewModel.setExperience(exp) }
                )
                Spacer(Modifier.height(8.dp))
            }
        }
        item {
            Text("Предпочитаемая длительность", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                PreferredDuration.entries.forEach { dur ->
                    SelectChipCard(
                        title = dur.title,
                        subtitle = dur.minutesText,
                        selected = state.preferredDuration == dur,
                        onClick = { viewModel.setPreferredDuration(dur) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
        item {
            Text("Тренировок в неделю", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf(2, 3, 4).forEach { days ->
                    val label = if (days == 4) "4–5 дней" else "$days дня"
                    SelectCard(
                        title = label,
                        selected = state.daysPerWeek == days,
                        onClick = { viewModel.setDaysPerWeek(days) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

// ================= STEP 6: PLAN GENERATION =================
@Composable
private fun Step6PlanGeneration(state: OnboardingUiState) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(96.dp),
            color = Color(0xFF171B21),
            shape = CircleShape,
            border = BorderStroke(2.dp, AccentRed)
        ) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { state.generationProgress },
                    color = AccentRed,
                    trackColor = CardEdge,
                    strokeWidth = 4.dp,
                    modifier = Modifier.fillMaxSize()
                )
                Icon(
                    Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    tint = AccentRed,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Spacer(Modifier.height(28.dp))

        Text(
            "Формирование вашего плана...",
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(12.dp))

        Text(
            state.generationMessage,
            color = AccentTeal,
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(32.dp))

        LinearProgressIndicator(
            progress = { state.generationProgress },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = AccentRed,
            trackColor = CardEdge
        )
    }
}

// ================= STEP 7: RESULTS =================
@Composable
private fun Step7Result(
    state: OnboardingUiState,
    viewModel: OnboardingViewModel,
    onFinish: (Long?) -> Unit
) {
    val profile = state.tempProfile

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            StepHeader(
                title = "Ваш персональный план готов!",
                subtitle = "Мы рассчитали идеальную норму питания и подобрали комплекс"
            )
        }

        item {
            TargetCaloriesCard(targetCalories = profile.dailyTargetCalories, bmr = profile.bmrCalories)
        }

        item {
            Text(
                "Рекомендуемые тренировки",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }

        if (state.isLoading) {
            item {
                Box(Modifier.fillMaxWidth().height(140.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AccentRed)
                }
            }
        } else {
            items(state.recommendedPrograms, key = { it.workout.id }) { card ->
                RecommendedProgramCard(
                    program = card,
                    selected = state.selectedProgramId == card.workout.id,
                    onClick = { viewModel.selectProgram(card.workout.id) }
                )
            }
        }

        item {
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = { viewModel.finishOnboarding(onFinish) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
            ) {
                Text("Начать тренироваться", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

// ================= HELPERS & COMPONENTS =================

@Composable
private fun StepHeader(title: String, subtitle: String) {
    Column {
        Text(title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 26.sp)
        Spacer(Modifier.height(6.dp))
        Text(subtitle, color = TextSecondary, fontSize = 15.sp)
    }
}

@Composable
private fun SelectCard(title: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .height(52.dp)
            .clickable(onClick = onClick),
        color = if (selected) AccentRed.copy(alpha = 0.15f) else CardBg,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if (selected) AccentRed else CardEdge)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                title,
                color = if (selected) AccentRed else TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun SelectChipCard(title: String, subtitle: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .height(64.dp)
            .clickable(onClick = onClick),
        color = if (selected) AccentRed.copy(alpha = 0.15f) else CardBg,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if (selected) AccentRed else CardEdge)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                title,
                color = if (selected) AccentRed else TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Text(
                subtitle,
                color = if (selected) AccentRed.copy(alpha = 0.8f) else TextSecondary,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun SelectCardFull(title: String, description: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = if (selected) AccentRed.copy(alpha = 0.15f) else CardBg,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, if (selected) AccentRed else CardEdge)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = if (selected) AccentRed else TextPrimary, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Spacer(Modifier.height(4.dp))
                Text(description, color = TextSecondary, fontSize = 13.sp)
            }
            if (selected) {
                Icon(Icons.Filled.Check, contentDescription = null, tint = AccentRed)
            }
        }
    }
}

@Composable
private fun SelectCardFullWithIcon(title: String, description: String, icon: ImageVector, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = if (selected) AccentRed.copy(alpha = 0.15f) else CardBg,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, if (selected) AccentRed else CardEdge)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                color = if (selected) AccentRed.copy(alpha = 0.2f) else Color(0xFF171B21),
                shape = CircleShape
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = if (selected) AccentRed else TextSecondary, modifier = Modifier.size(24.dp))
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = if (selected) AccentRed else TextPrimary, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Spacer(Modifier.height(4.dp))
                Text(description, color = TextSecondary, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun ValueInputSliderCard(
    label: String,
    unit: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    isDecimal: Boolean = false,
    onValueChange: (Float) -> Unit
) {
    var textValue by remember(value) {
        mutableStateOf(if (isDecimal) String.format(Locale.US, "%.1f", value) else value.toInt().toString())
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = CardBg,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, CardEdge)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(label, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BasicTextField(
                        value = textValue,
                        onValueChange = { input ->
                            textValue = input
                            val parsed = input.replace(',', '.').toFloatOrNull()
                            if (parsed != null && parsed in range) {
                                onValueChange(parsed)
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = if (isDecimal) KeyboardType.Decimal else KeyboardType.Number
                        ),
                        singleLine = true,
                        textStyle = TextStyle(
                            color = AccentRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            textAlign = TextAlign.End
                        ),
                        modifier = Modifier
                            .width(76.dp)
                            .background(Color(0xFF171B21), RoundedCornerShape(8.dp))
                            .border(1.dp, CardEdge, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(unit, color = TextSecondary, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                }
            }
            Spacer(Modifier.height(10.dp))
            Slider(
                value = value.coerceIn(range.start, range.endInclusive),
                onValueChange = { newVal ->
                    onValueChange(newVal)
                },
                valueRange = range,
                colors = SliderDefaults.colors(
                    thumbColor = AccentRed,
                    activeTrackColor = AccentRed,
                    inactiveTrackColor = CardEdge
                )
            )
        }
    }
}

@Composable
private fun MetricsPreviewChip(bmi: Double, bmiCategory: String, bmr: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF171B21),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, CardEdge)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("ИМТ", color = TextSecondary, fontSize = 12.sp)
                Text(String.format(Locale.US, "%.1f", bmi), color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(bmiCategory, color = AccentTeal, fontSize = 11.sp)
            }
            Box(Modifier.width(1.dp).height(30.dp).background(CardEdge))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Базовый BMR", color = TextSecondary, fontSize = 12.sp)
                Text("$bmr ккал", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Минимальный расход", color = TextSecondary, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun TargetCaloriesCard(targetCalories: Int, bmr: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = CardBg,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, CardEdge)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(52.dp),
                color = AccentRed.copy(alpha = 0.2f),
                shape = CircleShape
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.LocalFireDepartment, contentDescription = null, tint = AccentRed, modifier = Modifier.size(30.dp))
                }
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text("Целевой суточный калораж", color = TextSecondary, fontSize = 13.sp)
                Text("$targetCalories ккал / день", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                Text("Базовый метаболизм (BMR): $bmr ккал", color = AccentTeal, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun RecommendedProgramCard(program: ProgramCard, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = if (selected) Color(0xFF1C222B) else CardBg,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.5.dp, if (selected) AccentRed else CardEdge)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                color = if (selected) AccentRed.copy(alpha = 0.2f) else Color(0xFF171B21),
                shape = CircleShape
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.FitnessCenter, contentDescription = null, tint = if (selected) AccentRed else TextSecondary, modifier = Modifier.size(24.dp))
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(program.workout.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${program.exerciseCount} упражнений • ${program.totalSets} подходов", color = TextSecondary, fontSize = 13.sp)
            }
            if (selected) {
                Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = AccentRed, modifier = Modifier.size(24.dp))
            }
        }
    }
}
