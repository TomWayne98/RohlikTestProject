package cz.tom.wayne.homescreen.view

import cz.tom.wayne.architecture.BaseActivity
import cz.tom.wayne.core.navigation.Navigator
import cz.tom.wayne.homescreen.R
import cz.tom.wayne.homescreen.viewmodel.HomeScreenViewModel
import kotlinx.android.synthetic.main.activity_home_screen.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.getViewModel
import timber.log.Timber

/**
 * Activity containing all screens in the app
 */
class HomeScreenActivity : BaseActivity<HomeScreenViewModel>() {

    override val layoutId = R.layout.activity_home_screen

    override val navigator: Navigator by inject()

    override fun initViewModel() {
        viewModel = getViewModel()
    }

    override fun initUi() {
        var lastSelectedItem = 0
        vBottomNavigation.setOnNavigationItemSelectedListener {
            val itemId = it.itemId
            if (itemId != lastSelectedItem) {
                when (itemId) {
                    R.id.action_current -> viewModel.showCurrentDogClicked()
                    R.id.action_cached -> viewModel.showCachedDogsClicked()
                }
            }
            lastSelectedItem = itemId
            Timber.d("Menu item with id ${it.itemId} selected")
            true
        }
    }
}
