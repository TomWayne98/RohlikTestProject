@file:Suppress("TooManyFunctions")

package cz.tom.wayne.extension

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.annotation.StyleableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.signature.ObjectKey
import cz.tom.wayne.core.Constants
import cz.tom.wayne.core.extensions.observeNonNull
import cz.tom.wayne.ui.adapters.SimpleBinder
import cz.tom.wayne.ui.adapters.SimpleRecyclerAdapter
import cz.tom.wayne.ui.glidecache.GlideCustomCache
import cz.tom.wayne.ui.glidecache.GlideCustomCache.getBitmapForKey
import io.fireball.fireball.fireui.views.generic.MarginItemDecoration
import java.text.DecimalFormat

// Method for inflating root layout resources of custom views.
fun ViewGroup.inflateCustomViewContent(@LayoutRes layoutRes: Int): View =  LayoutInflater.from(this.context).inflate(layoutRes, this, true)

fun View.makeInvisible() {
    visibility = View.INVISIBLE
}

/**
 * Hey I am just basic bitch adapter
 *
 * I have margins on the top of first item and bottom of the last item
 * I love memes, doggos and linear layout manager.
 * Beer > Wine
 *
 * My adapter is called "SimpleRecyclerAdapter"
 *
 * Send me binder and I am yours
 *
 * I don't inflate first
 *
 */
fun <T> RecyclerView.setup(lifecycle: LifecycleOwner, binder: SimpleBinder<T>, items: LiveData<List<T>>?, itemLayoutRes: Int) {
    val adapter = SimpleRecyclerAdapter(itemLayoutRes, binder)
    this.adapter = adapter
    items?.observeNonNull(lifecycle) {
        adapter.setItems(it)
    }
    setVerticalLM()
    addBasicItemMargin()
}

fun RecyclerView.addBasicItemMargin() {
    addItemDecoration(
        MarginItemDecoration(firstItem = resources.dpToPxInt(Constants.RECYCLER_EXTRA_MARGIN), lastItem = resources.dpToPxInt(Constants.RECYCLER_EXTRA_MARGIN))
    )
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

/**
 * Loads [drawableRes] into this [ImageView].
 *
 * First checks [EmmaSmetanaCache] if the image is preloaded in the cache ([drawableRes] is used as the key) and loads that if it is.
 * If it is not in the cache, it is loaded via glide from the resource. In this case, you can provide [placeholder] to display
 * before the resource loads. [placeholder] is only loaded if it is in the cache, as it makes no sense otherwise.
 *
 * @param placeholder A texture key from [EmmaSmetanaCache] to display before the main image has loaded.
 */
fun ImageView.load(drawableRes: Int, cornerRounding: Int? = null, centerCrop: Boolean = false, placeholder: Int? = null) {
    val preloaded = GlideCustomCache.getBitmapForKey(drawableRes)
    if (preloaded != null) {
        load(preloaded, cornerRounding, centerCrop, skipSizing = true) // skipSizing is needed to prevent Glide from re-loading the image with different size
    } else {
        val placeholderBitmap = placeholder?.let { GlideCustomCache.getBitmapForKey(placeholder) }
        Glide.with(this).load(drawableRes)
            .placeholder(placeholderBitmap?.toDrawable(resources))
            .applyCommonGlideShit(this, cornerRounding, centerCrop)
    }
}

fun ImageView.load(url: String, cornerRounding: Int? = null, centerCrop: Boolean = false) {
    Glide.with(this).load(url).applyCommonGlideShit(this, cornerRounding, centerCrop)
}

fun ImageView.load(drawable: Drawable, cornerRounding: Int? = null, centerCrop: Boolean = false) {
    Glide.with(this).load(drawable).applyCommonGlideShit(this, cornerRounding, centerCrop)
}

/**
 * @param skipSizing Set true if you need to skip the down/upsampling and load the image in the original size.
 */
fun ImageView.load(bitmap: Bitmap, cornerRounding: Int? = null, centerCrop: Boolean = false, skipSizing: Boolean = false) {
    Glide.with(this).load(bitmap).apply { if (skipSizing) override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) }.applyCommonGlideShit(this, cornerRounding, centerCrop)
}

