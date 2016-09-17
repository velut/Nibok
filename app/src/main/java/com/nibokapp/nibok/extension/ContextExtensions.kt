package com.nibokapp.nibok.extension

import android.content.Context
import android.content.res.Configuration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import com.nibokapp.nibok.R

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

/**
 * Get a LinearLayoutManager based on the current screen size in dp.
 * If the size is >= 600dp (tablet) a GridLayoutManager with the given number of columns
 * is returned.
 * If the size is < 600dp (mobile) a LinearLayoutManager is returned.
 *
 * @param gridColumns the number of columns for the GridLayoutManager. Default is 2 columns.
 *
 * @return a LinearLayoutManager or a GridLayoutManager
 */
fun Context.getDpBasedLinearLayoutManager(gridColumns: Int = 2) : LinearLayoutManager {
    val isTablet = resources.getBoolean(R.bool.isTablet)
    if (isTablet) {
        return GridLayoutManager(this, gridColumns)
    } else {
        return LinearLayoutManager(this)
    }
}