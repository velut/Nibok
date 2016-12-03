package com.nibokapp.nibok.ui.fragment.main.common

/**
 * Interface for fragments shown in the view pager.
 *
 * A visible fragment is just a normal fragment which needs to update its view when it becomes visible.
 * A fragment becomes visible when it is selected by the viewpager.
 *
 */
interface VisibleFragment {

    /**
     * What to do when the fragment becomes visible.
     *
     * This function is needed because the viewpager does not call onResume() when preloaded fragments become visible.
     */
    fun onBecomeVisible()

    /**
     * What to do when the fragment becomes invisible.
     */
    fun onBecomeInvisible()

    /**
     * Check if the fragment is visible or not.
     *
     * @return true if the fragment is visible, false if it is not visible
     */
    fun isFragmentVisible(): Boolean
}
