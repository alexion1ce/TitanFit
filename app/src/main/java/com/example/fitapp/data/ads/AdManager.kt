package com.example.fitapp.data.ads

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

enum class AdProvider {
    ADMOB,
    YANDEX
}

data class AdState(
    val isAdsEnabled: Boolean = true,
    val adProvider: AdProvider = AdProvider.ADMOB,
    val bannerAdUnitId: String = "ca-app-pub-3940256099942544/6300978111", // AdMob Test Banner
    val interstitialAdUnitId: String = "ca-app-pub-3940256099942544/1033173712", // AdMob Test Interstitial
    val rewardedAdUnitId: String = "ca-app-pub-3940256099942544/5224354917", // AdMob Test Rewarded
    val isInterstitialShowing: Boolean = false,
    val isRewardedUnlocked: Boolean = false,
    val completedWorkoutsCount: Int = 0
)

@Singleton
class AdManager @Inject constructor() {

    private val _adState = MutableStateFlow(AdState())
    val adState: StateFlow<AdState> = _adState.asStateFlow()

    fun toggleAds(enabled: Boolean) {
        _adState.value = _adState.value.copy(isAdsEnabled = enabled)
    }

    fun setProvider(provider: AdProvider) {
        _adState.value = _adState.value.copy(adProvider = provider)
    }

    /**
     * Показать межстраничную рекламу после окончания тренировки
     */
    fun showPostWorkoutInterstitial(context: Context, onAdClosed: () -> Unit) {
        if (!_adState.value.isAdsEnabled) {
            onAdClosed()
            return
        }

        val count = _adState.value.completedWorkoutsCount + 1
        _adState.value = _adState.value.copy(
            completedWorkoutsCount = count,
            isInterstitialShowing = true
        )

        // Эмуляция/загрузка показа объявлений
        onAdClosed()
        _adState.value = _adState.value.copy(isInterstitialShowing = false)
    }

    /**
     * Показать вознаграждаемую рекламу для бонуса
     */
    fun showRewardedAd(context: Context, onRewardEarned: () -> Unit) {
        if (!_adState.value.isAdsEnabled) {
            onRewardEarned()
            return
        }

        _adState.value = _adState.value.copy(isRewardedUnlocked = true)
        onRewardEarned()
    }
}