fun RequestBuilder<Drawable>.applyCommonGlideShit(view: ImageView, cornerRounding: Int? = null, centerCrop: Boolean = false) {
    @Suppress("SpreadOperator")
    transform(
        *arrayListOf<Transformation<Bitmap>>().apply {
            if (centerCrop) add(CenterCrop())
            if (cornerRounding != null) add(RoundedCorners(cornerRounding))
        }.toTypedArray()
    ).into(view)
}

fun View.getString(resId: Int, optionalArgs: Any? = null): String {
    return if (optionalArgs != null) {
        resources.getString(resId, optionalArgs)
    } else {
        resources.getString(resId)
    }
}

/**
 * Returns this View's X position on screen.
 * Not to be confused with [getXPosInWindow].
 */
fun View.getXPosOnScreen(): Int {
    val arr = IntArray(2)
    this.getLocationOnScreen(arr)
    return arr[0]
}

/**
 * Returns this View's Y position on screen.
 * Not to be confused with [getYPosInWindow].
 */
fun View.getYPosOnScreen(): Int {
    val arr = IntArray(2)
    this.getLocationOnScreen(arr)
    return arr[1]
}

/**
 * Returns this View's X position inside the window the view is attached to.
 * Not to be confused with [getXPosOnScreen].
 */
fun View.getXPosInWindow(): Int {
    val arr = IntArray(2)
    this.getLocationInWindow(arr)
    return arr[0]
}

/**
 * Returns this View's Y position inside the window the view is attached to.
 * Not to be confused with [getYPosOnScreen].
 */
fun View.getYPosInWindow(): Int {
    val arr = IntArray(2)
    this.getLocationInWindow(arr)
    return arr[1]
}

fun Activity?.getWindowHeight(): Int {
    return this?.window?.decorView?.height ?: 0
}

fun Activity?.getWindowWidth(): Int {
    return this?.window?.decorView?.width ?: 0
}

fun Fragment?.getWindowHeight(): Int {
    return this?.activity?.window?.decorView?.height ?: 0
}

fun Fragment?.getWindowWidth(): Int {
    return this?.activity?.window?.decorView?.width ?: 0
}

fun getScreenWidth(): Int {
    return Resources.getSystem().displayMetrics.widthPixels
}

fun getScreenHeight(): Int {
    return Resources.getSystem().displayMetrics.heightPixels
}

fun View.setWidth(newWidth: Int) {
    this.run {
        val lp = if (layoutParams != null) layoutParams else ViewGroup.LayoutParams(0, 0)
        layoutParams = lp.apply {
            width = newWidth
        }
    }
}

fun View.setHeight(newHeight: Int) {
    this.run {
        val lp = if (layoutParams != null) layoutParams else ViewGroup.LayoutParams(0, 0)
        layoutParams = lp.apply {
            height = newHeight
        }
    }
}

fun View.setDimensions(newWidth: Int, newHeight: Int) {
    this.run {
        layoutParams = layoutParams.apply {
            width = newWidth
            height = newHeight
        }
    }
}

// value in pixels
fun View.setMarginBottom(value: Int) {
    val params = this.layoutParams as ViewGroup.MarginLayoutParams
    params.bottomMargin = value
    this.layoutParams = params
}

// value in pixels
fun View.setMarginLeft(value: Int) {
    val params = this.layoutParams as ViewGroup.MarginLayoutParams
    params.leftMargin = value
    this.layoutParams = params
}

// value in pixels
fun View.setMarginRight(value: Int) {
    val params = this.layoutParams as ViewGroup.MarginLayoutParams
    params.rightMargin = value
    this.layoutParams = params
}

// value in pixels
fun View.setMarginTop(value: Int) {
    val params = this.layoutParams as ViewGroup.MarginLayoutParams
    params.topMargin = value
    this.layoutParams = params
}

fun View.setPaddingTop(value: Int) {
    setPadding(paddingLeft, value, paddingRight, paddingBottom)
}

fun View.setPaddingBottom(value: Int) {
    setPadding(paddingLeft, paddingTop, paddingRight, value)
}

fun View.setPaddingLeft(value: Int) {
    setPadding(value, paddingTop, paddingRight, paddingBottom)
}

fun View.setPaddingRight(value: Int) {
    setPadding(paddingLeft, paddingTop, value, paddingBottom)
}

