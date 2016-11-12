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
import com.nibokapp.nibok.extension.hideKeyboardListener
import com.nibokapp.nibok.extension.inflate
import org.jetbrains.anko.findOptional

/**
 * BasePublishFragment collects common features of the fragments
 * that constitute the insertion publishing process.
 */
abstract class BasePublishFragment : Fragment() {

    /**
     * A PublishScreenManager should know how to handle page navigation
     * for the publishing process.
     */
    interface PublishScreenManager {

        /**
         * Go to the next screen in the publishing process.
         */
        fun nextScreen()

        /**
         * Go to the previous screen in the publishing process.
         */
        fun prevScreen()
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
     * Check if the inputs in the fragment all contain valid data.
     *
     * @return true if all the data from the inputs is valid, false otherwise
     */
    abstract fun hasValidData(): Boolean

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
    }

    override fun onPause() {
        super.onPause()
        getDialogs().forEach { it?.dismiss() }
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

    private fun nextScreen() {
        if (!hasValidData()) return
        // TODO save input data
        getScreenManager().nextScreen()
    }

    private fun prevScreen() {
        getScreenManager().prevScreen()
    }

    private fun getScreenManager(): PublishScreenManager {
        return activity as? PublishScreenManager ?:
                throw IllegalStateException("Host activity must implement PublishScreenManager " +
                        "to handle publishing process screens")
    }
}