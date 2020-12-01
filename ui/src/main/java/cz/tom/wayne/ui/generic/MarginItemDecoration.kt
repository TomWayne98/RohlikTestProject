package io.fireball.fireball.fireui.views.generic

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Applies margins to items of [RecyclerView]. Meant to be used with recyclers with linear layout managers.
 * Values (all in pixels):
 * [betweenItems]: The margin that should be between individual items in the primary direction - either in vertical or horizontal,
 * depending on [orientationVertical]. Half of the value will be applied on each side of the item.
 * [firstItem]: If not null, overrides the start margin (either top or left, depending on the direction) of the first adapter item.
 * [lastItem]: If not null, overrides the end margin (either bottom or right, depending on the direction) of the last adapter item.
 * [side]: The margin to apply on both sides of every item view in the secondary direction (in vertical orientation, this means left and right,
 * otherwise top and bottom).
 * [orientationVertical]: True if the orientation is vertical, false if it is horizontal.
 */
class MarginItemDecoration(
    private val betweenItems: Int = 0,
    private val firstItem: Int? = null,
    private val lastItem: Int? = null,
    private val side: Int = 0,
    private val orientationVertical: Boolean = true
) : RecyclerView.ItemDecoration() {
    @Suppress("ComplexMethod")
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        with(outRect) {
            val position = parent.getChildAdapterPosition(view)
            val lastItemPos = parent.adapter?.itemCount?.let { it - 1 }
            if (orientationVertical) {
                top = if (position == 0) firstItem ?: 0 else betweenItems / 2
                left = side
                right = side
                bottom = if (position == lastItemPos) lastItem ?: 0 else betweenItems / 2
            } else {
                top = side
                left = if (position == 0) firstItem ?: 0 else betweenItems / 2
                right = if (position == lastItemPos) lastItem ?: 0 else betweenItems / 2
                bottom = side
            }
        }
    }
}
