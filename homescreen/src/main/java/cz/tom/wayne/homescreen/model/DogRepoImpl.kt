package cz.tom.wayne.homescreen.model

import cz.tom.wayne.core.apis.DogApi
import cz.tom.wayne.core.daos.DogDAO
import cz.tom.wayne.core.data.toDBEntity
import cz.tom.wayne.core.repositories.DogRepo
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import timber.log.Timber

/**
 * Contains all methods related to doggy stuff
 */
class DogRepoImpl(private val dogApi: DogApi, private val dogDAO: DogDAO) : DogRepo {

    override suspend fun refreshRandomImage() {
        // Wait for image from Rest API
        val result = dogApi.getDogImage().await()
        // Store the image to DB
        dogDAO.insertDogImage(result.toDBEntity())
    }

    override suspend fun getLastCachedDog() = flow {
        dogDAO.getLastDog().collect {
            // If there is no dog in DB we need to fetch one
            if (it.isNullOrEmpty()) {
                refreshRandomImage()
                Timber.d("Last dog is null or empty")
            } else {
                // If there is a dog display it
                Timber.d("Last dog emitted")
                emit(it.first())
            }
        }
    }

    override suspend fun getAllDogs() = dogDAO.getAllDogImages()

}