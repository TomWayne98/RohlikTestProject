package cz.tom.wayne.architecture

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import java.util.*

@Suppress("TooManyFunctions")
abstract class BaseActivity<T : BaseViewModel> : AppCompatActivity() {

    // This is unique identifier for every instance of activity in app
    // We use it for example for identifiyng specific activity instances in Navigator
    private val uuid: UUID = UUID.randomUUID()

    protected abstract val layoutId: Int

    lateinit var viewModel: T

    abstract val navigator: Navigator
    abstract val fireHandler: FireHandler

    /**
     * True if this activity uses a NavController. If true, the navigator will try to retrieve it.
     */
    open val hasNavController = true

    open val navHostFragmentId: Int? = null

    /**
     * Key of the nav graph to inflate, read from the intent extras, or null if not available.
     */
    protected var navgraphMode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableHardwareAcceleration()
        initViewModel()
        setLayout()
        displayContentUnderStatusBar()
        readIntentArguments()
        // Sometimes we need to set multiple nav graphs for single activity
        ifNotNull(getDynamicNavgraphId(), navHostFragmentId) { navId, hostId ->
            (supportFragmentManager.findFragmentById(hostId) as NavHostFragment).navController.run {
                graph = navInflater.inflate(navId)
            }
        }
        observeBackEventsFromViewModel()
    }

    override fun onStart() {
        super.onStart()
        initUi()
    }

    override fun onResume() {
        super.onResume()
        navigator.setLiveActivity(this, uuid, hasNavController)
        ForegroundState.setForegroundScreen(getScreenType())
    }

    override fun onPause() {
        super.onPause()
        ForegroundState.clearForegroundScreen()
    }

    override fun onBackPressed() {
        navigator.navigateBack()
        viewModel.onBackPressed()
    }

    override fun onDestroy() {
        navigator.clearLiveActivity(this, uuid)
        super.onDestroy()
    }

    abstract fun initViewModel()

    @CallSuper
    open fun initUi() {
        fireHandler.activityRequests.bobserve(this) {
            it.invoke(this)
        }
    }

    /**
     * Some activities may need different NavGraphs for different situations.
     * Determine which nav graph should be inflated in this method.
     * The inflation happens in [onCreate].
     * In order for this to work, you need to also override [navHostFragmentId].
     */
    open fun getDynamicNavgraphId(): Int? = null

    open fun setLayout() {
        setContentView(layoutId)
    }

    /**
     * Return the type of screen that this activity represents. If the screen has an explicit case
     * defined in [ScreenType], override and return it from this function, otherwise keep the default.
     * This is used in notifications to determine which screen is in the foreground to only
     * show notifications related to other screens.
     */
    protected open fun getScreenType(): ScreenType = ScreenType.Other

    /**
     * Use when some config arguments need to be parsed from the intent extras that were passed when starting this
     * activity. This is called in [onCreate], before navgraph inflation.
     * The default implementation parses [navgraphMode].
     */
    @CallSuper
    protected open fun readIntentArguments() {
        navgraphMode = intent.getStringExtra(Navigator.NAVGRAPH_MODE_KEY)
    }

    private fun displayContentUnderStatusBar() {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }

    private fun enableHardwareAcceleration() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )
    }

    /**
     * Starts observing [BaseViewModel.goBackEvent] from the ViewModel.
     * This is used when the ViewModel needs to trigger a back event without the user actually clicking the back button.
     * This just routes the event to [onBackPressed].
     */
    private fun observeBackEventsFromViewModel() {
        viewModel.goBackEvent.bobserve(this) { onBackPressed() }
    }
}
