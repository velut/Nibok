package com.nibokapp.nibok.ui.fragment.publish

import android.view.View
import android.widget.ArrayAdapter
import com.nibokapp.nibok.R
import com.nibokapp.nibok.extension.afterTextChanged
import com.nibokapp.nibok.extension.toSafeFloat
import com.nibokapp.nibok.ui.filter.getPriceLeadingZerosFilter
import com.nibokapp.nibok.ui.filter.getPriceLengthFilter
import com.nibokapp.nibok.ui.fragment.publish.common.BasePublishFragment
import kotlinx.android.synthetic.main.fragment_publish_input_insertion_data.*

/**
 * Publishing fragment managing input data about the insertion,
 * such as price and book wear condition.
 */
class InputInsertionData : BasePublishFragment() {
    
    companion object {
        private val TAG = InputInsertionData::class.java.simpleName
    }

    private val priceText: String
        get() = inputInsertionBookPrice.getStringText()

    private val bookWearId: Int
        get() = inputInsertionBookCondition.selectedItemPosition


    override fun getFragmentLayout(): Int = R.layout.fragment_publish_input_insertion_data

    override fun getInputContainer(): View = inputInsertionDetailsContainer

    override fun triggerInputsUpdate() {
        handlePriceChange()
    }

    override fun hasValidData(): Boolean {
        return !inputInsertionBookPriceLayout.hasError()
    }

    override fun saveData() {
        publishProcessManager.apply {
            setPrice(priceText.toSafeFloat())
            setWearCondition(bookWearId)
        }
    }

    override fun setupInput() {
        setupPriceInput()
        setupBookWearInput()
    }

    private fun setupPriceInput() {
        inputInsertionBookPrice.apply {
            filters = arrayOf(getPriceLengthFilter(this),
                    getPriceLeadingZerosFilter(this))
            afterTextChanged { handlePriceChange() }
        }
    }

    private fun setupBookWearInput() {
        inputInsertionBookCondition.adapter = getSpinnerAdapter()
    }

    private fun handlePriceChange() {
        with(inputInsertionBookPriceLayout) {
            if (priceText.isBlank()) {
                setInputError(R.string.error_required_price)
            } else {
                removeInputError()
            }
        }
    }

    private fun getSpinnerAdapter(): ArrayAdapter<CharSequence> {
        val spinnerAdapter = ArrayAdapter.createFromResource(context,
                R.array.book_wear_condition_array, android.R.layout.simple_spinner_item)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return spinnerAdapter
    }
}