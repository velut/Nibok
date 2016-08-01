package com.nibokapp.nibok.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nibokapp.nibok.R
import com.nibokapp.nibok.ui.fragment.common.BaseFragment

/**
 * Fragment managing the books being sold the user.
 */
class SellingFragment : BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.selling_fragment, container, false)
    }
}