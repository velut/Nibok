package com.nibokapp.nibok.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nibokapp.nibok.R
import com.nibokapp.nibok.extension.inflate
import com.nibokapp.nibok.ui.fragment.common.BaseFragment

/**
 * Fragment managing the messaging between users.
 */
class MessagesFragment : BaseFragment() {

    companion object {
        private val TAG = MessagesFragment::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_messages)
    }

    override fun handleRefreshAction() {
        Log.i(TAG, "Refreshing")
    }

    override fun handleBackToTopAction() {
        Log.i(TAG, "Going back to top")
    }

    override fun handleOnQueryTextSubmit(query: String) {
        Log.i(TAG, "TODO")
    }

    override fun handleOnQueryTextChange(query: String) {
        Log.i(TAG, "TODO")
    }

    override fun getSearchHint() : String = getString(R.string.search_hint_message)
}