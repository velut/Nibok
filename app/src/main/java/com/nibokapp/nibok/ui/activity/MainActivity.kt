package com.nibokapp.nibok.ui.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import com.nibokapp.nibok.R
import com.nibokapp.nibok.ui.fragment.LatestFragment
import com.nibokapp.nibok.ui.fragment.MessagesFragment
import com.nibokapp.nibok.ui.fragment.SavedFragment
import com.nibokapp.nibok.ui.fragment.SellingFragment
import com.nibokapp.nibok.ui.fragment.common.BaseFragment
import com.nibokapp.nibok.ui.fragment.common.VisibleFragment
import kotlinx.android.synthetic.main.activity_main.*

/**
 * The main activity of the application.
 *
 * It sets up the ViewPager with the four main fragments.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        setupViewPager(viewPager)
        tabLayout.setupWithViewPager(viewPager)
    }

    /**
     * Sets up the ViewPager adding the four main fragments and the adapter.
     *
     * @param viewPager the ViewPager defined in the layout
     */
    fun setupViewPager(viewPager: ViewPager) {
        val fragments = mapOf(
                getString(R.string.latest_tab) to LatestFragment(),
                getString(R.string.saved_tab) to SavedFragment(),
                getString(R.string.selling_tab) to SellingFragment(),
                getString(R.string.messages_tab) to MessagesFragment()
        )
        val adapter = ViewPagerAdapter(supportFragmentManager, fragments)
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(
                object : ViewPager.OnPageChangeListener {
                    override fun onPageSelected(position: Int) {
                        val fragment = adapter.getItem(position) as? VisibleFragment
                        fragment?.onBecomeVisible()
                    }

                    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    }

                    override fun onPageScrollStateChanged(state: Int) {
                    }
                }
        )
    }

    class ViewPagerAdapter(fm: FragmentManager, val fragments: Map<String, BaseFragment>) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment? {
            val fragmentsInstances = fragments.values
            return fragmentsInstances.elementAt(position)
        }

        override fun getCount(): Int = fragments.size

        override fun getPageTitle(position: Int): CharSequence? {
            val fragmentsNames = fragments.keys
            return fragmentsNames.elementAt(position)
        }

    }
}
