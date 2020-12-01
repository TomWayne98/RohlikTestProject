package cz.tom.wayne.core.navigation

/**
 * This class should expose only the navigation functions from [Navigator] that are needed in a specific flow, and should be the only way of
 * accessing the [Navigator] a flow activity or ViewModel has.
 */
abstract class Flow(protected val navigator: Navigator) {

    /**
     * Navigates back to the previous screen.
     */
    fun navigateBack() {
        navigator.navigateBack()
    }

    /**
     * Finishes the currently live activity.
     */
    fun finishLiveActivity() {
        navigator.finishLiveActivity()
    }
}
