package com.nibokapp.nibok.ui.fragment

/**
 * Interface for fragments hosted in a ViewPager
 */
interface ViewPagerFragment {

    /**
     * Called when a fragment was selected in the viewpager and is the currently visible one.
     */
    fun onSelected()

}