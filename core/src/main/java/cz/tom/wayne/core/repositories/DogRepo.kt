package cz.tom.wayne.core.repositories

import cz.tom.wayne.core.data.DogImageEntity
import kotlinx.coroutines.flow.Flow

interface DogRepo {

    /**
     * Download another dog image
     */
    suspend fun refreshRandomImage()

    /**
     * Get the last downloaded image
     */
    suspend fun getLastCachedDog(): Flow<DogImageEntity>

    /**
     * Get all dogs in the database
     */
    suspend fun getAllDogs(): Flow<List<DogImageEntity>?>
}