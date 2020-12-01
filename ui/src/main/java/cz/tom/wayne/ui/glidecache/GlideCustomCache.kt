package cz.tom.wayne.ui.glidecache

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.util.LruCache
import timber.log.Timber

/**
 * Holds all stuff relevant for preloading bitmaps to LRU/Glide cache
 */
object GlideCustomCache {

    private lateinit var context: Context

    /**
     * @see precacheImageToFastCache() for info on how this works
     */
    private var permanentMemCache = GlideCustomLRUCache()

    // logging can be turned on using this flag, we don't need the logs at all times
    private const val LOGGING_ENABLED = false

    fun init(context: Context) {
        this.context = context
    }

    /**
     * Loads [drawableRes] silently and stores the image in all available Glide caches.
     */
    fun precacheImageToGlideCache(@DrawableRes drawableRes: Int, onReady: (() -> Unit)? = null) {
        Glide.with(context).asBitmap()
            .load(drawableRes)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(OneTimeCacheTarget(onReady))
    }

    /**
     * Loads [drawableRes] silently and stores the bitmap ONLY in an in-memory LRU cache.
     * [drawableRes] is also used as the key of the image withing the LRU cache.
     * To retrieve the bitmap when it is needed, use [getBitmapForKey].
     *
     * Only loads the bitmap if it is not present in the cache already.
     */
    fun precacheImageToFastCache(@DrawableRes drawableRes: Int, onReady: (() -> Unit)? = null) {
        if (!isImageWithKeyLoaded(drawableRes)) {
            // TODO: cap the image size to max the size of the screen
            Glide.with(context).asBitmap()
                .load(drawableRes)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(FastCacheTarget(drawableRes, onReady, context))
        } else if (LOGGING_ENABLED) {
            val resourceName = context.resources.getResourceEntryName(drawableRes)
            Timber.d("Image with key: $drawableRes and name $resourceName is already loaded in fast cache.")
        }
    }

    fun precacheImagesToFastCache(vararg drawableRes: Int) {
        for (key in drawableRes) precacheImageToFastCache(key)
    }

    /**
     * Returns the preloaded bitmap for the given [key] or null if the image has not been preloaded.
     */
    fun getBitmapForKey(@DrawableRes key: Int): Bitmap? {
        val preloaded = permanentMemCache.get(key)
        if (LOGGING_ENABLED) {
            val resourceName = context.resources.getResourceEntryName(key)
            if (preloaded != null) Timber.d("Image with key $key and name $resourceName loaded from fast cache.")
            else Timber.d("Image with key $key and name $resourceName is not present in fast cache.")
        }
        return preloaded
    }

    fun isImageWithKeyLoaded(@DrawableRes key: Int): Boolean = permanentMemCache.contains(key)

    /**
     * Unloads the preloaded bitmap with the given [key], if loaded.
     *
     * @return Whether the bitmap was removed.
     */
    fun removeBitmapWithKey(@DrawableRes key: Int): Boolean {
        permanentMemCache.remove(key)?.let { bitmap ->
            bitmap.recycle()
            return true
        } ?: return false
    }

    /**
     * Used to load the image only to all Glide caches.
     */
    private class OneTimeCacheTarget(private val onReady: (() -> Unit)?) : CustomTarget<Bitmap>() {
        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            onReady?.invoke()
        }

        override fun onLoadCleared(placeholder: Drawable?) {}
    }

    /**
     * Used to load the image to the [permanentMemCache] LRU cache.
     */
    private class FastCacheTarget(@DrawableRes private val cacheKey: Int, private val onReady: (() -> Unit)?, private val context: Context) : CustomTarget<Bitmap>() {
        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            permanentMemCache.put(cacheKey, resource)
            if (LOGGING_ENABLED) {
                val currentSize = permanentMemCache.currentSize
                val maxSize = permanentMemCache.maxSize
                val resourceName = context.resources.getResourceEntryName(cacheKey)
                Timber.d("Precaching of image with key: $cacheKey and name $resourceName to LRU cache completed - current size of cache $currentSize / $maxSize")
            }
            onReady?.invoke()
        }

        override fun onLoadCleared(placeholder: Drawable?) {}
    }

    private class GlideCustomLRUCache : LruCache<Int, Bitmap>(BYTES_IN_KB * BYTES_IN_KB * SIZE_OF_CACHE_IN_MB.toLong()) {
        override fun getSize(item: Bitmap?): Int = item!!.byteCount.div(BYTES_IN_KB)

        companion object {
            private const val BYTES_IN_KB = 1024
            private const val SIZE_OF_CACHE_IN_MB = 10
        }
    }
}
