package com.nibokapp.nibok.ui.fragment.publish

import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.nibokapp.nibok.R
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

    override fun setupInput() {
        setupPublishButton()
    }

    private fun setupPublishButton() {
        btnPublishInsertion.setOnClickListener {
            publishInsertion()
        }
    }

    private fun publishInsertion() {
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

    private fun showErrorDialog() {
        if (errorDialog == null) {
            errorDialog = getErrorDialog()
        }
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

    private fun getErrorDialog(): MaterialDialog {
        return MaterialDialog.Builder(context)
                .title(R.string.publish_insertion_error_title)
                .content(R.string.publish_insertion_error_content)
                .positiveText(android.R.string.ok)
                .build()
    }

}
