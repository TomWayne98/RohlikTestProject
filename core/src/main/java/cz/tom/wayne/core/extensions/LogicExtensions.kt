@file:Suppress("TooManyFunctions")
package cz.tom.wayne.core.extensions

import java.util.*

inline fun <A, B, R> ifNotNull(a: A?, b: B?, doCrazyShit: (A, B) -> R) {
    if (a != null && b != null) {
        doCrazyShit(a, b)
    }
}

inline fun <A, B, C, R> ifNotNull(a: A?, b: B?, c: C?, doCrazyShit: (A, B, C) -> R) {
    if (a != null && b != null && c != null) {
        doCrazyShit(a, b, c)
    }
}

/**
 * Returns first index of [element], or null if the list does not contain the element.
 */
fun <T> List<T>.indexOfOrNull(element: T): Int? {
    val index = indexOf(element)
    return if (index != -1) index else null
}

/**
 * Returns index of the first element matching the given [predicate], or null if the list does not contain such element.
 */
fun <T> List<T>.indexOfFirstOrNull(predicate: (T) -> Boolean): Int? {
    val index = indexOfFirst(predicate)
    return if (index != -1) index else null
}

/**
 * Creates another list instance with same elements
 * Useful when you need to backup origin state of your list
 */
fun <T> List<T>.clone(): List<T> {
    val clonedList = mutableListOf<T>()
    clonedList.addAll(this)
    return clonedList
}

/**
 * Shorthand for a lambda with no arguments and no return type. Can be used for click listeners, etc.
 */
typealias SimpleLambda = () -> Unit

/**
 * Calls [f] on every element from vararg [receivers].
 */
inline fun <T> onEach(vararg receivers: T, crossinline f: T.(index: Int) -> Unit) {
    for ((index, receiver) in receivers.withIndex()) receiver.f(index)
}

/**
 * Calls [f] on every non-null element from vararg [receivers].
 */
inline fun <T : Any> onEachNonNull(vararg receivers: T?, crossinline f: T.(index: Int) -> Unit) {
    onEachFromListNonNull(receivers.filterNotNull(), f)
}

/**
 * Calls [f] on every element from [receivers] list.
 */
inline fun <T> onEachFromList(receivers: List<T>, crossinline f: T.(index: Int) -> Unit) {
    for ((index, receiver) in receivers.withIndex()) receiver.f(index)
}

/**
 * Calls [f] on every non-null element from [receivers] list.
 */
inline fun <T : Any> onEachFromListNonNull(receivers: List<T?>, crossinline f: T.(index: Int) -> Unit) {
    onEachFromList(receivers.filterNotNull(), f)
}

/**
 * Returns the first element, or calls [error] with the given [errorMessage] if the list is empty.
 */
fun <T> Iterable<T>.firstOrError(errorMessage: String): T = firstOrNull() ?: error(errorMessage)

/**
 * Returns the first element matching the given [predicate], or calls [error] with the given [errorMessage] if element was not found.
 */
inline fun <T> Iterable<T>.firstOrError(errorMessage: String, predicate: (T) -> Boolean): T = firstOrNull(predicate) ?: error(errorMessage)

/**
 * Returns the first result of the [mapper] function that produces a non-null result, or null if no such result was returned for any of the elements.
 */
inline fun <T, R> Iterable<T>.mapFirstNonNull(mapper: (T) -> R?): R? {
    for (element in this) {
        val result = mapper(element)
        if (result != null) return result
    }
    return null
}

/**
 * Calls [Stack.peek] on this stack and returns the result if it is not empty or returns null if empty.
 */
fun <T> Stack<T>.peekOrNull(): T? {
    return if (!empty()) peek() else null
}
