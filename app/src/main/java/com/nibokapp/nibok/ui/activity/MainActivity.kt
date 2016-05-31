package com.nibokapp.nibok.ui.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import com.nibokapp.nibok.R
import com.nibokapp.nibok.ui.fragment.LatestFragment
import com.nibokapp.nibok.ui.fragment.MessagesFragment
import com.nibokapp.nibok.ui.fragment.SavedFragment
import com.nibokapp.nibok.ui.fragment.SellingFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        setupViewPager(viewPager)
        tabLayout.setupWithViewPager(viewPager)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    fun setupViewPager(viewPager: ViewPager) {
        val fragments = mapOf(
                "Latest" to LatestFragment(),
                "Saved" to SavedFragment(),
                "Selling" to SellingFragment(),
                "Messages" to MessagesFragment()
        )
        val adapter = ViewPagerAdapter(supportFragmentManager, fragments)
        viewPager.adapter = adapter
    }

    class ViewPagerAdapter(fm: FragmentManager, val fragments: Map<String, Fragment>) : FragmentPagerAdapter(fm) {

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
