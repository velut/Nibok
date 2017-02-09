package com.nibokapp.nibok.ui.fragment.main.common

/**
 * Interface for fragments hosted in a ViewPager.
 *
 * Used to alert fragments of when they become visible or become hidden in the viewpager.
 */
interface ViewPagerFragment {

    /**
     * Called when a fragment is selected in the viewpager becoming the currently visible one.
     */
    fun onSelected()

    /**
     * Called when a fragment previously selected in the viewpager is no longer the currently visible one.
     */
    fun onUnselected()

}