package com.example.fitapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.fitapp.data.repository.UserProfileRepository
import com.example.fitapp.ui.navigation.Destinations
import com.example.fitapp.ui.navigation.MainScreen
import com.example.fitapp.ui.session.RestTimerNotifications
import com.example.fitapp.ui.theme.FitAppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var userProfileRepository: UserProfileRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RestTimerNotifications.createChannel(this)
        enableEdgeToEdge()

        val startDestination = if (userProfileRepository.isCompleted()) {
            Destinations.CATALOG
        } else {
            Destinations.ONBOARDING
        }

        setContent {
            FitAppTheme {
                MainScreen(startDestination = startDestination)
            }
        }
    }
}
