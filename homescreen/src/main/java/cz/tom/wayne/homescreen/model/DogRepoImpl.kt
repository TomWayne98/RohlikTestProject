package cz.tom.wayne.homescreen.model

import cz.tom.wayne.core.apis.DogApi
import cz.tom.wayne.core.daos.DogDAO
import cz.tom.wayne.core.data.DogImageEntity
import cz.tom.wayne.core.data.toDBEntity
import cz.tom.wayne.core.repositories.DogRepo
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class DogRepoImpl(private val dogApi: DogApi, private val dogDAO: DogDAO) : DogRepo {

    override suspend fun refreshRandomImage() {
        val result = dogApi.getDogImage().await()
        Timber.d("Last dog image url downloaded")
        dogDAO.insertDogImage(result.toDBEntity())
    }

    override suspend fun getLastCachedDog() = flow<DogImageEntity> {
        dogDAO.getLastDog().collect {
            Timber.d("Last dog collected: $it")

            // If there is no dog in DB we need to fetch one
            if (it.isNullOrEmpty()) {
                refreshRandomImage()
                Timber.d("Last dog is null or empty")
                // emit(null)
            } else {
                Timber.d("Last dog emitted")
                emit(it.first())
            }
        }
    }

    override suspend fun getAllDogs() = dogDAO.getAllDogImages()

}