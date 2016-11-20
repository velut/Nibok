package com.nibokapp.nibok.ui.fragment.publish

import android.os.Bundle
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.nibokapp.nibok.R
import com.nibokapp.nibok.domain.model.publish.InsertionData
import com.nibokapp.nibok.extension.toCurrency
import com.nibokapp.nibok.ui.fragment.publish.common.BasePublishFragment
import kotlinx.android.synthetic.main.fragment_publish_finalize_insertion.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Publishing fragment that provides a summary of previously inserted data
 * letting the user review his input before publishing the insertion.
 */
class FinalizeInsertion : BasePublishFragment() {

    private var progressDialog: MaterialDialog? = null
    private var successDialog: MaterialDialog? = null
    private var errorDialog: MaterialDialog? = null


    override fun getFragmentLayout(): Int = R.layout.fragment_publish_finalize_insertion

    override fun getInputContainer(): View = infoRecapContainer

    override fun getDialogs(): List<MaterialDialog?> = listOf(
            progressDialog, successDialog, errorDialog
    )

    override fun hasValidData(): Boolean = true

    override fun saveData() = Unit

    override fun setupInput() {
        setupPublishButton()
    }


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindInsertionData()
    }

    private fun bindInsertionData() {
        val insertionData = getPublishManager().getInsertionData()

        if (!insertionData.isPublishable()) return

        with(insertionData) {
            // Insertion details (Price, Book wear condition)
            insertionBookPrice.text = bookPrice.toCurrency()
            val wearConditions = resources.getStringArray(R.array.book_wear_condition_array)
            val bookWearCondition = wearConditions.getOrNull(bookConditionId)
            bookWearCondition?.let { insertionBookCondition.text = it }

            // Book details (Title, Authors, Publishing Year, Publisher, ISBN)
            with(bookData) {
                detailBookTitle.text = title
                detailBookAuthorsHint.text = resources.getQuantityString(R.plurals.book_author, authors.size)
                detailBookAuthors.text = authors.joinToString() // Comma separated authors
                detailBookYear.text = year.toString()
                detailBookPublisher.text = publisher
                if (isbn.isNotBlank()) {
                    detailBookISBN.text = isbn
                }
            }
        }
    }

    private fun InsertionData.isPublishable(): Boolean = with(bookData) {
            title.isNotBlank() && authors.isNotEmpty() && publisher.isNotBlank()
    }

    private fun setupPublishButton() {
        btnPublishInsertion.setOnClickListener {
            publishInsertion()
        }
    }

    private fun publishInsertion() {
        val insertionData = getPublishManager().getInsertionData()

        if (!insertionData.isPublishable()) {
            showErrorDialog(contentRes = R.string.publish_insertion_incomplete_error_content)
            return
        }

        showProgressDialog()
        doAsync {
            val published = true // TODO Use presenter to publish
            uiThread {
                dismissProgressDialog()
                if (published) {
                    showSuccessDialog()
                } else {
                    showErrorDialog()
                }
            }
        }
    }

    private fun dismissProgressDialog() {
        progressDialog?.dismiss()
    }

    private fun showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = getProgressDialog()
        }
        progressDialog?.show()
    }

    private fun showSuccessDialog() {
        if (successDialog == null) {
            successDialog = getSuccessDialog()
        }
        successDialog?.show()
    }

    private fun showErrorDialog(titleRes: Int = R.string.publish_insertion_error_title,
                                contentRes: Int = R.string.publish_insertion_error_content) {
        errorDialog = getErrorDialog(titleRes, contentRes)
        errorDialog?.show()
    }

    private fun getProgressDialog(): MaterialDialog {
        return MaterialDialog.Builder(context)
                .content(R.string.progress_publishing)
                .progress(true, 0) // Indeterminate progress dialog
                .cancelable(false) // Clicking outside the dialog does not close it
                .build()
    }

    private fun getSuccessDialog(): MaterialDialog {
        return MaterialDialog.Builder(context)
                .title(R.string.publish_insertion_success_title)
                .content(R.string.publish_insertion_success_content)
                .positiveText(R.string.publish_insertion_success_quit)
                .onPositive { materialDialog, dialogAction -> activity.finish() }
                .build()
    }

    private fun getErrorDialog(titleRes: Int, contentRes: Int): MaterialDialog {
        return MaterialDialog.Builder(context)
                .title(titleRes)
                .content(contentRes)
                .positiveText(android.R.string.ok)
                .build()
    }

}
