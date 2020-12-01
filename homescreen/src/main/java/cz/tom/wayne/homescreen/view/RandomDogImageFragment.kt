package cz.tom.wayne.homescreen.view

import cz.tom.wayne.architecture.BaseViewModelFragment
import cz.tom.wayne.core.extensions.bobserve
import cz.tom.wayne.core.extensions.observeNonNull
import cz.tom.wayne.extension.load
import cz.tom.wayne.extension.onClick
import cz.tom.wayne.homescreen.viewmodel.HomeScreenViewModel
import cz.tom.wayne.homescreen.R
import kotlinx.android.synthetic.main.fragment_random_dog_image.*
import org.koin.androidx.viewmodel.ext.android.getSharedViewModel

class RandomDogImageFragment: BaseViewModelFragment<HomeScreenViewModel>() {

    override val layoutId = R.layout.fragment_random_dog_image

    override fun initViewModel(): HomeScreenViewModel = getSharedViewModel()

    override fun initUi() {
        vButton.onClick { viewModel.getAnotherDogPicture() }

        viewModel.currentDogImage.observeNonNull(this) {
            vImage.load(it.url)
        }
    }
}