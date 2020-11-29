package cz.tom.wayne.navigation

import android.app.Activity
import java.util.*


/**
 * This class is responsible for all the navigation action (activity/fragment) in the app.
 * For better organization: For each flow (such as onboarding), introduce a new abstract class in
 * subnavigators package & an abstract val of the new class type and contain all the functions related to that flow
 * inside the abstract class.
 */
@Suppress("TooManyFunctions")
abstract class Navigator {

    abstract val mainNavigator: MainNavigator

    /**
     * This has to be called in onResume from each activity in order to connect the activity to the Navigator.
     */
    abstract fun setLiveActivity(act: Activity, uuid: UUID, hasNavController: Boolean = true)

    /**
     * Clear the [act] reference from the Navigator.
     * Note that this only clears data related to [act].
     * If there is another activity already running, this does not clear it to prevent accidentally clearing a running activity.
     */
    abstract fun clearLiveActivity(act: Activity, uuid: UUID)

    /**
     * Finishes the current live activity.
     */
    abstract fun finishLiveActivity()

    /**
     * Navigates back to the previous destination.
     * Define custom back behaviour in this method.
     */
    abstract fun navigateBack()

    /**
     * Finishes all activities in the stack.
     */
    abstract fun endApp()

    companion object {
        // global keys (define flow-specific keys in subnavigators):
        const val NAVGRAPH_MODE_KEY = "NAVGRAPH_MODE"
        const val SELECTED_CAMPAIGN_ID = "SELECTED_CAMPAIGN_ID"
        const val IS_TRUE = "true"
        const val IS_FALSE = "bullshit"
    }
}
