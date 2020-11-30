package cz.tom.wayne.core.extensions

import android.content.Context
import android.widget.Toast

fun Context.toast(message: CharSequence) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

/**
 * Creates a list from the [elements] and duplicates the given elements [duplicateCount] times in the list.
 * Use for creating mock data.
 */
fun <T> listOfDuplicates(duplicateCount: Int, vararg elements: T): List<T> {
    val list = mutableListOf<T>()
    if (elements.isNotEmpty()) {
        for (i in 0 until duplicateCount) list.addAll(elements.asList())
    }
    return list
}
