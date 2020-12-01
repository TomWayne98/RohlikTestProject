package cz.tom.wayne.homescreen.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import cz.tom.wayne.architecture.BaseViewModel
import cz.tom.wayne.architecture.SingleLiveEvent
import cz.tom.wayne.core.data.DogImageEntity
import cz.tom.wayne.core.extensions.collectIfNotCollecting
import cz.tom.wayne.core.extensions.isNetworkAvailable
import cz.tom.wayne.core.navigation.MainNavigator
import cz.tom.wayne.core.repositories.DogRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class HomeScreenViewModel(private val context: Context, private val dogRepo: DogRepo, private val mainNavigator: MainNavigator) :
    BaseViewModel() {

    val currentDogImage = MutableLiveData<DogImageEntity>()

    val showNoConnectionEvent = SingleLiveEvent<Boolean>()

    init {
        launch {
            dogRepo.getLastCachedDog().collectIfNotCollecting("LAST_DOG") {
                currentDogImage.postValue(it)
            }
        }
    }

    fun showCachedDogsClicked() {
        mainNavigator.showCachedDogs()
    }

    fun showCurrentDogClicked() {
        mainNavigator.showCurrentDog()
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
}