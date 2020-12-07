package com.gamdestroyerr.roomnote.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS
import android.widget.ImageView
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.res.use
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.FutureTarget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

fun View.hideKeyboard() =
    (context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
        .hideSoftInputFromWindow(windowToken, HIDE_NOT_ALWAYS)

fun Context.loadHiRezThumbnail(
    uri: Uri?,
    image: ImageView
) = Glide.with(this)
    .load(uri)
    .override(500, 500)
    .diskCacheStrategy(DiskCacheStrategy.ALL)
    .thumbnail(0.1f)
    .transition(DrawableTransitionOptions.withCrossFade(200))
    .into(image)

suspend fun Context.asyncImageLoader(
    uri: Uri?,
    image: ImageView,
    job: CoroutineScope,
) {
    val bitmap = job.async(Dispatchers.IO, CoroutineStart.DEFAULT) {
        val futureTarget: FutureTarget<Bitmap> = Glide.with(this@asyncImageLoader)
            .asBitmap()
            .load(uri)
            .submit(1500, 1500)
        return@async futureTarget.get()
    }
    try {
        Glide.with(this)
            .load(bitmap.await())
            .thumbnail(0.01f)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .transition(DrawableTransitionOptions.withCrossFade(200))
            .into(image)
    } catch (e: IllegalArgumentException) {
        Log.e("asyncImageLoader", e.stackTraceToString())
    }
}
/**
 * Retrieve a color from the current [android.content.res.Resources.Theme].
 */
@ColorInt
@SuppressLint("Recycle")
fun Context.themeColor(
    @AttrRes themeAttrId: Int
): Int {
    return obtainStyledAttributes(
        intArrayOf(themeAttrId)
    ).use {
        it.getColor(0, Color.MAGENTA)
    }
}

fun Context.shortToast(message: String?) = makeText(this, message, LENGTH_SHORT).show()