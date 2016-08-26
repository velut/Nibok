package com.nibokapp.nibok.extension

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.ScaleAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.nibokapp.nibok.R

/**
 * View extensions file.
 *
 * Here are located extension functions defined over views.
 *
 */


/**
 * Inflate a layout directly in a view.
 *
 * @param layoutId the layout to inflate
 */
fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)
}

/**
 * Load an image directly in a view.
 *
 * @param imageLocation the location of the image, it can be an url a uri or a path
 */
fun ImageView.loadImg(imageLocation: String) {
    Glide.with(context)
            .load(imageLocation)
            .placeholder(R.drawable.book_placeholder_image)
            .error(R.drawable.book_placeholder_image)
            .into(this)
}

/**
 * Animate a scaling with the given parameters.
 *
 */
fun View.animateScaling(xStart: Float, xEnd: Float, yStart: Float, yEnd: Float, duration: Long = 250) {
    val animation = ScaleAnimation(
            xStart, xEnd,
            yStart, yEnd,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f)
    animation.fillAfter = true
    animation.duration = duration
    animation.interpolator = DecelerateInterpolator(0.5f)
    this.startAnimation(animation)
}

fun View.animateScaleUp() {
    this.animateScaling(1f, 1.618f, 1f, 1.618f)
}

fun View.animateScaleDown() {
    this.animateScaling(1.618f, 1f, 1.618f, 1f)
}

/**
 * A scale up followed by a scale down animation to be called directly by a View.
 */
fun View.animateBounce() {
    this.animateScaleUp()
    this.animateScaleDown()
}

/**
 * Get the short name of a view.
 */
fun View.getName() = this.toString().substringAfter("app:id/").substringBefore('}')

/**
 * Hide the soft keyboard in this view.
 *
 * @param context the context needed to get system services
 */
fun View.hideSoftKeyboard(context: Context) {
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE)
            as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(this.windowToken, 0)
}