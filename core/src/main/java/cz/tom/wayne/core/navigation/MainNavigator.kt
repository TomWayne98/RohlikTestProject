package cz.tom.wayne.core.navigation


abstract class MainNavigator {

    /**
     * Show list of all cached dogs
     */
    abstract fun showCachedDogs()

    /**
     * Show currently fetched dog
     */
    abstract fun showCurrentDog()
}