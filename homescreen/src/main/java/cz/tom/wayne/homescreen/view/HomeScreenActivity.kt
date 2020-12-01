package cz.tom.wayne.homescreen.view

import cz.tom.wayne.architecture.BaseActivity
import cz.tom.wayne.core.navigation.Navigator
import cz.tom.wayne.homescreen.R
import cz.tom.wayne.homescreen.viewmodel.HomeScreenViewModel
import kotlinx.android.synthetic.main.activity_home_screen.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.getViewModel
import timber.log.Timber

class HomeScreenActivity : BaseActivity<HomeScreenViewModel>() {

    override val layoutId = R.layout.activity_home_screen

    override val navigator: Navigator by inject()

    override fun initViewModel() {
        viewModel = getViewModel()
    }

    override fun initUi() {
        vBottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_current -> viewModel.showCurrentDogClicked()
                R.id.action_cached -> viewModel.showCachedDogsClicked()
            }
            Timber.d("Menu item with id ${it.itemId} selected")
            true
        }
    }
}
