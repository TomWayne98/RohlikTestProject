package cz.tom.wayne.core.navigation


abstract class HomeScreenNavigator {

    /**
     * Show list of all cached dogs
     */
    abstract fun showCachedDogs()

    /**
     * Show currently fetched dog
     */
    abstract fun showCurrentDog()
}