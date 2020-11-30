@file:Suppress("TooManyFunctions")

package cz.tom.wayne.core.extensions

import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import java.util.*

/**
 * Bob 👦 is the best server, he serves the best, so from now on, we will call him to serve our LiveData.
 *
 * Explanation: When having this named observe, like the original [LiveData.observe],
 * the IDE often had problems with differentiating the two and importing them correctly, so I called Bob for help.
 */
fun <T> LiveData<T>.bobserve(lifecycleOwner: LifecycleOwner, doOnObserve: (T) -> (Unit)) {
    val observer: Observer<T> = Observer { value ->
        doOnObserve(value)
    }
    this.observe(lifecycleOwner, observer)
}

fun <T> LiveData<T>.bobserve(fragment: Fragment, doOnObserve: (T?) -> (Unit)) {
    bobserve(fragment.viewLifecycleOwner, doOnObserve)
}

fun <T> LiveData<T>.observeNonNull(lifecycleOwner: LifecycleOwner, doOnObserve: (T) -> (Unit)) {
    val observer: Observer<T> = Observer { value ->
        if (value != null) {
            doOnObserve(value)
        }
    }
    this.observe(lifecycleOwner, observer)
}

fun <T> LiveData<T>.observeNonNull(fragment: Fragment, doOnObserve: (T) -> (Unit)) {
    observeNonNull(fragment.viewLifecycleOwner, doOnObserve)
}

fun <T> LiveData<T>.removeObserversFromFragment(fragment: Fragment) {
    removeObservers(fragment.viewLifecycleOwner)
}

fun <T> MutableLiveData<T>.changeValue(dataValue: (T) -> (T)) {
    this.postValue(dataValue(this.value!!))
}

val <T> LiveData<T>.valueOrThrow: T
    get() = value!!

/**
 * Adds [sourceA] and [sourceB] to this [MediatorLiveData]. When one of the sources emits a value, the
 * values of both sources are checked. If both values are non-null at this moment, [merge] is called
 * and the value returned from it is posted to the mediator.
 * @return this mediator.
 */
fun <T, A, B> MediatorLiveData<T>.mergeSourcesNonNull(
    sourceA: LiveData<A>,
    sourceB: LiveData<B>,
    merge: MediatorLiveData<T>.(a: A, b: B) -> T
): MediatorLiveData<T> {
    fun mergeInternal(a: A?, b: B?) {
        if (a != null && b != null) postValue(this.merge(a, b))
    }
    addSource(sourceA) {
        mergeInternal(it, sourceB.value)
    }
    addSource(sourceB) {
        mergeInternal(sourceA.value, it)
    }
    return this
}

/**
 * @see [mergeSourcesNonNull] with two sources - only difference is the number of sources.
 */
fun <T, A, B, C> MediatorLiveData<T>.mergeSourcesNonNull(
    sourceA: LiveData<A>,
    sourceB: LiveData<B>,
    sourceC: LiveData<C>,
    merge: MediatorLiveData<T>.(a: A, b: B, c: C) -> T
): MediatorLiveData<T> {
    fun mergeInternal(a: A?, b: B?, c: C?) {
        if (a != null && b != null && c != null) postValue(this.merge(a, b, c))
    }
    addSource(sourceA) {
        mergeInternal(it, sourceB.value, sourceC.value)
    }
    addSource(sourceB) {
        mergeInternal(sourceA.value, it, sourceC.value)
    }
    addSource(sourceC) {
        mergeInternal(sourceA.value, sourceB.value, it)
    }
    return this
}

/**
 * @see [mergeSourcesNonNull] with two sources - only difference is the number of sources.
 */
