package cz.tom.wayne.homescreen.view

import cz.tom.wayne.architecture.BaseViewModelFragment
import cz.tom.wayne.homescreen.viewmodel.HomeScreenViewModel
import cz.tom.wayne.homescreen.R
import org.koin.androidx.viewmodel.ext.android.getSharedViewModel

class CachedDogsFragment: BaseViewModelFragment<HomeScreenViewModel>() {

    override val layoutId = R.layout.fragment_cached_dogs

    override fun initViewModel(): HomeScreenViewModel = getSharedViewModel()
}