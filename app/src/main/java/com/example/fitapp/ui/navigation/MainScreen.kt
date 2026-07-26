package com.example.fitapp.ui.navigation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fitapp.ui.components.FitAccentRed
import com.example.fitapp.ui.components.FitMutedLight
import com.example.fitapp.ui.components.FitNavDark
import com.example.fitapp.ui.components.FitScreenBackground

private data class BottomItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

private val bottomItems = listOf(
    BottomItem(Destinations.CATALOG, "Каталог", Icons.Outlined.FitnessCenter),
    BottomItem(Destinations.PROGRAMS, "Программы", Icons.AutoMirrored.Outlined.Assignment),
    BottomItem(Destinations.PROGRESS, "Прогресс", Icons.Outlined.Analytics),
    BottomItem(Destinations.MY_WORKOUTS, "Мои", Icons.Outlined.Person)
)

@Composable
fun MainScreen(startDestination: String = Destinations.CATALOG) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val showBottomBar = currentRoute in bottomItems.map { it.route }

    Scaffold(
        containerColor = FitScreenBackground,
        bottomBar = {
            if (showBottomBar) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    AppBottomBar(
                        currentRoute = currentRoute,
                        onItemClick = { route ->
                            if (currentRoute != route) {
                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        onCreateClick = {
                            navController.navigate(Destinations.workoutEditor(-1L))
                        }
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            NavGraph(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun AppBottomBar(
    currentRoute: String?,
    onItemClick: (String) -> Unit,
    onCreateClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(118.dp)
            .navigationBarsPadding()
    ) {
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(88.dp),
            color = Color.Transparent,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            border = BorderStroke(
                1.dp,
                Brush.linearGradient(
                    colors = listOf(Color(0xFF353C4A), FitAccentRed.copy(alpha = 0.4f))
                )
            ),
            shadowElevation = 18.dp
        ) {
            Row(
                modifier = Modifier
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF1B202A), Color(0xFF11141A))
                        )
                    )
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                bottomItems.forEachIndexed { index, item ->
                    BottomBarItem(
                        item = item,
                        selected = currentRoute == item.route,
                        onClick = { onItemClick(item.route) },
                        modifier = Modifier.weight(1f)
                    )
                    if (index == 1) {
                        Spacer(modifier = Modifier.size(78.dp))
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(76.dp)
                .shadow(12.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(FitAccentRed, Color(0xFFB71C1C))
                    )
                )
                .border(1.5.dp, Color.White.copy(alpha = 0.35f), CircleShape)
                .clickable(onClick = onCreateClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = "Создать тренировку",
                tint = Color.White,
                modifier = Modifier.size(38.dp)
            )
        }
    }
}

@Composable
private fun BottomBarItem(
    item: BottomItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tint = if (selected) FitAccentRed else FitMutedLight

    Column(
        modifier = modifier
            .height(68.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(top = 7.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            tint = tint,
            modifier = Modifier.size(26.dp)
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = item.label,
            color = tint,
            maxLines = 1,
            overflow = TextOverflow.Clip,
            softWrap = false,
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
