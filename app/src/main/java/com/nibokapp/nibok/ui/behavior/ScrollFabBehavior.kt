package com.nibokapp.nibok.ui.behavior

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.util.Log
import android.view.View

@Suppress("unused", "UNUSED_PARAMETER")
class ScrollFabBehavior(context: Context, attrs: AttributeSet) : FloatingActionButton.Behavior() {

    companion object {
        private val TAG = ScrollFabBehavior::class.java.simpleName
    }

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout?, child: FloatingActionButton?,
                                     directTargetChild: View?, target: View?, nestedScrollAxes: Int): Boolean {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL ||
                super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes)
    }

    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout?,
                                   child: FloatingActionButton?, target: View?,
                                   dx: Int, dy: Int, consumed: IntArray) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed)

        child?.let {
            // If scrolled down and Fab still visible hide it
            if (dy > 0 && it.visibility == View.VISIBLE) {
                Log.d(TAG, "Hiding Fab button")
                it.hide()
            } else // else if scrolled up and Fab not visible show it
                if (dy < 0 && it.visibility != View.VISIBLE) {
                Log.d(TAG, "Showing Fab button")
                it.show()
            }
        }
    }
}