fun <T, A, B, C, D> MediatorLiveData<T>.mergeSourcesNonNull(
    sourceA: LiveData<A>,
    sourceB: LiveData<B>,
    sourceC: LiveData<C>,
    sourceD: LiveData<D>,
    merge: MediatorLiveData<T>.(a: A, b: B, c: C, d: D) -> T
): MediatorLiveData<T> {
    fun mergeInternal(a: A?, b: B?, c: C?, d: D?) {
        @Suppress("ComplexCondition")
        if (a != null && b != null && c != null && d != null) postValue(this.merge(a, b, c, d))
    }
    addSource(sourceA) {
        mergeInternal(it, sourceB.value, sourceC.value, sourceD.value)
    }
    addSource(sourceB) {
        mergeInternal(sourceA.value, it, sourceC.value, sourceD.value)
    }
    addSource(sourceC) {
        mergeInternal(sourceA.value, sourceB.value, it, sourceD.value)
    }
    addSource(sourceD) {
        mergeInternal(sourceA.value, sourceB.value, sourceC.value, it)
    }
    return this
}

/**
 * Convenience for MediatorLiveData().mergeSourcesNonNull().
 * Creates a [MediatorLiveData] and calls [mergeSourcesNonNull] on it with the arguments provided.
 */
fun <T, A, B> createMediatorAndMergeSourcesNonNull(
    sourceA: LiveData<A>,
    sourceB: LiveData<B>,
    merge: MediatorLiveData<T>.(a: A, b: B) -> T
): MediatorLiveData<T> {
    return MediatorLiveData<T>().apply {
        mergeSourcesNonNull(sourceA, sourceB, merge)
    }
}

/**
 * @see [createMediatorAndMergeSourcesNonNull] with two sources - only difference is the number of sources.
 */
fun <T, A, B, C> createMediatorAndMergeSourcesNonNull(
    sourceA: LiveData<A>,
    sourceB: LiveData<B>,
    sourceC: LiveData<C>,
    merge: MediatorLiveData<T>.(a: A, b: B, c: C) -> T
): MediatorLiveData<T> {
    return MediatorLiveData<T>().apply {
        mergeSourcesNonNull(sourceA, sourceB, sourceC, merge)
    }
}

/**
 * @see [createMediatorAndMergeSourcesNonNull] with two sources - only difference is the number of sources.
 */
fun <T, A, B, C, D> createMediatorAndMergeSourcesNonNull(
    sourceA: LiveData<A>,
    sourceB: LiveData<B>,
    sourceC: LiveData<C>,
    sourceD: LiveData<D>,
    merge: MediatorLiveData<T>.(a: A, b: B, c: C, d: D) -> T
): MediatorLiveData<T> {
    return MediatorLiveData<T>().apply {
        mergeSourcesNonNull(sourceA, sourceB, sourceC, sourceD, merge)
    }
}

/**
 * Adds [sourceA] and [sourceB] to this [MediatorLiveData]. When one of the sources emits a value, [merge] is called
 * and the value returned from it is posted to the mediator.
 * @return this mediator.
 */
fun <T, A, B> MediatorLiveData<T>.mergeSources(
    sourceA: LiveData<A>,
    sourceB: LiveData<B>,
    merge: MediatorLiveData<T>.(a: A?, b: B?) -> T
): MediatorLiveData<T> {
    fun mergeInternal(a: A?, b: B?) {
        postValue(this.merge(a, b))
    }
    addSource(sourceA) {
        mergeInternal(it, sourceB.value)
    }
    addSource(sourceB) {
        mergeInternal(sourceA.value, it)
    }
    return this
}

/**
 * @see [mergeSources] with two sources - only difference is the number of sources.
 */
fun <T, A, B, C> MediatorLiveData<T>.mergeSources(
    sourceA: LiveData<A>,
    sourceB: LiveData<B>,
    sourceC: LiveData<C>,
    merge: MediatorLiveData<T>.(a: A?, b: B?, c: C?) -> T
): MediatorLiveData<T> {
    fun mergeInternal(a: A?, b: B?, c: C?) {
        postValue(this.merge(a, b, c))
    }
    addSource(sourceA) {
        mergeInternal(it, sourceB.value, sourceC.value)
    }
    addSource(sourceB) {
        mergeInternal(sourceA.value, it, sourceC.value)
    }
    addSource(sourceC) {
        mergeInternal(sourceA.value, sourceB.value, it)
    }
    return this
}

