package cz.tom.wayne.errorhandler

/**
 * State of a [NetworkTask].
 * Can be [NetworkProgress], [NetworkSuccess], [NetworkError] or [NotStarted].
 */
sealed class NetworkTaskState

object NetworkProgress : NetworkTaskState()

class NetworkSuccess<T>(var result: T) : NetworkTaskState()

object NotStarted : NetworkTaskState()

/**
 * Error class for [FireTask], resp. [FireHandler]. See their docs for more.
 */
sealed class NetworkError : NetworkTaskState() {
    /**
     * LIST ALL POSSIBLE ERRORS HERE
     */
    // Generic errors
    object NoConnection : NetworkError()

    object GenericError : NetworkError()
    // and so on
}
