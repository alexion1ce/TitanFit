package com.example.fitapp.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.fitapp.R

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow

@Composable
fun ExerciseArtworkThumbnail(
    exerciseCode: String,
    primaryMuscleCode: String,
    secondaryMuscleCode: String?,
    modifier: Modifier = Modifier
) {
    val imageRes = exerciseArtworkResId(exerciseCode, primaryMuscleCode)
    if (imageRes != null) {
        Surface(
            modifier = modifier,
            color = Color.White,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFF2B3038).copy(alpha = 0.5f))
        ) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(3.dp),
                contentScale = ContentScale.Fit
            )
        }
    } else {
        MissingArtwork(modifier)
    }
}

@Composable
fun ExerciseArtworkHero(
    exerciseCode: String,
    primaryMuscleCode: String,
    secondaryMuscleCode: String?,
    exerciseName: String,
    modifier: Modifier = Modifier
) {
    val imageRes = exerciseArtworkResId(exerciseCode, primaryMuscleCode)
    var showFullImage by remember { mutableStateOf(false) }

    if (imageRes != null) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f)
                .clickable { showFullImage = true },
            color = Color.White,
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.dp, Color(0xFF2B3038))
        ) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = exerciseName,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentScale = ContentScale.Fit
            )
        }
        if (showFullImage) {
            FullScreenArtworkDialog(
                imageRes = imageRes,
                exerciseName = exerciseName,
                onDismiss = { showFullImage = false }
            )
        }
    } else {
        MissingArtwork(
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MuscleColorLegend(modifier: Modifier = Modifier) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        LegendItem(Color(0xFFE53935), "основная")
        LegendItem(Color(0xFFFF8A3D), "помогает")
        LegendItem(Color(0xFF00D2FF), "стабилизирует")
    }
}


@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.size(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun FullScreenArtworkDialog(
    @DrawableRes imageRes: Int,
    exerciseName: String,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black
        ) {
            Box {
                Image(
                    painter = painterResource(imageRes),
                    contentDescription = exerciseName,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f), CircleShape)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Закрыть")
                }
            }
        }
    }
}