/**
 * @see [mergeSources] with two sources - only difference is the number of sources.
 */
fun <T, A, B, C, D> MediatorLiveData<T>.mergeSources(
    sourceA: LiveData<A>,
    sourceB: LiveData<B>,
    sourceC: LiveData<C>,
    sourceD: LiveData<D>,
    merge: MediatorLiveData<T>.(a: A?, b: B?, c: C?, d: D?) -> T
): MediatorLiveData<T> {
    fun mergeInternal(a: A?, b: B?, c: C?, d: D?) {
        postValue(this.merge(a, b, c, d))
    }
    addSource(sourceA) {
        mergeInternal(it, sourceB.value, sourceC.value, sourceD.value)
    }
    addSource(sourceB) {
        mergeInternal(sourceA.value, it, sourceC.value, sourceD.value)
    }
    addSource(sourceC) {
        mergeInternal(sourceA.value, sourceB.value, it, sourceD.value)
    }
    addSource(sourceD) {
        mergeInternal(sourceA.value, sourceB.value, sourceC.value, it)
    }
    return this
}

/**
 * Convenience for MediatorLiveData().mergeSources().
 * Creates a [MediatorLiveData] and calls [mergeSourcesNonNull] on it with the arguments provided.
 */
fun <T, A, B> createMediatorAndMergeSources(
    sourceA: LiveData<A>,
    sourceB: LiveData<B>,
    merge: MediatorLiveData<T>.(a: A?, b: B?) -> T
): MediatorLiveData<T> {
    return MediatorLiveData<T>().apply {
        mergeSources(sourceA, sourceB, merge)
    }
}

/**
 * @see [createMediatorAndMergeSources] with two sources - only difference is the number of sources.
 */
fun <T, A, B, C> createMediatorAndMergeSources(
    sourceA: LiveData<A>,
    sourceB: LiveData<B>,
    sourceC: LiveData<C>,
    merge: MediatorLiveData<T>.(a: A?, b: B?, c: C?) -> T
): MediatorLiveData<T> {
    return MediatorLiveData<T>().apply {
        mergeSources(sourceA, sourceB, sourceC, merge)
    }
}

/**
 * @see [createMediatorAndMergeSources] with two sources - only difference is the number of sources.
 */
fun <T, A, B, C, D> createMediatorAndMergeSources(
    sourceA: LiveData<A>,
    sourceB: LiveData<B>,
    sourceC: LiveData<C>,
    sourceD: LiveData<D>,
    merge: MediatorLiveData<T>.(a: A?, b: B?, c: C?, d: D?) -> T
): MediatorLiveData<T> {
    return MediatorLiveData<T>().apply {
        mergeSources(sourceA, sourceB, sourceC, sourceD, merge)
    }
}

/**
 * Just like [Transformations.map], but when the emitted value is null, it just passes null downstream without calling [mapFunction].
 */
inline fun <X, R> LiveData<X?>.mapSkipNulls(crossinline mapFunction: (X) -> R): LiveData<R> = Transformations.map(this) {
    it?.let(mapFunction)
}

/**
 * Just like [Transformations.switchMap], but when the emitted value is null, it just passes null downstream without calling [mapFunction].
 */
inline fun <X, R> LiveData<X?>.switchMapSkipNulls(crossinline mapFunction: (X) -> LiveData<R>): LiveData<R> = Transformations.switchMap(this) {
    it?.let(mapFunction)
}

fun <T> MutableLiveData<Stack<T>>.pop() {
    value = value?.apply { pop() }
}

fun <T> MutableLiveData<Stack<T>>.push(item: T) {
    value = value?.apply { push(item) }
}
