package com.nibokapp.nibok.extension

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.nibokapp.nibok.R

/**
 * Extensions file.
 *
 * Here are located extension functions defined for convenience.
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
 * @param imageUrl the url of the image
 */
fun ImageView.loadImg(imageUrl: String) {
    if (TextUtils.isEmpty(imageUrl)) {
        Glide.with(context).load(R.mipmap.ic_launcher).into(this)
    } else {
        Glide.with(context).load(imageUrl).into(this)
    }
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