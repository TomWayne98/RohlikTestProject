package cz.tom.wayne.core.errorhandler

/**
 * Error class for [NetworkTask], resp. [ErrorHandler]. See their docs for more.
 */
sealed class NetworkError : FireTaskState() {
    /**
     * LIST ALL POSSIBLE ERRORS HERE
     */
    // Generic errors
    object NoConnection : NetworkError()

    object GenericError : NetworkError()
    object CancelledByUser : NetworkError()

    // Add specific errors here
}

object FireProgress : FireTaskState()

class FireSuccess<T>(var result: T) : FireTaskState()

object NotStarted : FireTaskState()

/**
 * State of a [NetworkTask].
 * Can be [FireProgress], [FireSuccess], [NetworkError] or [NotStarted].
 */
sealed class FireTaskState
