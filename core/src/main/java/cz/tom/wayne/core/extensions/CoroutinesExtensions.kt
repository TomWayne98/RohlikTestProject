package cz.tom.wayne.core.extensions

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * Coroutines related extensions
 */

// Merge two flows of same T to one flow containing their values
@FlowPreview
@Suppress("SpreadOperator")
fun <T> Flow<T>.merge(vararg flows: Flow<T>) = flowOf(this, *flows).flattenMerge()

/**
 * Returns a flow which performs the given [action] on the first emitted value of the original flow.
 */
fun <T> Flow<T>.onFirst(action: suspend (T) -> Unit): Flow<T> {
    var executed = false
    return onEach {
        if (!executed) {
            action(it)
            executed = true
        }
    }
}

/**
 * Calls [collect] on the flow only if there is no another active collector.
 */
@ExperimentalCoroutinesApi
suspend inline fun <T> Flow<T>.collectIfNotCollecting(tag: String, crossinline action: suspend (value: T) -> Unit) {
    if (!ActiveCollectors.activeCollectorTags.contains(tag)) onCompletion { ActiveCollectors.activeCollectorTags.remove(tag) }.collect(action)
}

/**
 * Calls [collectLatest] on the flow only if there is no another active collector.
 */
@ExperimentalCoroutinesApi
suspend fun <T> Flow<T>.collectLatestIfNotCollecting(tag: String, action: suspend (value: T) -> Unit) {
    if (!ActiveCollectors.activeCollectorTags.contains(tag)) onCompletion { ActiveCollectors.activeCollectorTags.remove(tag) }.collectLatest(action)
}

/**
 * Returns the job of this scope, or null if it doesn't have any.
 */
val CoroutineScope.job: Job?
    get() = coroutineContext[Job]

/**
 * [launch]es a new coroutine inside this context and starts collecting the [source] flow in that coroutine.
 * Whenever the flow emits a value, it gets sent to [target] [MutableLiveData].
 */
fun <X> CoroutineScope.collectFlowToLiveData(source: Flow<X>, target: MutableLiveData<X>) {
    launch {
        source.collect {
            target.postValue(it)
        }
    }
}
