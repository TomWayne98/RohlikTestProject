package cz.tom.wayne.homescreen.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import cz.tom.wayne.architecture.BaseViewModel
import cz.tom.wayne.architecture.SingleLiveEvent
import cz.tom.wayne.core.data.DogImageEntity
import cz.tom.wayne.core.extensions.collectIfNotCollecting
import cz.tom.wayne.core.extensions.isNetworkAvailable
import cz.tom.wayne.core.repositories.DogRepo
import cz.tom.wayne.homescreen.HomeScreenFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

/**
 * ViewModel for all HomeScreenActivity fragments
 */
@ExperimentalCoroutinesApi
class HomeScreenViewModel(private val context: Context, private val dogRepo: DogRepo, private val flow: HomeScreenFlow) :
    BaseViewModel() {

    val currentDogImage = MutableLiveData<DogImageEntity>()
    val allDogsImages = MutableLiveData<List<DogImageEntity>>()

    val showNoConnectionEvent = SingleLiveEvent<Boolean>()

    init {
        launch {
            dogRepo.getLastCachedDog().collectIfNotCollecting(TAG_LAST_DOG) {
                currentDogImage.postValue(it)
            }
        }
        launch {
            dogRepo.getAllDogs().collectIfNotCollecting(TAG_ALL_DOGS) {
                allDogsImages.postValue(it)
            }
        }
    }

    fun showCachedDogsClicked() {
        flow.showCachedDogs()
    }

    fun showCurrentDogClicked() {
        flow.showCurrentDog()
    }

    fun getAnotherDogPictureClicked() {
        if (context.isNetworkAvailable()) {
            launch {
                dogRepo.refreshRandomImage()
            }
        } else {
            showNoConnectionEvent.call()
        }
    }

    companion object {
        const val TAG_LAST_DOG = "LAST_DOG"
        const val TAG_ALL_DOGS = "ALL_DOGS"
    }
}