/**
 * Convenience for [show] and [hide] for a use case where a boolean flag is used to determine visibility.
 */
fun View.setIsShown(isShown: Boolean) = if (isShown) show() else hide()

/**
 * Convenience for [show] and [makeInvisible] for a use case where a boolean flag is used to determine visibility.
 */
fun View.setIsVisible(isShown: Boolean) = if (isShown) show() else makeInvisible()

fun View.onClick(onClick: (View) -> Unit) {
    setOnClickListener {
        onClick.invoke(it)
    }
}

fun FrameLayout.setBackgroundTint(color: Int) {
    backgroundTintList = ColorStateList.valueOf(color)
}

inline var View.isVisible: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }

fun View.getBitmap(): Bitmap {
    val displayMetrics = DisplayMetrics()
    (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
    layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)

    val bitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    draw(canvas)
    return bitmap
}

fun EditText.onTextChanged(onNewText: (String) -> (Unit)) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            onNewText(s.toString())
        }
    })
}

fun RecyclerView.scrollToEnd() {
    val itemCount = adapter?.itemCount ?: 0
    smoothScrollToPosition(itemCount)
}

inline fun ViewPropertyAnimator.setListener(
    crossinline onAnimationStart: (Animator) -> Unit = {},
    crossinline onAnimationRepeat: (Animator) -> Unit = {},
    crossinline onAnimationCancel: (Animator) -> Unit = {},
    crossinline onAnimationEnd: (Animator) -> Unit = {}
) {

    setListener(object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator) {
            onAnimationStart(animation)
        }

        override fun onAnimationRepeat(animation: Animator) {
            onAnimationRepeat(animation)
        }

        override fun onAnimationCancel(animation: Animator) {
            onAnimationCancel(animation)
        }

        override fun onAnimationEnd(animation: Animator) {
            onAnimationEnd(animation)
        }
    })
}

fun View.hideWithFadeAnimation(duration: Long) {
    animate().alpha(0.0f).setDuration(duration).setListener(onAnimationEnd = { hide() })
}

fun View.showWithFadeAnimation(duration: Long) {
    show()
    animate().alpha(1f).setDuration(duration).setListener(null)
}

fun LinearLayoutManager.isScrolledToTop(): Boolean {
    return findFirstCompletelyVisibleItemPosition() == 0
}

fun Context.getScreenWidth(): Int {
    val displayMetrics = DisplayMetrics()
    (this as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)

    return displayMetrics.widthPixels
}

fun Context.getScreenHeight(): Int {
    val displayMetrics = DisplayMetrics()
    (this as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)

    return displayMetrics.heightPixels
}

// These methods are doing the same but one is deprecated, the other is for API >= 24
fun View.startToDrag(
    data: ClipData?,
    shadowBuilder: View.DragShadowBuilder,
    localState: Any,
    flags: Int
): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        startDragAndDrop(data, shadowBuilder, localState, flags)
    } else {
        startDrag(data, shadowBuilder, localState, flags)
    }
}

/**
 * Adds a listener that calls the [f] function block once this [View] has been laid out, and removes the listener after it is triggered.
 * Note that if the view is already laid out at the moment of calling this function, the block will not get executed.
 * See [onGlobalLayoutOrNow] for a workaround.
 */
inline fun <T> T.onGlobalLayout(crossinline f: (T) -> Unit) where T : View {
    val thisView = this
    with(viewTreeObserver) {
        addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                f(thisView)
            }
        })
    }
}

/**
 * Calls the [f] function block immediately if this view is laid out - [View.isLaidOut], otherwise calls [onGlobalLayout].
 * This means that [f] will always be executed at some point, no matter which state the view is in in the moment of calling this function.
 */
inline fun <T> T.onGlobalLayoutOrNow(crossinline f: (T) -> Unit) where T : View {
    if (isLaidOut) f(this) else onGlobalLayout(f)
}

/**
 * Same as [onGlobalLayout] but the view is a receiver in the lambda used here.
 */
inline fun <T> T.applyOnGlobalLayout(crossinline f: T.() -> Unit) where T : View {
    onGlobalLayout(f)
}

/**
 * Same as [onGlobalLayoutOrNow] but the view is a receiver in the lambda used here.
 */
