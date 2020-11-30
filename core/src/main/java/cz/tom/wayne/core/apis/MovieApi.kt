package cz.tom.wayne.core.apis

/**
 * API for everything related to campaign creation
 */
interface MovieApi {

    /**
     * Fetch all movies currently played in Cinema
     */
    fun fetchMoviesInCinema(name: String)
}
