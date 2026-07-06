package com.example.fitapp.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.fitapp.ui.builder.ExercisePickerScreen
import com.example.fitapp.ui.builder.MyWorkoutsScreen
import com.example.fitapp.ui.builder.WorkoutEditorScreen
import com.example.fitapp.ui.builder.WorkoutEditorViewModel
import com.example.fitapp.ui.catalog.CatalogScreen
import com.example.fitapp.ui.catalog.ExerciseDetailScreen
import com.example.fitapp.ui.journal.JournalScreen
import com.example.fitapp.ui.journal.LogDetailScreen
import com.example.fitapp.ui.programs.ProgramDetailScreen
import com.example.fitapp.ui.programs.ProgramsScreen
import com.example.fitapp.ui.progress.ProgressScreen
import com.example.fitapp.ui.session.ActiveWorkoutScreen

@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Destinations.CATALOG,
        modifier = modifier
    ) {

        // ===== Каталог упражнений =====
        composable(Destinations.CATALOG) {
            CatalogScreen(
                onExerciseClick = { id -> navController.navigate(Destinations.exerciseDetail(id)) }
            )
        }

        // ===== Детали упражнения =====
        composable(
            route = Destinations.EXERCISE_DETAIL,
            arguments = listOf(navArgument("exerciseId") { type = NavType.LongType })
        ) {
            ExerciseDetailScreen(onBackClick = { navController.popBackStack() })
        }

        // ===== Мои тренировки =====
        composable(Destinations.MY_WORKOUTS) {
            MyWorkoutsScreen(
                onCreateWorkout = {
                    navController.navigate(Destinations.workoutEditor(-1L))
                },
                onEditWorkout = { id ->
                    navController.navigate(Destinations.workoutEditor(id))
                },
                onStartWorkout = { id ->
                    navController.navigate(Destinations.activeWorkout(id))
                }
            )
        }

        // ===== Редактор тренировки =====
        composable(
            route = Destinations.WORKOUT_EDITOR,
            arguments = listOf(navArgument("workoutId") { type = NavType.LongType })
        ) {
            WorkoutEditorScreen(
                onBack = { navController.popBackStack() },
                onSaved = {
                    navController.navigate(Destinations.MY_WORKOUTS) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onAddExercise = {
                    navController.navigate(Destinations.EXERCISE_PICKER)
                }
            )
        }

        // ===== Выбор упражнений (пикер) =====
        composable(Destinations.EXERCISE_PICKER) {
            val editorBackStackEntry = navController.previousBackStackEntry
            val editorViewModel: WorkoutEditorViewModel? = editorBackStackEntry?.let {
                hiltViewModel(it)
            }
            ExercisePickerScreen(
                onBack = { navController.popBackStack() },
                onConfirmSelection = { ids ->
                    if (editorViewModel != null) {
                        editorViewModel.addPickedExerciseIds(ids)
                    } else {
                    // Передаём результат в SavedStateHandle редактора (предыдущий экран).
                    editorBackStackEntry
                        ?.savedStateHandle
                        ?.set(WorkoutEditorViewModel.KEY_PICKED_IDS, ids.toLongArray())
                    }
                    navController.popBackStack()
                }
            )
        }

        // ===== Готовые программы =====
        composable(Destinations.PROGRAMS) {
            ProgramsScreen(
                onProgramClick = { id -> navController.navigate(Destinations.programDetail(id)) },
                onStartProgram = { id -> navController.navigate(Destinations.activeWorkout(id)) },
                onMyWorkoutsClick = { navController.navigate(Destinations.MY_WORKOUTS) }
            )
        }

        // ===== Детали программы =====
        composable(
            route = Destinations.PROGRAM_DETAIL,
            arguments = listOf(navArgument("workoutId") { type = NavType.LongType })
        ) {
            ProgramDetailScreen(
                onBack = { navController.popBackStack() },
                onStartWorkout = { id -> navController.navigate(Destinations.activeWorkout(id)) },
                onExerciseClick = { id -> navController.navigate(Destinations.exerciseDetail(id)) }
            )
        }

        // ===== Активная тренировка =====
        composable(
            route = Destinations.ACTIVE_WORKOUT,
            arguments = listOf(navArgument("workoutId") { type = NavType.LongType })
        ) {
            ActiveWorkoutScreen(
                onBack = { navController.popBackStack() },
                onFinish = {
                    navController.navigate(Destinations.JOURNAL) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        // ===== Журнал тренировок =====
        composable(Destinations.JOURNAL) {
            JournalScreen(
                onEntryClick = { logId -> navController.navigate(Destinations.logDetail(logId)) }
            )
        }

        // ===== Детали выполненной тренировки =====
        composable(
            route = Destinations.LOG_DETAIL,
            arguments = listOf(navArgument("logId") { type = NavType.LongType })
        ) {
            LogDetailScreen(onBack = { navController.popBackStack() })
        }

        // ===== Прогресс =====
        composable(Destinations.PROGRESS) {
            ProgressScreen(
                onEntryClick = { logId -> navController.navigate(Destinations.logDetail(logId)) }
            )
        }
    }
}

@Composable
private fun PlaceholderRoute(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            "«$title» — раздел в разработке",
            style = MaterialTheme.typography.titleMedium
        )
    }
}
