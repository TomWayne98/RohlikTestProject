package cz.tom.wayne.homescreen

import cz.tom.wayne.core.navigation.Flow
import cz.tom.wayne.core.navigation.Navigator

/**
 * List all posible destination changes in HomeScreenActivity
 */
class HomeScreenFlow(navigator: Navigator) : Flow(navigator) {

    /**
     * Show current dog picture
     */
    fun showCurrentDog() {
        navigator.homeScreenNavigator.showCurrentDog()
    }

    /**
     * Show list of cached dogs
     */
    fun showCachedDogs() {
        navigator.homeScreenNavigator.showCachedDogs()
    }
}