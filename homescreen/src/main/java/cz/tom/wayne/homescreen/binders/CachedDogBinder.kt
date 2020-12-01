package cz.tom.wayne.homescreen.binders

import android.view.View
import cz.tom.wayne.core.data.DogImageEntity
import cz.tom.wayne.extension.load
import cz.tom.wayne.ui.adapters.SimpleBinder
import kotlinx.android.synthetic.main.item_dog.view.*

/**
 * Binds dog image into the "cached dogs" recycler
 */
class CachedDogBinder() : SimpleBinder<DogImageEntity>() {

    override fun bind(view: View, itemData: DogImageEntity, position: Int, itemCount: Int) {
        view.vDogImage.load(itemData.url)
    }
}
