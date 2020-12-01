package cz.tom.wayne.homescreen.model

import cz.tom.wayne.core.apis.DogApi
import cz.tom.wayne.core.daos.DogDAO
import cz.tom.wayne.core.data.DogImageEntity
import cz.tom.wayne.core.extensions.collectIfNotCollecting
import cz.tom.wayne.core.repositories.DogRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

class DogRepoImpl(private val dogApi: DogApi, private val dogDAO: DogDAO): DogRepo {

    override fun refreshRandomImage() {
            dogApi.getDoggoImage().body()?.let {
                dogDAO.insertDogImage(it)
            }
    }

    override suspend fun getLastCachedDog() = flow<DogImageEntity> {
        dogDAO.getLastDog().collect {
            // If there is no dog in DB we need to fetch one
            if (it == null) {
                refreshRandomImage()
               // emit(null)
            } else {
                emit(it)
            }
        }
    }
}