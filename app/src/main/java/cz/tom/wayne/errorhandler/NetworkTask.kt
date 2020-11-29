package cz.tom.wayne.errorhandler


/**
 * Task class for [FireHandler]. See its docs for more.
 * [state] State of this task.
 * [showUi] Whether [FireHandler] should show its UI stuff for this task.
 */
sealed class NetworkTask(var showUi: Boolean = true) {

    var state: NetworkTaskState? = NotStarted
        private set

    fun isNotStarted(): Boolean = state is NotStarted

    fun isSuccessful(): Boolean = state is NetworkSuccess<*>

    fun isProgressing(): Boolean = state is NetworkProgress

    fun isError(): Boolean = state is NetworkError

    fun setProgressing(): NetworkTask {
        state = NetworkProgress
        return this
    }

    fun reset(): NetworkTask {
        state = NotStarted
        return this
    }

    fun setError(error: NetworkError = NetworkError.GenericError): NetworkTask {
        state = error
        return this
    }

    fun <T> setSuccess(result: T): NetworkTask {
        state = NetworkSuccess(result)
        return this
    }

    fun setSuccess(): NetworkTask {
        state = NetworkSuccess(Unit)
        return this
    }

    fun noUi(): NetworkTask {
        showUi = false
        return this
    }

    fun withUi(): NetworkTask {
        showUi = true
        return this
    }
}
