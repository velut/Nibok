package com.nibokapp.nibok.ui.fragment.publish.common

import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.afollestad.materialdialogs.MaterialDialog
import com.nibokapp.nibok.R
import com.nibokapp.nibok.domain.model.publish.BookData
import com.nibokapp.nibok.domain.model.publish.InsertionData
import com.nibokapp.nibok.extension.hideKeyboardListener
import com.nibokapp.nibok.extension.inflate
import org.jetbrains.anko.findOptional

/**
 * BasePublishFragment collects common features of the fragments
 * that constitute the insertion publishing process.
 */
abstract class BasePublishFragment : Fragment() {

    companion object {
        private val TAG = BasePublishFragment::class.java.simpleName
    }

    /**
     * A PublishProcessManager should know how to handle page navigation
     * for the publishing process.
     * It should also know how to save data collected from inputs.
     */
    interface PublishProcessManager {

        /**
         * Go to the next screen in the publishing process.
         */
        fun nextScreen()

        /**
         * Go to the previous screen in the publishing process.
         */
        fun prevScreen()

        /**
         * Get a copy of the current insertion data.
         *
         * @return the current InsertionData
         */
        fun getInsertionData(): InsertionData

        /**
         * Reset data eventually set for this insertion.
         */
        fun resetData()

        /**
         * Set the ISBN code for the book in the insertion to be published
         *
         * @param isbn the isbn code
         */
        fun setIsbn(isbn: String)

        /**
         * Check if a correct ISBN code is already set.
         *
         * @return true if a code is set, false otherwise
         */
        fun isIsbnSet(): Boolean

        /**
         * Set the data about the book.
         */
        fun setBookData(data: BookData)

        /**
         * Set the price for the book.
         * @param price the price for the book
         */
        fun setPrice(price: Float)

        /**
         * Set the wear condition for the book.
         *
         * @param conditionId the id of the wear condition of the book
         */
        fun setWearCondition(conditionId: Int)

        /**
         * Set the list of pictures used in the insertion.
         *
         * @param pictures the list of pictures sources
         */
        fun setPictures(pictures: List<String>)
    }

    /**
     * Get the layout of the fragment.
     *
     * @return an Int referring to the layout's resource
     */
    abstract fun getFragmentLayout(): Int

    /**
     * Get the root View that contains input forms.
     *
     * @return the root View containing the input forms
     */
    abstract fun getInputContainer(): View

    /**
     * Trigger an update that checks inputs.
     * This is done to refresh input layouts before checking if data is valid.
     */
    open fun triggerInputsUpdate(): Unit = Unit

    /**
     * Check if the inputs in the fragment all contain valid data.
     *
     * @return true if all the data from the inputs is valid, false otherwise
     */
    abstract fun hasValidData(): Boolean

    /**
     * Save insertion data held by the fragment.
     */
    abstract fun saveData(): Unit

    /**
     * Setup the inputs.
     */
    abstract fun setupInput(): Unit

    /**
     * Get the list of dialogs used by a fragment.
     * This is done in order to automatically dismiss them at onPause().
     *
     * @return a list of dialogs
     */
    open fun getDialogs(): List<MaterialDialog?> = emptyList()


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(getFragmentLayout())
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addOnTouchKeyboardHidingForInputContainer()
        setupNavigation(view)
        setupInput()
        getInputContainer().requestFocus()
    }

    override fun onPause() {
        super.onPause()
        getDialogs().forEach { it?.dismiss() }
    }


    protected fun nextScreen() {
        triggerInputsUpdate()
        if (!hasValidData()) return
        saveData()
        getPublishManager().nextScreen()
    }

    protected fun prevScreen() {
        getPublishManager().prevScreen()
    }

    protected fun getPublishManager(): PublishProcessManager {
        return activity as? PublishProcessManager ?:
                throw IllegalStateException("Host activity must implement PublishProcessManager")
    }

    /**
     * Set an input error on this TextInputLayout.
     *
     * @param errorResource the id of the string resource describing the error
     * @param requestFocus true if the inputs has to request focus on error. True by default
     */
    protected fun TextInputLayout.setInputError(errorResource: Int, requestFocus: Boolean = true) {
        this.apply {
            error = getString(errorResource)
            if (requestFocus) requestFocus()
        }
    }

    /**
     * Remove input errors from this TextInputLayout.
     */
    protected fun TextInputLayout.removeInputError() {
        this.error = null
    }

    /**
     * Check if this TextInputLayout has any errors.
     */
    protected fun TextInputLayout.hasError() = this.error != null

    /**
     * Get the String of text from this EditText.
     *
     * @param trimmed true if the text has to be trimmed. True by default
     *
     * @return a String containing the text in this EditText
     */
    protected fun EditText.getStringText(trimmed: Boolean = true): String {
        val text = this.text.toString()
        return if (trimmed) text.trim() else text
    }

    private fun addOnTouchKeyboardHidingForInputContainer() {
        getInputContainer().setOnTouchListener {
            view, motionEvent ->
            hideKeyboardListener(motionEvent, view, context)
        }
    }

    private fun setupNavigation(view: View?) {
        val btnPrev = view?.findOptional<Button>(R.id.btnPrev)
        val btnNext = view?.findOptional<Button>(R.id.btnNext)
        btnPrev?.setOnClickListener { prevScreen() }
        btnNext?.setOnClickListener { nextScreen() }
    }
}