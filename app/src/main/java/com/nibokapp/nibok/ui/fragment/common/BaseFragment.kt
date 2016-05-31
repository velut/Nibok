package com.nibokapp.nibok.ui.fragment.common

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.nibokapp.nibok.R
import org.jetbrains.anko.toast

open class BaseFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.searchAction -> handleSearchAction()
            R.id.refreshAction -> handleRefreshAction()
            R.id.settingsAction -> handleSettingsAction()
            else -> handleUnknownAction(item)
        }
        return true
    }

    open fun handleSearchAction() {
        context.toast("Search")
    }

    open fun handleRefreshAction() {
        context.toast("Refresh")
    }

    private fun handleSettingsAction() {
        context.toast("Settings")
    }

    private fun handleUnknownAction(item: MenuItem) {
        super.onOptionsItemSelected(item)
        context.toast("Unknown menu action")
    }

}