inline fun <T> T.applyOnGlobalLayoutOrNow(crossinline f: T.() -> Unit) where T : View {
    onGlobalLayoutOrNow(f)
}

fun Int.toStringWithSign(): String {
    val df = DecimalFormat("+#;-#")
    return df.format(this)
}

fun Int.toStringWithThousandsSeparator(): String {
    return String.format("%,d", this)
}

fun TypedArray.getStringOrEmpty(@StyleableRes index: Int) = this.getString(index) ?: ""

/**
 * Retrieves the TypedArray with custom attributes, calls [f] on it and recycles the TypedArray afterwards.
 * [baseAttrSet] is the base attrs retrieved from the view's constructor,
 * [customAttrs] is the custom attribute array, retrieved using R.styleable.{View name}.
 */
inline fun View.withCustomAttributes(baseAttrSet: AttributeSet?, @StyleableRes customAttrs: IntArray, f: TypedArray.() -> Unit) {
    with(this.context.obtainStyledAttributes(baseAttrSet, customAttrs)) {
        f(this)
        recycle()
    }
}

var TextView.textColorStateListResource: Int
    set(value) {
        val stateList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) resources.getColorStateList(value, context.theme)
        else resources.getColorStateList(value)
        this.setTextColor(stateList)
    }
    @Deprecated("Getter not supported")
    get() = throw UnsupportedOperationException("Getter not supported")

fun Activity.disableFullscreen() {
    window?.apply {
        addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }
}

fun Fragment.disableFullscreen() {
    activity?.disableFullscreen()
}

fun Activity.enableFullscreen() {
    window?.apply {
        addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
    }
}

fun Fragment.enableFullscreen() {
    activity?.enableFullscreen()
}

/**
 * Sets the keyboard's ime option to [EditorInfo.IME_ACTION_DONE] and triggers the [function] when the done button is pressed.
 */
inline fun EditText.onDonePressedOnKeyboard(crossinline function: (EditText) -> Unit) {
    imeOptions = EditorInfo.IME_ACTION_DONE
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            function(this)
            true
        } else false
    }
}

/**
 * Use when soft keyboard is needed in fullscreen mode.
 * Shows soft keyboard and temporarily disables fullscreen (because window resizing doesn't work in fullscreen mode).
 * [editTextToGainFocus] Optional [EditText] to request focus on. Use to be able to type directly into this field once the mode is toggled.
 * When finished using the keyboard, call [toggleKeyboardOff].
 * [rootLayout] If not null, fitsSystemWindows will be set to true on this layout to make resizing work.
 * If [titleBarView] is not null, temporarily sets [TitleBarView.ignoreCutout] on it to true.
 *
 * In a screen whose primary function is to enter text, this should be called in [Activity.onResume].
 *
 * Calls [keyboardConfigOn], then [showKeyboard].
 */
fun Activity.toggleKeyboardOn(
    editTextToGainFocus: EditText? = null,
    rootLayout: View? = null
) {
    keyboardConfigOn(rootLayout)
    showKeyboard(editTextToGainFocus)
}

/**
 * Closing counterpart to [toggleKeyboardOn]. See it's docs for explanation.
 * Hides the keyboard and enables fullscreen back.
 * [rootLayout] If not null, fitsSystemWindows will be set back to false on this view. When switching fitsSystemWindows,
 * the padding of the root layout gets reset. Use [rootLayoutTopPadding] and [rootLayoutBottomPadding] to specify how much padding the layout should
 * have on the bottom and on the top respectively, otherwise it will be set to 0.
 * If [titleBarView] is not null, resets its [TitleBarView.ignoreCutout] to false.
 *
 * In a screen whose primary function is to enter text, this should be called in [Activity.onPause].
 *
 * Calls [keyboardConfigOff], then [hideKeyboard].
 */
fun Activity.toggleKeyboardOff(
    rootLayout: View? = null,
    rootLayoutTopPadding: Int = 0,
    rootLayoutBottomPadding: Int = 0,
) {
    keyboardConfigOff(rootLayout, rootLayoutTopPadding, rootLayoutBottomPadding)
    hideKeyboard()
}

/**
 * @see [Activity.toggleKeyboardOn]
 */
