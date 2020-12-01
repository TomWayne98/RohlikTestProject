package cz.tom.wayne.core.apis

import cz.tom.wayne.core.data.DogImageResponse
import retrofit2.Response

interface DogApi {

    /**
     * Returns URL to image of random dog
     */
    fun getDoggoImage(): Response<DogImageResponse>
}