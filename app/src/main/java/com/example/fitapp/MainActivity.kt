package com.example.fitapp

import android.Manifest
import android.os.Bundle
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
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

    private val requestNotificationsPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RestTimerNotifications.createChannel(this)
        requestNotificationsPermissionIfNeeded()
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

    private fun requestNotificationsPermissionIfNeeded() {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            requestNotificationsPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
