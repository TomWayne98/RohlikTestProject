package cz.tom.wayne.extension

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.util.TypedValue

fun Int.toDrawable(ctx: Context): Drawable = ctx.getDrawable(this)
    ?: throw IllegalArgumentException("The drawable for the given resource couldn't be loaded.")

fun Int.toColor(ctx: Context): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        ctx.resources.getColor(this, ctx.theme)
    } else {
        ctx.resources.getColor(this)
    }
}

// support html tags in a string f.e. for bold text
fun String.toHtml(): Spanned {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        return Html.fromHtml(this, Html.FROM_HTML_MODE_COMPACT)
    } else {
        return Html.fromHtml(this)
    }
}

fun Resources.dpToPx(dp: Int): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        displayMetrics
    )
}

fun Context.getQuantityString(res: Int, quantity: Int): String =
    resources.getQuantityString(res, quantity, quantity)

fun Resources.dpToPxInt(dp: Int): Int = this.dpToPx(dp).toInt()
