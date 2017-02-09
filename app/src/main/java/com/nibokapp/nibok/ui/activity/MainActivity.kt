package com.nibokapp.nibok.ui.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import com.nibokapp.nibok.R
import com.nibokapp.nibok.extension.onPageSelected
import com.nibokapp.nibok.ui.fragment.*
import kotlinx.android.synthetic.main.activity_main.*

/**
 * MainActivity.
 *
 * The main activity of the application.
 *
 * It hosts the ViewPager with the four main fragments:
 *  FeedFragment: the fragment for the feed of latest insertions published
 *  BookmarkFragment: the fragment for the list of insertions bookmarked by the user
 *  PublishedFragment: the fragment for the list of insertions published by the user
 *  ConversationFragment: the fragment for the list of messages exchanged with other users
 */
class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    /**
     * The map of Tab title -> Fragment for the fragments that make up the main view of the application.
     */
    private val fragments: Map<String, Fragment> by lazy {
        mapOf<String, Fragment>(
                getString(R.string.main_tab_new) to FeedFragment(),
                getString(R.string.main_tab_saved) to BookmarkFragment(),
                getString(R.string.main_tab_published) to PublishedFragment(),
                getString(R.string.main_tab_messages) to ConversationFragment()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setupViewPager(viewPager, fragments)
        tabLayout.setupWithViewPager(viewPager)
    }



    /**
     * Sets up the ViewPager linking it to the adapter and adding the given fragments.
     *
     * @param viewPager the ViewPager defined in the layout
     * @param fragments the fragments representing the pages to display in the viewpager
     */
    fun setupViewPager(viewPager: ViewPager, fragments: Map<String, Fragment>) {

        val adapter = ViewPagerAdapter(supportFragmentManager, fragments)
        viewPager.adapter = adapter
        viewPager.onPageSelected {
            position ->
            // Alert selected fragment
            val selectedFragment = adapter.getItem(position)
            if (selectedFragment is ViewPagerFragment) selectedFragment.onSelected()

            // Alert other fragments
            val otherFragmentsPositions = (0..adapter.count - 1).filter { it != position }
            val otherFragments = otherFragmentsPositions.map { adapter.getItem(it) }
            val unselectedFragments = otherFragments.filterIsInstance<ViewPagerFragment>()
            unselectedFragments.forEach { it.onUnselected() }
        }
    }

    class ViewPagerAdapter(val fm: FragmentManager, val fragments: Map<String, Fragment>) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment? {
            // If the fragment manager already has a fragment with the given tag
            // then return the stored fragment instead of a new instance
            // This solves the problems of duplicate fragments and blank views after rotation
            // caused by the viewpager
            val foundFragment = fm.findFragmentByTag(getFragmentTagForPosition(position))
            return foundFragment ?: fragments.values.elementAt(position)
        }

        override fun getCount(): Int = fragments.size

        override fun getPageTitle(position: Int): CharSequence? {
            val fragmentsNames = fragments.keys
            return fragmentsNames.elementAt(position)
        }

        /**
         * Return the tag given by the adapter to the fragment.
         *
         * @return the string representing the tag of the fragment at the given position
         */
        fun getFragmentTagForPosition(position: Int) = "android:switcher:${R.id.viewPager}:$position"
    }
}
