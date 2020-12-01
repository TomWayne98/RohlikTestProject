package cz.tom.wayne.architecture

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

/**
 * Contains all common logic for viewModels - handling lifecycler of coroutines etc.
 * Should be inherited by all viewModels
 */
abstract class BaseViewModel : ViewModel(), CoroutineScope {

    private val job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    val goBackEvent = SingleLiveEvent<Boolean>()

    /**
     * An array of functions that should be executed when the VM is clearing itself.
     * F.E.: a function to clear a [LiveData] observer.
     */
    protected val clearingTasks = mutableListOf<() -> Unit>()

    init {
        Timber.d("NAVIGATION VIEWMODEL: onInit is called for ${javaClass.simpleName}")
    }

    /**
     * Use this to trigger a back event without the user actually clicking the back button.
     * The event will be routed to the activity, which handles it as a normal back event
     * (the activity calls [Navigator.navigateBack], so if you need custom back behavior, modify it there).
     */
    fun goBack() {
        goBackEvent.call()
    }

    /**
     * For use with data binding.
     * Same as calling [goBack] with no params.
     * [view] Param is ignored.
     */
    fun goBack(view: View?) {
        goBack()
    }

    /**
     * Called by activity when a back event has been intercepted.
     * This is for the ViewModel to be able to intercept back events.
     *
     * NOTE: this doesn't trigger a back event, it is just used to intercept it. To trigger one, use [goBack].
     */
    open fun onBackPressed() {}

    /**
     * Starts observing the [liveData] in this [ViewModel] (without the need of a lifecycle owner).
     * The observer will get automatically cleared in this VM's [ViewModel.onCleared].
     */
    protected fun <T> observe(liveData: LiveData<T>, doOnObserve: (T?) -> Unit) {
        val observer: Observer<T?> = object : Observer<T?> {
            override fun onChanged(t: T?) {
                doOnObserve(t)
            }
        }
        liveData.observeForever(observer)
        clearingTasks.add { liveData.removeObserver(observer) }
    }

    /**
     * Starts observing the [liveData] in this [ViewModel] (without the need of a lifecycle owner).
     * Only triggers [doOnObserve] if the emitted value of [liveData] is not null.
     * The observer will get automatically cleared in this VM's [ViewModel.onCleared].
     */
    protected fun <T> observeIfNotNull(liveData: LiveData<T>, doOnObserve: (T) -> Unit) {
        val observer: Observer<T?> = object : Observer<T?> {
            override fun onChanged(t: T?) {
                t?.let {
                    doOnObserve(t)
                }
            }
        }
        liveData.observeForever(observer)
        clearingTasks.add { liveData.removeObserver(observer) }
    }

    /**
     * Starts observing the [liveData] in this [ViewModel] (without the need of a lifecycle owner).
     * Only triggers [doOnObserve] once, then removes the observer.
     * However, if the VM gets cleared before a value is emitted,
     * the observer will get automatically cleared in this VM's [ViewModel.onCleared].
     */
    protected fun <T> observeOnce(liveData: LiveData<T?>, doOnObserve: (T?) -> Unit) {
        val observer: Observer<T?> = object : Observer<T?> {
            override fun onChanged(t: T?) {
                liveData.removeObserver(this)
                doOnObserve(t)
            }
        }
        liveData.observeForever(observer)
        clearingTasks.add { liveData.removeObserver(observer) }
    }

    /**
     * Starts observing the [liveData] in this [ViewModel] (without the need of a lifecycle owner).
     * Only triggers [doOnObserve] if the value of [liveData] is not null and just once, then removes the observer.
     * However, if the VM gets cleared before a value is emitted,
     * the observer will get automatically cleared in this VM's [ViewModel.onCleared].
     */
    protected fun <T> observeOnceIfNotNull(liveData: LiveData<T?>, doOnObserve: (T) -> Unit) {
        val observer: Observer<T?> = object : Observer<T?> {
            override fun onChanged(t: T?) {
                t?.let {
                    liveData.removeObserver(this)
                    doOnObserve(t)
                }
            }
        }
        liveData.observeForever(observer)
        clearingTasks.add { liveData.removeObserver(observer) }
    }

    /**
     * Adds a live data event listener for the given [task] to [fireHandler] (if it was not added
     * yet) and starts observing the live data. Calls [onUpdated] when the listener is triggered.
     */
    /* protected inline fun observeTask(
         fireHandler: ErrorHandler,
         task: NetworkTask,
         crossinline onUpdated: (task: NetworkTask) -> Unit
     ) {
         fireHandler.addLiveDataEventListener(task, ErrorHandler.PRIORITY_VM)?.let { ld ->
             observeIfNotNull(ld) {
                 onUpdated(it)
             }
         }
     }*/

    override fun onCleared() {
        cancel()
        executeClearingTasks()
        super.onCleared()
    }

    /**
     * Executes any functions that were added to [clearingTasks].
     */
    private fun executeClearingTasks() {
        clearingTasks.forEach { it() }
    }

    companion object {
        const val TAG = "Architecture-core"
    }
}
