package com.example.fitapp.ui.today

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fitapp.data.repository.LastWorkoutSummary
import com.example.fitapp.data.repository.MuscleRecovery
import com.example.fitapp.data.repository.SuggestedWorkout
import com.example.fitapp.data.repository.TodaySnapshot
import com.example.fitapp.data.repository.UnfinishedSession
import com.example.fitapp.data.repository.WeekDayStatus
import com.example.fitapp.ui.components.FitAccentRed
import com.example.fitapp.ui.components.FitAccentTeal
import com.example.fitapp.ui.components.FitBrushedSteelCard
import com.example.fitapp.ui.components.FitCardBorder
import com.example.fitapp.ui.components.FitHybridScreen
import com.example.fitapp.ui.components.FitInk
import com.example.fitapp.ui.components.FitMuted
import com.example.fitapp.ui.components.FitMutedLight
import com.example.fitapp.ui.components.FitScreenHeader
import com.example.fitapp.ui.components.FitSectionTitle
import com.example.fitapp.ui.components.FitSurfaceCard
import com.example.fitapp.ui.components.MuscleLoadMap
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

private val SteelInk = Color(0xFF121820)
private val SteelMuted = Color(0xFF303944)
private val RecoveringOrange = Color(0xFFFF9500)
private val ruLocale = Locale("ru")

