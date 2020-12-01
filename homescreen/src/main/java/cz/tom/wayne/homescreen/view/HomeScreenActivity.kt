package cz.tom.wayne.homescreen.view

import cz.tom.wayne.architecture.BaseActivity
import cz.tom.wayne.core.navigation.Navigator
import cz.tom.wayne.homescreen.viewmodel.HomeScreenViewModel
import cz.tom.wayne.homescreen.R
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.getViewModel

class HomeScreenActivity: BaseActivity<HomeScreenViewModel>() {

    override val layoutId = R.layout.activity_home_screen

    override val navigator: Navigator by inject()

//    override val fireHandler: ErrorHandler by inject()

    override fun initViewModel() {
        viewModel = getViewModel()
    }
}