fun Fragment.toggleKeyboardOn(
    editTextToGainFocus: EditText? = null,
    rootLayout: View? = null
) =
    activity?.toggleKeyboardOn(editTextToGainFocus, rootLayout)

/**
 * @see [Activity.toggleKeyboardOff]
 */
fun Fragment.toggleKeyboardOff(
    rootLayout: View? = null,
    rootLayoutTopPadding: Int = 0,
    rootLayoutBottomPadding: Int = 0
) =
    activity?.toggleKeyboardOff(
        rootLayout,
        rootLayoutTopPadding,
        rootLayoutBottomPadding
    )

/**
 * Use to prepare the configuration before showing the soft keyboard in full screen mode. Temporarily disables fullscreen mode so that
 * window resizing works. If [rootLayout] is not null, temporarily sets fitsSystemWindows to true on it.
 * If [titleBarView] is not null, temporarily sets [TitleBarView.ignoreCutout] on it to true.
 * Use [keyboardConfigOff] to revert the changes.
 */
fun Activity.keyboardConfigOn(rootLayout: View? = null) {
    rootLayout?.run {
        fitsSystemWindows = true
        requestApplyInsets()
    }
    disableFullscreen()
}

/**
 * Closing counterpart to [keyboardConfigOn].
 * Enables fullscreen back. If [rootLayout] is not null, sets fitsSystemWindows back to false, and resets its top and bottom padding to
 * [rootLayoutTopPadding] and [rootLayoutBottomPadding].
 * If [titleBarView] is not null, resets its [TitleBarView.ignoreCutout] to false.
 */
fun Activity.keyboardConfigOff(
    rootLayout: View? = null,
    rootLayoutTopPadding: Int = 0,
    rootLayoutBottomPadding: Int = 0,
) {
    rootLayout?.run {
        fitsSystemWindows = false
        setPaddingTop(rootLayoutTopPadding)
        setPaddingBottom(rootLayoutBottomPadding)
        requestApplyInsets()
    }
    enableFullscreen()
}

/**
 * @see [Activity.keyboardConfigOn]
 */
fun Fragment.keyboardConfigOn(rootLayout: View? = null) {
    activity?.keyboardConfigOn(rootLayout)
}

/**
 * @see [Activity.keyboardConfigOff]
 */
fun Fragment.keyboardConfigOff(
    rootLayout: View? = null,
    rootLayoutTopPadding: Int = 0,
    rootLayoutBottomPadding: Int = 0
) {
    activity?.keyboardConfigOff(
        rootLayout,
        rootLayoutTopPadding,
        rootLayoutBottomPadding
    )
}

/**
 * IMPORTANT: this method only hides the keyboard itself. For full keyboard functionality, including full screen handling, see [toggleKeyboardOff].
 */
fun Activity.hideKeyboard() {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    // Try to find a window token either from the currently focused view, or from the decor view.
    // If the token couldn't be found, do nothing. It is still better to have a ghost keyboard than crashing the app.
    (currentFocus?.windowToken ?: window?.decorView?.windowToken)?.let {
        inputMethodManager.hideSoftInputFromWindow(it, 0)
    }
}

/**
 * IMPORTANT: this method only shows the keyboard itself. For full keyboard functionality, including full screen handling, see [toggleKeyboardOn].
 */
