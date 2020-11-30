package cz.tom.wayne.core

/**
 * Represents the current foreground state of the app - which screen is in the foreground.
 * Used by notification service to determine if we want to show a specific notification or not.
 */
object ForegroundState {
    var activeScreen: ScreenType = ScreenType.None
        private set

    fun setForegroundScreen(screenType: ScreenType) {
        activeScreen = screenType
    }

    fun clearForegroundScreen() {
        activeScreen = ScreenType.None
    }
}

sealed class ScreenType {
    data class Game(val campaignId: String) : ScreenType()
    data class UserChat(val roomId: String?) : ScreenType()
    object Platform : ScreenType()
    object Other : ScreenType()
    object None : ScreenType()
}
