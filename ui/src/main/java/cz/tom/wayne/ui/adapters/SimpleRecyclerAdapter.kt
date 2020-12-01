package cz.tom.wayne.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

/**
 * Non-databinding version of [SimpleBindingRecyclerAdapter]
 *
 * @author Josef Hruška (pepa.hruska@gmail.com).
 */
open class SimpleRecyclerAdapter<ITEM_DATA>(
    @LayoutRes private val itemLayoutRes: Int,
    private val binder: SimpleBinder<ITEM_DATA>
) : RecyclerView.Adapter<ViewHolder>() {

    // Data relevant to display a single item
    private var itemData: List<ITEM_DATA> = listOf()

    override fun getItemCount() = itemData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        binder.bind(holder.itemView, itemData[position], position, itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(itemLayoutRes, parent, false)
        return ViewHolder(view)
    }

    fun setItems(data: List<ITEM_DATA>) {
        this.itemData = data
        // It would be cool to use DiffUtil here, but it requires a larger refactoring
        // (all equals methods, ids handling) and it's not worth it
        notifyDataSetChanged()
    }
}

abstract class SimpleBinder<in SINGLE_ITEM_DATA> {
    abstract fun bind(view: View, itemData: SINGLE_ITEM_DATA, position: Int, itemCount: Int)
}

class ViewHolder(val root: View) : RecyclerView.ViewHolder(root)
