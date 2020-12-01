package cz.tom.wayne.homescreen.viewmodel

import androidx.lifecycle.MutableLiveData
import cz.tom.wayne.architecture.BaseViewModel
import cz.tom.wayne.core.data.DogImageEntity
import cz.tom.wayne.core.extensions.collectIfNotCollecting
import cz.tom.wayne.core.repositories.DogRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class HomeScreenViewModel(private val dogRepo: DogRepo) : BaseViewModel() {

    val currentDogImage = MutableLiveData<DogImageEntity>()

    init {
        launch {
            dogRepo.getLastCachedDog().collectIfNotCollecting("LAST_DOG") {
                currentDogImage.postValue(it)
            }
        }
    }

    fun getAnotherDogPicture() {
        dogRepo.refreshRandomImage()
    }

}