fun Activity.showKeyboard(editTextToGainFocus: EditText? = null) {
    editTextToGainFocus?.requestFocus()
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

/**
 * IMPORTANT: this method only hides the keyboard itself. For full keyboard functionality, including full screen handling, see [toggleKeyboardOff].
 */
fun Fragment.hideKeyboard() {
    activity?.hideKeyboard()
}

/**
 * IMPORTANT: this method only shows the keyboard itself. For full keyboard functionality, including full screen handling, see [toggleKeyboardOn].
 */
fun Fragment.showKeyboard(editTextToGainFocus: EditText? = null) {
    activity?.showKeyboard(editTextToGainFocus)
}

/**
 * Returns the pixel height of the top cutout, or 0 if one of the components is null, cutout is not present, or if the API is smaller that 28.
 */
fun getTopCutoutOrZero(insets: WindowInsets?) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
        insets?.displayCutout?.safeInsetTop ?: 0
    else 0

/**
 * Sets the top margin of this view to [marginToAdd] plus the top cutout height.
 */
fun View.adjustTopMarginForCutout(marginToAdd: Int = 0) {
    this.setOnApplyWindowInsetsListener { v, insets ->
        v.setMarginTop(getTopCutoutOrZero(insets) + marginToAdd)
        insets
    }
    requestApplyInsets()
}

/**
 * Sets the top padding of this view to [paddingToAdd] plus the top cutout height.
 */
fun View.adjustTopPaddingForCutout(paddingToAdd: Int = 0) {
    this.setOnApplyWindowInsetsListener { v, insets ->
        v.setPaddingTop(getTopCutoutOrZero(insets) + paddingToAdd)
        insets
    }
    requestApplyInsets()
}

fun SnapHelper.detachFromRecyclerView() {
    attachToRecyclerView(null)
}

fun RecyclerView.Adapter<*>.isEmpty() = itemCount == 0

/**
 * Converts this list to a mutable list if not null, or to an empty mutable list if null. Used for recycler implementations.
 */
fun <T> List<T>?.toMutableListOrEmptyIfNull() = this?.toMutableList() ?: mutableListOf()

/**
 * Creates and attaches a horizontal [LinearLayoutManager] to this [RecyclerView].
 */
fun RecyclerView.setHorizontalLM(reversedLayout: Boolean = false) {
    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, reversedLayout)
}

/**
 * Creates and attaches a vertical [LinearLayoutManager] to this [RecyclerView].
 */
fun RecyclerView.setVerticalLM(reversedLayout: Boolean = false) {
    layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, reversedLayout)
}

/**
 * Shows this dialog and uses the class name as the tag.
 */
fun DialogFragment.show(fragmentManager: FragmentManager) {
    show(fragmentManager, this::class.java.simpleName)
}

fun Fragment.showDialog(dialog: DialogFragment) {
    dialog.show(activity!!.supportFragmentManager)
}

fun Activity.showDialog(dialog: DialogFragment) {
    this as FragmentActivity
    dialog.show(supportFragmentManager)
}

fun Fragment.toast(message: String) = Toast.makeText(context, message, Toast.LENGTH_LONG).show()

fun Context.toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()

fun Context.getColour(@ColorRes id: Int): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        resources.getColor(id, theme)
    } else {
        resources.getColor(id)
    }
}

/**
 * Change state of view from View.GONE to View.VISIBLE using alpha animation
 */
fun View.showWithAlphaAnimation(animationDuration: Long = 400, startDelay: Long = 0): ViewPropertyAnimator {
    // Set the content view to 0% opacity but visible, so that it is visible
    // (but fully transparent) during the animation.
    alpha = 0f
    visibility = View.VISIBLE

    // Animate the content view to 100% opacity, and clear any animation
    // listener set on the view.
    return animate().apply {
        alpha(1f)
        duration = animationDuration
        this.startDelay = startDelay
    }
}

/**
 * Animate TextView color from [fromColorValue] to [toColorValue]
 */
fun TextView.changeTextColorAnimated(fromColorValue: Int, toColorValue: Int) {
    val colorAnim = ObjectAnimator.ofInt(this, "textColor", fromColorValue, toColorValue)
    colorAnim.setEvaluator(ArgbEvaluator())
    colorAnim.start()
}

/**
 * Returns color from resources using the most simple compatible way
 */
fun Context.gimmeColor(colorRes: Int) = ContextCompat.getColor(this, colorRes)

/**
 * Run [hustle] lambda on UI thread with some [delay]
 */
fun doLaterOnUI(delay: Long, hustle: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed(hustle, delay)
}

/**
 * Workaround for when a view accessed using kotlin synthetics is saved into a lambda when the lambda is initialized, not when it is executed.
 * The view is null when the lambda initializes itself, so by using getThis().vExampleView.doStuff() in the lambda, it is forced to retain
 * a reference to this object and ask for "this" returned from this function on runtime, which will then return the non-null view.
 */
fun View.getThis(): View = this

fun EditText.setMaxLength(maxChars: Int) {
    filters = filters.toMutableList().apply {
        removeAll { it is InputFilter.LengthFilter }
        add(InputFilter.LengthFilter(maxChars))
    }.toTypedArray()
}
