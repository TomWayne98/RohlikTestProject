package cz.tom.wayne.navigator

import android.app.Activity
import android.content.Intent
import androidx.navigation.NavController
import androidx.navigation.findNavController
import cz.tom.wayne.R
import cz.tom.wayne.core.navigation.MainNavigator
import cz.tom.wayne.core.navigation.Navigator
import java.util.*
import kotlin.system.exitProcess


@Suppress("TooManyFunctions")
class NavigatorImpl : Navigator() {

    override val mainNavigator: MainNavigator = MainNavigatorImpl()

    private var liveActivity: Activity? = null
    private var navController: NavController? = null
    private var currentLiveActivityUUID: UUID? = null

    private val fragmentsWithNoBackNavigation = emptyList<Int>(
        // All fragments that don't support back navigation (back button does nothing) should be listed here
    )

    override fun setLiveActivity(act: Activity, uuid: UUID, hasNavController: Boolean) {
        currentLiveActivityUUID = uuid
        liveActivity = act
        navController =
            if (hasNavController) liveActivity?.findNavController(R.id.vNavHostFragment) else null
    }

    override fun clearLiveActivity(act: Activity, uuid: UUID) {
        // only clear liveActivity if the one we want to clear is the current liveActivity - to prevent clearing an activity that is running
        if (currentLiveActivityUUID == uuid) {
            currentLiveActivityUUID = null
            liveActivity = null
            navController = null
        }
    }

    override fun finishLiveActivity() {
        liveActivity?.finish()
    }

    override fun navigateBack() {
        // If the current fragment doesn't support back navigation, no need to do anything.
        // If it does, try to pop the back stack and we are done if it was popped.
        // If there was nothing to pop (because the curr. frag. was the last in the stack), try to finish the flow activity if it is not null.
        navController?.let {
            return@let if (fragmentsWithNoBackNavigation.contains(it.currentDestination?.id) || it.popBackStack()) it else null
        } ?: liveActivity?.also {
            it.finish()
        }
    }

    override fun endApp() {
        liveActivity?.let {
            it.finishAffinity()
            // we need to exit the app process when logging out to reset the whole app state before a new user logs in
            // (if we don't do this, all static variables, objects, repos, etc. remain initialized, resulting in a corrupted state for the new user)
            exitProcess(0)
        }
    }

    private inline fun <reified T : Activity> switchActivity(
        extras: List<Pair<String, String?>>? = null,
        finishPrevious: Boolean = false
    ) {
        liveActivity?.run {
            val intent = Intent(this, T::class.java)
            extras?.forEach { intent.putExtra(it.first, it.second) }
            startActivity(intent)
            if (finishPrevious) finish()
        }
    }

    inner class MainNavigatorImpl : MainNavigator() {
        override fun showCachedDogs() {
            navController?.navigate(R.id.action_random_dog_to_cached_dogs)
        }

        override fun showCurrentDog() {
            navController?.navigate(R.id.action_cached_dogs_to_random_dog)

        }
    }
}
