package com.nibokapp.nibok.extension

import android.content.Context
import android.content.res.Configuration

/**
 * Extension functions related to Android and device configuration.
 */

/**
 * Check if orientation is portrait.
 *
 * @return true if orientation is portrait, false otherwise
 */
fun Context.isOrientationPortrait(): Boolean =
    this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

/**
 * Check if orientation is landscape.
 *
 * @return true if orientation is landscape, false otherwise
 */
fun Context.isOrientationLandscape(): Boolean =
    this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE