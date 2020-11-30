package cz.tom.wayne.core.repositories

/**
 * Repository for everything user-related - either to the current user, or to profiles of other users in the game.
 */
interface MoviesRepo {

    /**
     * Return list of movies which are currently in cinema
     */
    fun loadMoviesInCinema()
}
