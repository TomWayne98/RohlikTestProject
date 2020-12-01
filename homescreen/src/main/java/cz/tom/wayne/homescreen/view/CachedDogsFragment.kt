package cz.tom.wayne.homescreen.view

import cz.tom.wayne.architecture.BaseViewModelFragment
import cz.tom.wayne.extension.setup
import cz.tom.wayne.homescreen.R
import cz.tom.wayne.homescreen.binders.CachedDogBinder
import cz.tom.wayne.homescreen.viewmodel.HomeScreenViewModel
import kotlinx.android.synthetic.main.fragment_cached_dogs.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.getSharedViewModel

/**
 * Display list of all cached dogs in recycler view
 */
@ExperimentalCoroutinesApi
class CachedDogsFragment : BaseViewModelFragment<HomeScreenViewModel>() {

    override val layoutId = R.layout.fragment_cached_dogs

    override fun initViewModel(): HomeScreenViewModel = getSharedViewModel()

    override fun initUi() {
        setupRecycler()
    }

    private fun setupRecycler() {
        vRecycler.setup(this, CachedDogBinder(), viewModel.allDogsImages, R.layout.item_dog)
    }
}