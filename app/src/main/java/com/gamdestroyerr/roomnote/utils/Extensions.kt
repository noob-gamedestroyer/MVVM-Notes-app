package com.gamdestroyerr.roomnote.utils

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.net.Uri
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

fun View.hideKeyboard() =
    (context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
        .hideSoftInputFromWindow(windowToken, HIDE_NOT_ALWAYS)

fun Context.loadImage(
    uri: Uri?,
    image: ImageView
) = Glide.with(this)
    .load(uri)
    .override(3200, 2400)
    .thumbnail(0.5f)
    .transition(DrawableTransitionOptions.withCrossFade(500))
    .into(image)

fun Context.loadHiRezThumbnail(
    uri: Uri?,
    image: ImageView
) = Glide.with(this)
    .load(uri)
    .override(600, 600)
    .thumbnail(0.01f)
    .transition(DrawableTransitionOptions.withCrossFade(500))
    .into(image)