package cz.tom.wayne.homescreen.view

import cz.tom.wayne.architecture.BaseViewModelFragment
import cz.tom.wayne.core.Constants
import cz.tom.wayne.core.extensions.observeNonNull
import cz.tom.wayne.extension.dpToPxInt
import cz.tom.wayne.extension.setup
import cz.tom.wayne.homescreen.R
import cz.tom.wayne.homescreen.binders.CachedDogBinder
import cz.tom.wayne.homescreen.viewmodel.HomeScreenViewModel
import cz.tom.wayne.ui.adapters.SimpleRecyclerAdapter
import io.fireball.fireball.fireui.views.generic.MarginItemDecoration
import kotlinx.android.synthetic.main.fragment_cached_dogs.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.getSharedViewModel
import timber.log.Timber

@ExperimentalCoroutinesApi
class CachedDogsFragment : BaseViewModelFragment<HomeScreenViewModel>() {

    override val layoutId = R.layout.fragment_cached_dogs

    override fun initViewModel(): HomeScreenViewModel = getSharedViewModel()

    override fun initUi() {
        Timber.d("Cached dogs fragment")
        setupRecycler()
    }

    private fun setupRecycler() {
        vRecycler.setup(this, CachedDogBinder(), viewModel.allDogsImages, R.layout.item_dog)
    }
}