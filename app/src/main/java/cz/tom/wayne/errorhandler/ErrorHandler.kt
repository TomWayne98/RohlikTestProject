package cz.tom.wayne.errorhandler

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData


/**
 * An event and task handler, that should be used where possible for long running or background operations
 * across the app to avoid defining a lot of callback functions.
 * Handles state of tasks - progress, errors and success, along with listeners that subscribe only to the events of relevant tasks and along with
 * displaying UI about the events to the user.
 *
 * BASE IDEA:
 * The base idea is that every operation that is executed on background should define a subclass of [NetworkTask] along with
 * a subclass of [NetworkError] for all errors that can happen while executing the task. Then when executing the task, call [handleEvent] with the task
 * object and set its [NetworkTask.state] to [NetworkProgress].
 * Then if the task succeeds, call [handleEvent] again with the task's state set to [NetworkSuccess],
 * optionally you can provide the result of the operation. The dialog will be dismissed. If the task failed, call [handleEvent] with the task's state
 * set to a corresponding subclass of [NetworkError]. An error dialog will be displayed with texts depending on the type of the error.
 *
 * LISTENERS:
 * To listen for events, call either [addEventListener] to add a lambda callback (for non-lifecycle-aware callers), or [addLiveDataEventListener],
 * which returns a live data for lifecycle-aware callers. However, the UI handled by [FireHandler] is non-dependant on the listeners, so if a task
 * has no listeners set, the dialogs will still be displayed. After a task ends, all listeners for it all cleared.
 *
 * UI HANDLING:
 * [FireHandler] handles and shows some UI automatically depending on the task state:
 * (To disable UI handling, set your task's [NetworkTask.showUi] to false)
 * If it is a [NetworkProgress], dismisses all previously shown error dialogs and shows a non-dismissable, non-cancelable progress dialog
 *   with progress text related to the task, but only if [NetworkTask.showUi] is set to true.
 * If it is a [NetworkSuccess], dismisses all previously shown progress dialogs, but only if [NetworkTask.showUi] is set to true.
 * If it is a [NetworkError], dismisses all previously shown progress dialogs (even if [NetworkTask.showUi] is false), and then
 *   if [NetworkTask.showUi] is true, shows an error dialog with information about the error, based on the error type.
 *   That way, an error will always dismiss a progress dialog, but won't show the error dialog if not needed. This is useful for example
 *   when the error is [NetworkError.CancelledByUser] with [NetworkTask.showUi] set to false, where we don't want to show
 *   any error message (because the user knows he cancelled the action), but we still want to dismiss the progress dialog.
 *
 * IMPLEMENTATION:
 * The implementation of this class is in coreUI, and there in FireHandlerResources, you should also define the string resources for your tasks
 * and errors.
 */
interface ErrorHandler {

    /**
     * A [FragmentActivity] that is visible has to observe this (in resumed state) and execute the lambdas in order for the dialogs to work.
     */
    var activityRequests: LiveData<(FragmentActivity) -> Unit>

    /**
     * Handles the events based on the state of the [task]. See class docs for more.
     */
    fun handleEvent(task: NetworkTask)

    /**
     * Adds a lambda [callback], in case it wasn't previously added for this [priority].
     * [task] the task to listen for - the listener will receive events related only to this specific task.
     * [priority] - an integer (constants defined in the companion can be used). Because a single task can have multiple listeners in
     *  different places, priority defines the order in which they are called (highest goes first).
     *  Also, only one listener for a priority is allowed.
     * [passOn] whether listeners with lower priority should be called after this one gets executed.
     */
    fun addEventListener(task: NetworkTask, priority: Int = PRIORITY_DEFAULT, passOn: Boolean = true, callback: (task: NetworkTask) -> Unit)

    /**
     * Creates and returns a [LiveData] listener, if it wasn't previously added for this [priority], in that case returns null.
     * [task] the task to listen for - the listener will receive events related only to this specific task.
     * [priority] - an integer (constants defined in the companion can be used). Because a single task can have multiple listeners in
     *  different places, priority defines the order in which they are called (highest goes first).
     *  Also, only one listener for a priority is allowed.
     * [passOn] whether listeners with lower priority should be called after this one gets executed.
     */
    fun addLiveDataEventListener(task: NetworkTask, priority: Int = PRIORITY_DEFAULT, passOn: Boolean = true): LiveData<NetworkTask>?

    /**
     * Clears all registered listeners for the given [task].
     */
    fun clearListenersFor(task: NetworkTask)

    companion object {
        const val PRIORITY_API = 40
        const val PRIORITY_REPO = 30
        const val PRIORITY_VM = 20
        const val PRIORITY_VIEW = 10
        const val PRIORITY_DEFAULT = 0
    }
}
