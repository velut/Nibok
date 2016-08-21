package com.nibokapp.nibok.ui.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.nibokapp.nibok.R
import com.nibokapp.nibok.ui.fragment.LatestFragment
import com.nibokapp.nibok.ui.fragment.MessagesFragment
import com.nibokapp.nibok.ui.fragment.SavedFragment
import com.nibokapp.nibok.ui.fragment.SellingFragment
import com.nibokapp.nibok.ui.fragment.common.VisibleFragment
import kotlinx.android.synthetic.main.activity_main.*

/**
 * The main activity of the application.
 *
 * It hosts the ViewPager with the four main fragments:
 *  LatestFragment: the fragment for the feed of latest insertions published
 *  SavedFragment: the fragment for the list of insertions bookmarked by the user
 *  SellingFragment: the fragment for the list of insertions published by the user
 *  MessagesFragment: the fragment for the list of messages exchanged with other users
 */
class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val fragments = mapOf<String, Fragment>(
                getString(R.string.latest_tab) to LatestFragment(),
                getString(R.string.saved_tab) to SavedFragment(),
                getString(R.string.selling_tab) to SellingFragment(),
                getString(R.string.messages_tab) to MessagesFragment()
        )

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
        viewPager.addOnPageChangeListener(
                object : ViewPager.OnPageChangeListener {
                    override fun onPageSelected(position: Int) {
                        val fragment = adapter.getItem(position) as? VisibleFragment
                        Log.d("$TAG ViewPager", "Fragment $fragment at position $position became visible")
                        fragment?.onBecomeVisible()

                        // Alert other fragments that they became invisible
                        val otherFragmentsPositions = (0..adapter.count - 1).filter { it != position }
                        otherFragmentsPositions.forEach { (adapter.getItem(it) as? VisibleFragment)?.onBecomeInvisible() }
                    }

                    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    }

                    override fun onPageScrollStateChanged(state: Int) {
                    }
                }
        )
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
