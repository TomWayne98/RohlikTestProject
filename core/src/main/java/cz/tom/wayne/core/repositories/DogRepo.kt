package cz.tom.wayne.core.repositories

import androidx.lifecycle.LiveData
import cz.tom.wayne.core.data.DogImageEntity
import kotlinx.coroutines.flow.Flow

interface DogRepo {

    /**
     * Download another dog image
     */
    fun refreshRandomImage()

    /**
     * Get the last downloaded image
     */
    suspend fun getLastCachedDog(): Flow<DogImageEntity>
}