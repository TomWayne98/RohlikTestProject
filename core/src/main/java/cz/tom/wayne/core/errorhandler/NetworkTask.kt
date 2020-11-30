package cz.tom.wayne.core.errorhandler

/**
 * LIST ALL POSSIBLE TASKS HERE:
 */

object CreateCharacterTask : NetworkTask()

object ListenForUserUpdatesTask : NetworkTask(showUi = false)

object AuthenticateTask : NetworkTask()

object UpdateAccountInfoTask : NetworkTask()

object CreateCampaignTask : NetworkTask()

object InviteToCampaignTask : NetworkTask()

object CheckUserAccountTask : NetworkTask()

object UpdateUsernameTask : NetworkTask()

object UpdateProfilePictureTask : NetworkTask()

object UploadProfilePictureToFirebaseStorageTask : NetworkTask()

object DeleteAccountTask : NetworkTask()

object AcceptInviteTask : NetworkTask()

object DeclineInviteTask : NetworkTask()

object CancelInviteTask : NetworkTask()

object StartGameTask : NetworkTask()

object SetSceneTask : NetworkTask()

object CreateChatRoomTask : NetworkTask()

object LogOutTask : NetworkTask()

object UpdateRollTask : NetworkTask()

/**
 * Use when a custom task is not needed,
 * for example when when you only need to display an error without needing the progress and success functionality.
 */
object NoTask : NetworkTask()

/**
 * Task class for [ErrorHandler]. See its docs for more.
 * [state] State of this task.
 * [showUi] Whether [ErrorHandler] should show its UI stuff for this task.
 */
sealed class NetworkTask(var showUi: Boolean = true) {

    var state: FireTaskState? = NotStarted
        private set

    fun isNotStarted(): Boolean = state is NotStarted

    fun isSuccessful(): Boolean = state is FireSuccess<*>

    fun isProgressing(): Boolean = state is FireProgress

    fun isError(): Boolean = state is NetworkError

    fun setProgressing(): NetworkTask {
        state = FireProgress
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
        state = FireSuccess(result)
        return this
    }

    fun setSuccess(): NetworkTask {
        state = FireSuccess(Unit)
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
