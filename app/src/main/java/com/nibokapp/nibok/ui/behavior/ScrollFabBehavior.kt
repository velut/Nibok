package com.nibokapp.nibok.ui.behavior

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.v4.view.ViewCompat
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View

@Suppress("unused", "UNUSED_PARAMETER")
class ScrollFabBehavior(context: Context, attrs: AttributeSet) : FloatingActionButton.Behavior() {

    private var active = true

    companion object {
        private val TAG = ScrollFabBehavior::class.java.simpleName
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout?, child: FloatingActionButton?, dependency: View?): Boolean {
        val recyclerView = dependency as? RecyclerView

        active = recyclerView?.let {
            it.visibility == View.VISIBLE && it.childCount != 0
        } ?: false
        return super.onDependentViewChanged(parent, child, dependency)
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

        if (!active) return

        child?.let {
            // If scrolled down and Fab still visible hide it
            if (dy > 0 && it.visibility == View.VISIBLE) {
                it.hide()
            } else // else if scrolled up and Fab not visible show it
                if (dy < 0 && it.visibility != View.VISIBLE) {
                it.show()
            }
        }
    }
}