package io.fireball.fireball.fireui.views.generic

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * A [RecyclerView.LayoutManager] that lays out as many views as possible on a line and breaks to next line if they don't fit, much like the word
 * wrapping feature in text views. Supports vertical scrolling.
 */
@Suppress("TooManyFunctions")
/*TODO: refactor/optimize this. I didn't realize this when making this, but loading all the views from the adapter upon initialization of the RV
   kind of defeats the purpose of using a RECYCLER view in the first place.
   Using this is okay for small lists for now, but on large lists it will have bad performance.*/
class LineWrapLayoutManager : RecyclerView.LayoutManager() {

    private val preparedRows = ArrayList<PreparedRow>()

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams = RecyclerView.LayoutParams(
        RecyclerView.LayoutParams.MATCH_PARENT,
        RecyclerView.LayoutParams.WRAP_CONTENT
    )

    override fun canScrollVertically(): Boolean = true

    /**
     * Needed for wrap content to work.
     */
    override fun isAutoMeasureEnabled(): Boolean = true

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (itemCount <= 0 || state.isPreLayout || !state.didStructureChange()) return
        log("🛏👨‍👦‍👦: Laying out children")
        detachAndScrapAttachedViews(recycler)
        prepareAllRows(recycler)
        var topOffset = 0
        for ((index, row) in preparedRows.withIndex()) {
            layoutRowBelow(row, topOffset, index)
            topOffset += row.rowHeight
        }
    }

    /**
     * This LM doesn't support updating/adding/moving/removing parts of the dataset. Every call like this will trigger re-laying out of the
     * whole dataset.
     */
    override fun onItemsUpdated(recyclerView: RecyclerView, positionStart: Int, itemCount: Int) = onItemsChanged(recyclerView)

    override fun onItemsAdded(recyclerView: RecyclerView, positionStart: Int, itemCount: Int) = onItemsChanged(recyclerView)
    override fun onItemsMoved(recyclerView: RecyclerView, from: Int, to: Int, itemCount: Int) = onItemsChanged(recyclerView)
    override fun onItemsRemoved(recyclerView: RecyclerView, positionStart: Int, itemCount: Int) = onItemsChanged(recyclerView)
    override fun onItemsUpdated(recyclerView: RecyclerView, positionStart: Int, itemCount: Int, payload: Any?) = onItemsChanged(recyclerView)

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        log("📜: scrollVerticallyBy $dy")
        // add new views that need to become visible because of the scroll
        val distanceTravelled = if (dy == 0) 0 else addViewsBasedOnScroll(dy, recycler)
        if (distanceTravelled == 0) return 0

        // does the actual scroll
        offsetChildrenVertical(-distanceTravelled)
        log("📜: Actually scrolled by ${-distanceTravelled}")

        // recycles view that have become invisible because of the scroll
        recycleInvisibleViews(recycler)
        return distanceTravelled
    }

    /**
     * Lays out all views from [row], below the given [topOffset]. If the views are variable in height, aligns their tops no matter the
     * height.
     * [rowIndexForLogging] Index of [row] in [preparedRows], just for logging.
     */
    private fun layoutRowBelow(row: PreparedRow, topOffset: Int, rowIndexForLogging: Int) {
        log("🛏🚣‍: Laying out row with index $rowIndexForLogging below topOffset $topOffset.")
        var laidOutWidth = 0
        for (child in row.preparedChildren) {
            val left = laidOutWidth
            val top = topOffset
            val right = laidOutWidth + child.measuredWidth
            val bottom = top + child.measuredHeight
            addAndLayoutView(child.view, left, top, right, bottom, child.childIndex)
            laidOutWidth += child.measuredWidth
        }
    }

    /**
     * Lays out all views from [row], above the given [bottomOffset]. If the views are variable in height, aligns their tops no matter the
     * height.
     * [rowIndexForLogging] Index of [row] in [preparedRows], just for logging.
     */
    private fun layoutRowAbove(row: PreparedRow, bottomOffset: Int, rowIndexForLogging: Int) {
        log("🛏🚣‍: Laying out row with index $rowIndexForLogging above bottomOffset $bottomOffset.")
        var laidOutWidth = 0
        for (child in row.preparedChildren) {
            val left = laidOutWidth
            val top = bottomOffset - row.rowHeight
            val right = laidOutWidth + child.measuredWidth
            val bottom = top + child.measuredHeight
            addAndLayoutView(child.view, left, top, right, bottom, child.childIndex, false)
            laidOutWidth += child.measuredWidth
        }
    }

    /**
     * Adds and lays out [view] to the parent RV.
     * [left], [top], [right], [bottom]: The coordinates where the view should be laid out.
     * [childIndexForLogging]: Adapter position of [view], just for logging.
     * [directionDown] If true, the view will be added to the end of the parent's views, if false, it will be added to the beginning.
     */
    @Suppress("LongParameterList")
    private fun addAndLayoutView(
        view: View,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
        childIndexForLogging: Int,
        directionDown: Boolean = true
    ) {
        log("🛏🌄: Laying out view with index $childIndexForLogging. left: $left, top: $top, right: $right, bottom: $bottom")
        measureChildWithMargins(view, 0, 0)
        if (directionDown) addView(view) else addView(view, 0)
        layoutDecorated(view, left, top, right, bottom)
    }

    /**
     * Adds new views that need to become visible because of a scroll.
     * @return Returns the distance that was travelled.
     */
    @Suppress("ReturnCount")
    private fun addViewsBasedOnScroll(dy: Int, recycler: RecyclerView.Recycler): Int {
        val layoutDirectionDown = dy > 0
        if (layoutDirectionDown) {
            val lastVisibleChild = getChildAt(childCount - 1) ?: return 0
            val lastVisibleRow = findRowOfChild(getPosition(lastVisibleChild))
            val lastVisibleRowIndex = preparedRows.indexOf(lastVisibleRow)
            log("➕📜: lastVisibleRowIndex: $lastVisibleRowIndex")
            val lastRowBottom = getDecoratedTop(lastVisibleChild) + lastVisibleRow.rowHeight
            var topOffset = lastRowBottom
            var laidOutHeight = 0
            for (i in lastVisibleRowIndex + 1 until preparedRows.size) {
                val rowToLayout = preparedRows[i]
                layoutRowBelow(rowToLayout, topOffset, i)
                topOffset += rowToLayout.rowHeight
                laidOutHeight += rowToLayout.rowHeight
                if (laidOutHeight > dy) {
                    log("➕📜: Already laid out more height ($laidOutHeight) than dy ($dy), returning dy.")
                    return dy
                }
            }
            // the last visible row may have changed, so we need to retrieve it again
            val lastVisibleChildAfterScroll = getChildAt(childCount - 1) ?: return 0
            val lastVisibleRowAfterScroll = findRowOfChild(getPosition(lastVisibleChildAfterScroll))
            val lastRowBottomAfterScroll = getDecoratedTop(lastVisibleChildAfterScroll) + lastVisibleRowAfterScroll.rowHeight
            val lastRowAfterScrollHiddenPart = lastRowBottomAfterScroll - height
            log("➕📜: Ran out of rows. Returning the smaller from dy ($dy) and lastRowAfterScrollHiddenPart ($lastRowAfterScrollHiddenPart).")
            return min(lastRowAfterScrollHiddenPart, dy)
        } else {
            val lastVisibleChild = getChildAt(0) ?: return 0
            val lastVisibleRow = findRowOfChild(getPosition(lastVisibleChild))
            val lastVisibleRowIndex = preparedRows.indexOf(lastVisibleRow)
            log("➕📜: lastVisibleRowIndex: $lastVisibleRowIndex")
            val lastRowTop = getDecoratedTop(lastVisibleChild)
            var bottomOffset = lastRowTop
            var laidOutHeight = 0
            for (i in lastVisibleRowIndex - 1 downTo 0) {
                val rowToLayout = preparedRows[i]
                layoutRowAbove(rowToLayout, bottomOffset, i)
                bottomOffset -= rowToLayout.rowHeight
                laidOutHeight += rowToLayout.rowHeight
                if (laidOutHeight > abs(dy)) {
                    log("➕📜: Already laid out more height ($laidOutHeight) than absolute dy (${abs(dy)}), returning dy.")
                    return dy
                }
            }
            // the last visible row may have changed, so we need to retrieve it again
            val lastVisibleChildAfterScroll = getChildAt(0) ?: return 0
            val lastRowTopAfterScroll = getDecoratedTop(lastVisibleChildAfterScroll)
            log("➕📜: Ran out of rows. Returning the larger from dy ($dy) and lastRowTopAfterScroll ($lastRowTopAfterScroll).")
            return max(lastRowTopAfterScroll, dy)
        }
    }

    /**
     * Finds invisible rows and removes and recycles all views they contain.
     */
    private fun recycleInvisibleViews(recycler: RecyclerView.Recycler) {
        log("♻️: Recycling views")
        var recycledChildCount = 0
        var recycledRowCount = 0
        var i = 0
        val childrenToRecycle = mutableListOf<View>()
        while (i in 0 until childCount) {
            val firstChildInRow = getChildAt(i) ?: continue
            val firstChildInRowAdapterPos = getPosition(firstChildInRow)
            val row = findRowOfChild(firstChildInRowAdapterPos)
            val highestChildInRowLayoutIndex = i + (row.highestChildIndex - row.fromIndex)
            val highestChildInRow = getChildAt(highestChildInRowLayoutIndex) ?: throw RuntimeException("Can't obtain highest child from row!")
            val rowNotVisibleAnymore = getDecoratedBottom(highestChildInRow) < 0 || getDecoratedTop(highestChildInRow) > height
            if (rowNotVisibleAnymore) {
                // recycle children no longer in view
                for (child in row.preparedChildren) {
                    val layoutIndex = i + (child.childIndex - row.fromIndex)
                    val view = getChildAt(layoutIndex) ?: continue
                    childrenToRecycle.add(view)
                    log("♻️: Scheduled child with adapter position ${child.childIndex} and layout index $layoutIndex for recycling.")
                    recycledChildCount++
                }
                recycledRowCount++
            }
            i += row.preparedChildren.size
        }
        for (view in childrenToRecycle) removeAndRecycleView(view, recycler)
        if (recycledChildCount != 0) log("✅♻️: Recycled $recycledRowCount rows, that is $recycledChildCount views.")
    }

    /**
     * Measures all views and sorts them into a list of rows, representing information about how all of the children should be laid out.
     * Saves the result into [preparedRows].
     */
    private fun prepareAllRows(recycler: RecyclerView.Recycler) {
        log("🍳🌎🚣‍‍: Preparing all rows")
        preparedRows.clear()
        val start = 0
        var i = start
        while (i in start until itemCount) {
            val row = prepareRow(recycler, i)
            preparedRows.add(row)
            i = row.toIndex + 1
        }
        log("🍳🌎🚣✅‍‍: Preparing rows done. Rows prepared: ${preparedRows.size}")
    }

    /**
     * Prepares and returns a single row of children.
     * [fromChildIndex] The adapter position of the first child to take into consideration when finding views for this row.
     */
    private fun prepareRow(recycler: RecyclerView.Recycler, fromChildIndex: Int): PreparedRow {
        val preparedChildren = mutableListOf<PreparedChild>()
        var availableWidth = width
        var highestChildHeight = 0
        var highestChildIndex = fromChildIndex
        var endIndex = fromChildIndex
        for (i in fromChildIndex until itemCount) {
            val view = recycler.getViewForPosition(i)
            measureChildWithMargins(view, 0, 0)
            val measuredWidth = getDecoratedMeasuredWidth(view)
            val measuredHeight = getDecoratedMeasuredHeight(view)
            // save the height of the highest view
            if (measuredHeight > highestChildHeight) {
                highestChildHeight = measuredHeight
                highestChildIndex = i
            }
            val firstIteration = i == fromChildIndex
            if (firstIteration && measuredWidth >= availableWidth) {
                // If this is the first run of the loop and the view is larger than the available width,
                // it will need to be on its own line and it will be cropped. If this is the case, add the view to the prepared list and
                // break the loop.
                preparedChildren.add(PreparedChild(i, measuredWidth, measuredHeight, view))
                endIndex = i
                break
            } else if (measuredWidth > availableWidth) {
                break // break the loop if the next view won't fit on this line
            } else {
                // add the view to the prepared list and add subtract its width from the available space
                preparedChildren.add(PreparedChild(i, measuredWidth, measuredHeight, view))
                availableWidth -= measuredWidth
                endIndex = i
            }
        }
        return PreparedRow(preparedChildren, highestChildHeight, highestChildIndex, fromChildIndex, endIndex)
    }

    /**
     * Finds and returns the [PreparedRow] from [preparedRows] in which the child with adapter position [childIndex] is contained.
     */
    private fun findRowOfChild(childIndex: Int): PreparedRow {
        for (row in preparedRows) {
            for (child in row.preparedChildren) {
                if (child.childIndex == childIndex) return row
            }
        }
        throw RuntimeException("The child with index $childIndex was not found in any row!")
    }

    private fun log(message: String) {
        @Suppress("ConstantConditionIf")
        if (LOG_ENABLED) Timber.d(message)
    }

    companion object {
        const val LOG_ENABLED = false
    }

    /**
     * Information about how a row of children should be laid out.
     * [preparedChildren] The list of children views this row has and info about them.
     * [rowHeight] The height of this row, respectively the height of the highest child in this row.
     * [highestChildIndex] Adapter position of the highest child in this row.
     * [fromIndex] Adapter position of the first child in this row.
     * [toIndex] Adapter position of the last child in this row.
     */
    data class PreparedRow(
        val preparedChildren: List<PreparedChild>,
        val rowHeight: Int,
        val highestChildIndex: Int,
        val fromIndex: Int,
        val toIndex: Int
    )

    /**
     * Information about how a child should be laid out.
     * [childIndex] Adapter position of the child.
     * [measuredWidth] The child wants to be this wide.
     * [measuredHeight] The child wants to be this high.
     * [view] The view of this child.
     */
    data class PreparedChild(
        val childIndex: Int,
        val measuredWidth: Int,
        val measuredHeight: Int,
        val view: View
    )
}
