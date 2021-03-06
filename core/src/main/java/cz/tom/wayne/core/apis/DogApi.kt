package cz.tom.wayne.core.apis

import cz.tom.wayne.core.data.DogImageResponse
import kotlinx.coroutines.Deferred
import retrofit2.http.GET

/**
 * Contains all dog related API calls
 */
interface DogApi {

    /**
     * Returns URL to image of random dog
     */
    @GET("breeds/image/random")
    fun getDogImage(): Deferred<DogImageResponse>
}