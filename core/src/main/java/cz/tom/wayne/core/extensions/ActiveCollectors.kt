package cz.tom.wayne.core.extensions

/**
 * Wraps a registry of active coroutine flow collector tags.
 */
object ActiveCollectors {

    /**
     * Needed for [collectIfNotCollecting] and similar. Contains the tags of flows that are currently being collected.
     */
    val activeCollectorTags = mutableSetOf<String>()
}
