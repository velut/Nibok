package com.nibokapp.nibok.ui.fragment.common

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.nibokapp.nibok.R
import org.jetbrains.anko.toast

open class BaseFragment : Fragment() {

    companion object {
        val TAG = BaseFragment::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.toolbar_menu, menu)

        val searchView = menu?.findItem(R.id.searchAction)?.actionView as SearchView

        searchView.setOnQueryTextListener(
                object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        Log.i(TAG, "Search submit: $query")
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        Log.i(TAG, "Search change to: $newText")
                        return false
                    }
                }
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.searchAction -> handleSearchAction()
            R.id.refreshAction -> handleRefreshAction()
            R.id.backToTopAction -> handleBackToTopAction()
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

    open fun handleBackToTopAction() {
        context.toast("Back to top")
    }

    private fun handleSettingsAction() {
        context.toast("Settings")
    }

    private fun handleUnknownAction(item: MenuItem) {
        super.onOptionsItemSelected(item)
        context.toast("Unknown menu action")
    }

}


