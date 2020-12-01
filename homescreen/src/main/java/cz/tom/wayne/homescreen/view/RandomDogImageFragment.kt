package cz.tom.wayne.homescreen.view

import cz.tom.wayne.architecture.BaseViewModelFragment
import cz.tom.wayne.core.extensions.observeNonNull
import cz.tom.wayne.extension.load
import cz.tom.wayne.extension.onClick
import cz.tom.wayne.extension.toast
import cz.tom.wayne.homescreen.viewmodel.HomeScreenViewModel
import cz.tom.wayne.homescreen.R
import kotlinx.android.synthetic.main.fragment_random_dog_image.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.getSharedViewModel
import timber.log.Timber

@ExperimentalCoroutinesApi
class RandomDogImageFragment: BaseViewModelFragment<HomeScreenViewModel>() {

    override val layoutId = R.layout.fragment_random_dog_image

    override fun initViewModel(): HomeScreenViewModel = getSharedViewModel()

    override fun initUi() {
        setupButton()
        observeVM()
    }

    private fun observeVM() {
        viewModel.showNoConnectionEvent.observe(this) {
            toast(getString(R.string.check_connection))
        }

        viewModel.currentDogImage.observeNonNull(this) {
            Timber.d("Last dog image loading")
            vImage.load(it.url)
        }
    }

    private fun setupButton() {
        vButton.onClick {
            viewModel.getAnotherDogPictureClicked()
        }
    }
}