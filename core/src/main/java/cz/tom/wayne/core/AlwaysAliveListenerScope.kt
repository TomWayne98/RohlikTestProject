package cz.tom.wayne.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.isActive

/**
 * Use this scope for launching coroutines that need to run for the whole lifetime of the app,
 * as long as the user is authenticated, for example when launching firestore listeners for data
 * that should be always kept updated.
 *
 * This scope will be cancelled when the user logs out.
 *
 * Use [AlwaysAliveListenerScopeProvider.get] to get an instance.
 */
class AlwaysAliveListenerScope internal constructor() : CoroutineScope {
    override val coroutineContext = Dispatchers.IO + SupervisorJob()
}

/**
 * Manages there is always only one active instance of [AlwaysAliveListenerScope].
 */
object AlwaysAliveListenerScopeProvider {
    private var scope: AlwaysAliveListenerScope = AlwaysAliveListenerScope()

    /**
     * Returns the currently active scope or creates a new one if the current is cancelled
     * (this happens only when a user has logged out and logged in again).
     */
    fun get(): AlwaysAliveListenerScope {
        if (!scope.isActive) {
            scope = AlwaysAliveListenerScope()
        }
        return scope
    }
}