@Composable
fun TodayScreen(
    onStartWorkout: (workoutId: Long) -> Unit,
    onOpenProgram: (workoutId: Long) -> Unit,
    onOpenPrograms: () -> Unit,
    onOpenMyWorkouts: () -> Unit,
    onCreateWorkout: () -> Unit,
    onOpenLog: (logId: Long) -> Unit,
    viewModel: TodayViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.refresh()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val (greeting, dateText) = remember { greetingAndDate() }

    FitHybridScreen {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, top = 42.dp, end = 16.dp, bottom = 118.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { FitScreenHeader(title = greeting, subtitle = dateText) }

            val snapshot = state.snapshot
            when {
                state.isLoading -> item {
                    Box(Modifier.fillMaxWidth().padding(top = 48.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = FitAccentRed)
                    }
                }
                snapshot == null -> item {
                    Text(
                        text = state.errorMessage ?: "Не удалось загрузить данные.",
                        color = FitMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 32.dp)
                    )
                }
                else -> {
                    item { WeekCard(snapshot) }
                    item {
                        HeroCard(
                            unfinished = snapshot.unfinished,
                            suggested = snapshot.suggested,
                            onStartWorkout = onStartWorkout,
                            onOpenProgram = onOpenProgram,
                            onOpenPrograms = onOpenPrograms,
                            onCreateWorkout = onCreateWorkout
                        )
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            QuickAction(
                                title = "Программы",
                                icon = Icons.AutoMirrored.Outlined.Assignment,
                                onClick = onOpenPrograms,
                                modifier = Modifier.weight(1f)
                            )
                            QuickAction(
                                title = "Мои тренировки",
                                icon = Icons.Outlined.Person,
                                onClick = onOpenMyWorkouts,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    item { RecoveryCard(snapshot.recovery, snapshot.muscleNames) }
                    snapshot.lastWorkout?.let { last ->
                        item { LastWorkoutCard(last, onClick = { onOpenLog(last.logId) }) }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeekCard(snapshot: TodaySnapshot) {
    FitSurfaceCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Эта неделя",
                color = FitInk,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${snapshot.workoutsThisWeek} из ${snapshot.weeklyGoal}",
                color = if (snapshot.workoutsThisWeek >= snapshot.weeklyGoal) FitAccentTeal else FitMuted,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            snapshot.week.forEach { day -> WeekDayDot(day) }
        }
        if (snapshot.volumeThisWeek > 0 || snapshot.volumeLastWeek > 0) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = weekVolumeText(snapshot.volumeThisWeek, snapshot.volumeLastWeek),
                color = FitMutedLight,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun WeekDayDot(day: WeekDayStatus) {
    val label = day.date.dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, ruLocale)
        .replaceFirstChar { it.titlecase(ruLocale) }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            color = if (day.isToday) FitInk else FitMutedLight,
            fontSize = 12.sp,
            fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Normal
        )
        Spacer(Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(if (day.trained) FitAccentRed else Color(0xFF1E232C), CircleShape)
                .border(
                    width = if (day.isToday) 2.dp else 1.dp,
                    color = if (day.isToday) FitAccentRed else FitCardBorder,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (day.trained) {
                Icon(Icons.Outlined.Check, contentDescription = "Тренировка", tint = Color.White, modifier = Modifier.size(18.dp))
            } else {
                Text(text = "${day.date.dayOfMonth}", color = FitMutedLight, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun HeroCard(
    unfinished: UnfinishedSession?,
    suggested: SuggestedWorkout?,
    onStartWorkout: (Long) -> Unit,
    onOpenProgram: (Long) -> Unit,
    onOpenPrograms: () -> Unit,
    onCreateWorkout: () -> Unit
) {
    FitBrushedSteelCard(contentPadding = PaddingValues(18.dp)) {
        when {
            unfinished != null -> {
                HeroOverline("Незавершённая тренировка")
                HeroTitle(unfinished.workoutName)
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Начата ${formatDateTime(unfinished.startedAt)} · выполнено " +
                        "${unfinished.doneSets} из ${unfinished.totalSets} подходов",
                    color = SteelMuted,
                    fontSize = 14.sp
                )
                if (unfinished.totalSets > 0) {
                    Spacer(Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { unfinished.doneSets.toFloat() / unfinished.totalSets },
                        modifier = Modifier.fillMaxWidth().height(6.dp),
                        color = FitAccentRed,
                        trackColor = Color(0xFF5E6573).copy(alpha = 0.35f)
                    )
                }
                Spacer(Modifier.height(16.dp))
                HeroButton("Продолжить", onClick = { onStartWorkout(unfinished.workoutId) })
            }
            suggested != null -> {
                HeroOverline("Следующая тренировка")
                HeroTitle(suggested.name)
                Spacer(Modifier.height(6.dp))
                val muscles = suggested.muscleNames.take(3).joinToString(", ")
                Text(
                    text = listOf("${suggested.exerciseCount} упр.", muscles).filter { it.isNotBlank() }.joinToString(" · "),
                    color = SteelMuted,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = suggested.lastDoneAt?.let { "В прошлый раз: ${formatDate(it)}" } ?: "Ещё не выполнялась",
                    color = SteelMuted,
                    fontSize = 13.sp
                )
                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    HeroButton("Начать", onClick = { onStartWorkout(suggested.workoutId) }, modifier = Modifier.weight(1f))
                    Spacer(Modifier.width(8.dp))
                    TextButton(onClick = { onOpenProgram(suggested.workoutId) }) {
                        Text("Состав", color = SteelInk, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            else -> {
                HeroOverline("С чего начать")
                HeroTitle("Выберите первую тренировку")
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Возьмите готовую программу или соберите свою из каталога.",
                    color = SteelMuted,
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    HeroButton("Программы", onClick = onOpenPrograms, modifier = Modifier.weight(1f))
                    OutlinedButton(
                        onClick = onCreateWorkout,
                        modifier = Modifier.weight(1f).height(48.dp),
                        border = BorderStroke(1.dp, SteelInk.copy(alpha = 0.5f))
                    ) {
                        Text("Создать свою", color = SteelInk, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroOverline(text: String) {
    Text(
        text = text.uppercase(ruLocale),
        color = Color(0xFFB71C1C),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
    )
    Spacer(Modifier.height(4.dp))
}

@Composable
private fun HeroTitle(text: String) {
    Text(
        text = text,
        color = SteelInk,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun HeroButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = FitAccentRed, contentColor = Color.White),
        shape = RoundedCornerShape(14.dp)
    ) {
        Icon(Icons.Outlined.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(6.dp))
        Text(text, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

@Composable
private fun QuickAction(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(56.dp).clickable(onClick = onClick),
        color = Color(0xFF171B21),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, FitCardBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = FitAccentRed, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(10.dp))
            Text(
                text = title,
                color = FitInk,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun RecoveryCard(recovery: MuscleRecovery, muscleNames: Map<String, String>) {
    FitSurfaceCard {
        FitSectionTitle(title = "Нагрузка на мышцы")
        Spacer(Modifier.height(10.dp))
        MuscleLoadMap(
            fresh = recovery.fresh,
            recovering = recovery.recovering,
            modifier = Modifier.fillMaxWidth().height(190.dp)
        )
        Spacer(Modifier.height(10.dp))
        if (recovery.fresh.isEmpty() && recovery.recovering.isEmpty()) {
            Text(
                text = "За последние 3 дня нагрузки не было. Все мышцы отдохнули.",
                color = FitMuted,
                fontSize = 14.sp
            )
        } else {
            RecoveryLegend(FitAccentRed, "Работали недавно", recovery.fresh, muscleNames)
            RecoveryLegend(RecoveringOrange, "Восстанавливаются", recovery.recovering, muscleNames)
        }
    }
}

@Composable
private fun RecoveryLegend(color: Color, label: String, codes: Set<String>, muscleNames: Map<String, String>) {
    if (codes.isEmpty()) return
    Row(modifier = Modifier.padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(10.dp).background(color, CircleShape))
        Spacer(Modifier.width(8.dp))
        Text(
            text = "$label: " + codes.map { muscleNames[it] ?: it }.sorted().joinToString(", "),
            color = FitMuted,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun LastWorkoutCard(last: LastWorkoutSummary, onClick: () -> Unit) {
    FitSurfaceCard(contentPadding = PaddingValues(0.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(text = "Последняя тренировка", color = FitMutedLight, fontSize = 13.sp)
                Spacer(Modifier.height(2.dp))
                Text(
                    text = last.workoutName,
                    color = FitInk,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = listOf(
                        formatDate(last.finishedAt),
                        "${last.durationMin} мин",
                        "${last.completedSets} подх.",
                        "${formatNumber(last.totalVolume)} кг"
                    ).joinToString(" · "),
                    color = FitMuted,
                    fontSize = 13.sp
                )
            }
            Icon(Icons.Outlined.ChevronRight, contentDescription = "Открыть", tint = FitMutedLight)
        }
    }
}

private fun greetingAndDate(): Pair<String, String> {
    val now = LocalDateTime.now()
    val greeting = when (now.hour) {
        in 5..11 -> "Доброе утро"
        in 12..17 -> "Добрый день"
        in 18..22 -> "Добрый вечер"
        else -> "Доброй ночи"
    }
    val date = now.format(DateTimeFormatter.ofPattern("EEEE, d MMMM", ruLocale))
        .replaceFirstChar { it.titlecase(ruLocale) }
    return greeting to date
}

private fun weekVolumeText(thisWeek: Double, lastWeek: Double): String {
    val base = "Объём: ${formatNumber(thisWeek)} кг"
    if (lastWeek <= 0) return base
    val change = ((thisWeek - lastWeek) / lastWeek * 100).roundToInt()
    val sign = if (change > 0) "+" else ""
    return "$base · $sign$change% к прошлой неделе"
}

private fun formatDate(millis: Long): String =
    SimpleDateFormat("d MMM", ruLocale).format(Date(millis))

private fun formatDateTime(millis: Long): String =
    SimpleDateFormat("d MMM, HH:mm", ruLocale).format(Date(millis))

private fun formatNumber(value: Double): String {
    val raw = value.roundToInt().toString()
    return raw.reversed().chunked(3).joinToString(" ").reversed()
}
