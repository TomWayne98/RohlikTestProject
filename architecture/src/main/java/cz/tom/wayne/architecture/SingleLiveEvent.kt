package cz.tom.wayne.architecture

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Google SingleLiveEvent to get full info
 */
class SingleLiveEvent<T> : MediatorLiveData<T>() {

    private val pending = AtomicBoolean()

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        pending.set(false) // prevent triggering at the start

        super.observe(
            owner, {
                if (pending.compareAndSet(true, false)) {
                    observer.onChanged(it)
                }
            }
        )
    }

    @MainThread
    override fun setValue(t: T?) {
        pending.set(true)
        super.setValue(t)
    }

    @MainThread
    fun call() {
        value = null
    }
}