@Composable
private fun MissingArtwork(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.clip(RoundedCornerShape(12.dp)),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "Иллюстрация готовится",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@DrawableRes
private fun exerciseArtworkResId(exerciseCode: String, primaryMuscleCode: String): Int? =
    when (exerciseCode) {
        "pushup" -> R.drawable.exercise_pushup
        "bench_press" -> R.drawable.exercise_chest_press
        "dumbbell_bench_press" -> R.drawable.exercise_dumbbell_bench_press
        "machine_chest_press" -> R.drawable.exercise_machine_chest_press
        "incline_barbell_press" -> R.drawable.exercise_incline_barbell_press
        "decline_bench_press" -> R.drawable.exercise_chest_press
        "incline_dumbbell_press" -> R.drawable.exercise_incline_dumbbell_press
        "cable_fly" -> R.drawable.exercise_cable_fly
        "pec_deck" -> R.drawable.exercise_pec_deck
        "dumbbell_fly" -> R.drawable.exercise_dumbbell_fly
        "pullup" -> R.drawable.exercise_pullup
        "deadlift" -> R.drawable.exercise_deadlift
        "sumo_deadlift" -> R.drawable.exercise_sumo_deadlift
        "lat_pulldown" -> R.drawable.exercise_back_pull
        "seated_cable_row" -> R.drawable.exercise_seated_cable_row
        "machine_row" -> R.drawable.exercise_machine_row
        "straight_arm_pulldown" -> R.drawable.exercise_straight_arm_pulldown
        "barbell_row" -> R.drawable.exercise_barbell_row
        "one_arm_dumbbell_row" -> R.drawable.exercise_one_arm_dumbbell_row
        "t_bar_row" -> R.drawable.exercise_t_bar_row
        "back_extension" -> R.drawable.exercise_back_extension
        "lunge", "walking_lunge" -> R.drawable.exercise_lunge
        "bulgarian_split_squat" -> R.drawable.exercise_bulgarian_split_squat
        "squat" -> R.drawable.exercise_legs_squat
        "goblet_squat" -> R.drawable.exercise_goblet_squat
        "front_squat" -> R.drawable.exercise_front_squat
        "hack_squat" -> R.drawable.exercise_hack_squat
        "smith_squat" -> R.drawable.exercise_smith_squat
        "leg_press" -> R.drawable.exercise_leg_press
        "leg_extension" -> R.drawable.exercise_leg_extension
        "romanian_deadlift" -> R.drawable.exercise_romanian_deadlift
        "leg_curl" -> R.drawable.exercise_leg_curl
        "calf_raise", "standing_calf_raise_machine" -> R.drawable.exercise_calf_raise
        "seated_calf_raise" -> R.drawable.exercise_seated_calf_raise
        "overhead_press", "front_raise" -> R.drawable.exercise_shoulders_press
        "machine_shoulder_press" -> R.drawable.exercise_machine_shoulder_press
        "arnold_press" -> R.drawable.exercise_arnold_press
        "seated_dumbbell_press" -> R.drawable.exercise_seated_dumbbell_press
        "lateral_raise" -> R.drawable.exercise_lateral_raise
        "face_pull" -> R.drawable.exercise_face_pull
        "rear_delt_fly" -> R.drawable.exercise_rear_delt_fly
        "reverse_pec_deck" -> R.drawable.exercise_reverse_pec_deck
        "barbell_curl" -> R.drawable.exercise_barbell_curl
        "preacher_curl" -> R.drawable.exercise_preacher_curl
        "dumbbell_curl" -> R.drawable.exercise_biceps_curl
        "concentration_curl" -> R.drawable.exercise_concentration_curl
        "incline_dumbbell_curl" -> R.drawable.exercise_incline_dumbbell_curl
        "cable_curl" -> R.drawable.exercise_cable_curl
        "hammer_curl" -> R.drawable.exercise_hammer_curl
        "triceps_pushdown" -> R.drawable.exercise_triceps_pushdown
        "rope_pushdown" -> R.drawable.exercise_rope_pushdown
        "skullcrusher" -> R.drawable.exercise_skullcrusher
        "overhead_triceps_extension" -> R.drawable.exercise_overhead_triceps_extension
        "dips" -> R.drawable.exercise_dips
        "close_grip_bench_press" -> R.drawable.exercise_close_grip_bench_press
        "bench_dips" -> R.drawable.exercise_bench_dips
        "plank" -> R.drawable.exercise_plank
        "side_plank" -> R.drawable.exercise_side_plank
        "ab_wheel_rollout" -> R.drawable.exercise_ab_wheel_rollout
        "crunch" -> R.drawable.exercise_abs_crunch
        "cable_crunch" -> R.drawable.exercise_cable_crunch
        "russian_twist" -> R.drawable.exercise_russian_twist
        "leg_raise" -> R.drawable.exercise_leg_raise
        "hanging_leg_raise" -> R.drawable.exercise_hanging_leg_raise
        "hip_thrust" -> R.drawable.exercise_hip_thrust
        "cable_kickback" -> R.drawable.exercise_cable_kickback
        "hip_abduction_machine" -> R.drawable.exercise_hip_abduction_machine
        "glute_bridge" -> R.drawable.exercise_glute_bridge
        "kettlebell_swing" -> R.drawable.exercise_kettlebell_swing
        else -> when (primaryMuscleCode) {
            "chest" -> R.drawable.exercise_chest_press
            "back" -> R.drawable.exercise_back_pull
            "legs" -> R.drawable.exercise_legs_squat
            "shoulders" -> R.drawable.exercise_shoulders_press
            "biceps" -> R.drawable.exercise_biceps_curl
            "triceps" -> R.drawable.exercise_triceps_dips
            "abs" -> R.drawable.exercise_abs_crunch
            "glutes" -> R.drawable.exercise_glutes_bridge
            else -> null
        }
    }
