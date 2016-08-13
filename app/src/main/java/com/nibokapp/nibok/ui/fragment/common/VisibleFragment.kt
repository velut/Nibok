package com.nibokapp.nibok.ui.fragment.common

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
}
