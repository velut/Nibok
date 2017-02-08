package com.nibokapp.nibok.ui.adapter

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.nibokapp.nibok.R
import com.nibokapp.nibok.domain.model.BookInsertionModel
import com.nibokapp.nibok.extension.*
import com.nibokapp.nibok.ui.App
import kotlinx.android.synthetic.main.card_book.view.*
import kotlin.properties.Delegates

/**
 * Updatable adapter holding BookInsertionModel items.
 *
 * All listener functions receive the item's id.
 *
 * @param onItemClick the function to execute when the item is clicked
 * @param onThumbnailClick the function to execute when the item's thumbnail is clicked
 * @param onSaveButtonClick the optional function to execute when the item's save button is clicked.
 *                          If null the save button is disabled and hidden. Default is null
 */
class InsertionAdapter(
        val onItemClick: (String) -> Unit,
        val onThumbnailClick: (String) -> Unit,
        val onSaveButtonClick: ((String) -> Unit)? = null,
        val onDeleteButtonClick: ((String) -> Unit)? = null
) : RecyclerView.Adapter<InsertionAdapter.ViewHolder>(), UpdatableAdapter {

    companion object {
        private val TAG = InsertionAdapter::class.java.simpleName

        /**
         * Update key for the save button
         */
        private const val KEY_IS_SAVED = "InsertionAdapter.KEY_IS_SAVED"
    }

    /**
     * Items held by the adapter.
     * When items change an adapter update is triggered. See [UpdatableAdapter].
     */
    var items: List<BookInsertionModel> by Delegates.observable(emptyList()) {
        prop, oldItems, newItems ->
        Log.d(TAG, "Updating adapter items")
        update(oldItems, newItems,
                { o, n -> areItemsTheSame(o, n) },
                { o, n -> getChangePayload(o, n) })
    }

    private fun areItemsTheSame(old: BookInsertionModel, new: BookInsertionModel): Boolean {
        return old.getItemId() == new.getItemId()
    }

    private fun getChangePayload(old: BookInsertionModel, new: BookInsertionModel): Bundle? {
        val diffBundle = Bundle()
        if (old.savedByUser != new.savedByUser) {
            diffBundle.putBoolean(KEY_IS_SAVED, new.savedByUser)
        }
        return if (diffBundle.size() != 0) diffBundle else null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.card_book),
                onItemClick, onThumbnailClick, onSaveButtonClick, onDeleteButtonClick)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
            return
        }

        val updateBundle = payloads[0] as? Bundle ?: return

        Log.d(TAG, "Updating view holder through payload: $updateBundle")
        val keys = updateBundle.keySet()
        keys.forEach {
            when (it) {
                KEY_IS_SAVED -> {
                    val isSaved = updateBundle.getBoolean(KEY_IS_SAVED)
                    holder.updateSaveButton(isSaved)
                }
            }
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    /**
     * Toggle the insertion save status of the item with the given id.
     *
     * @param insertionId the id of the insertion of which the save status should be changed
     * @param isSaved true if the new status is saved, false if it is not saved
     */
    fun toggleInsertionSaveStatus(insertionId: String, isSaved: Boolean) {
        if (items.isEmpty()) return

        val updatedItems = items.map {
            if (it.insertionId == insertionId) {
                it.copy(savedByUser = isSaved)
            } else {
                it
            }
        }
        items = updatedItems
    }

    /**
     * Remove the insertion with the given id from the current list of items
     * and return the list of items as it was before the removal.
     *
     * @param insertionId the id of the insertion to remove
     *
     * @return the list of items as it was before the removal, that is including the removed item
     */
    fun removeInsertion(insertionId: String): List<BookInsertionModel> {
        if (items.isEmpty()) return emptyList()

        val oldItems = items.toList()
        val newItems = items.filter { it.insertionId != insertionId }
        items = newItems
        return oldItems
    }

    class ViewHolder(itemView: View,
                     val onItemClick: (String) -> Unit,
                     val onThumbnailClick: (String) -> Unit,
                     val onSaveButtonClick: ((String) -> Unit)? = null,
                     val onDeleteButtonClick: ((String) -> Unit)? = null
    ) : RecyclerView.ViewHolder(itemView) {

        companion object {
            /**
             * Maximum number of authors to display in a card.
             */
            const val MAX_AUTHORS = 2
        }

        /**
         * Update the graphical appearance of the save button.
         *
         * @param isSaved true if the insertion is saved, false if it is not
         * @param animate true if an animation should play when an insertion becomes bookmarked. Default is true
         */
        fun updateSaveButton(isSaved: Boolean, animate: Boolean = true) {
            with(itemView) {
                val (colorFilter, imageResource) = if (isSaved) {
                    Pair(ContextCompat.getColor(context, R.color.primary),
                            R.drawable.ic_bookmark_black_24dp)
                } else {
                    Pair(ContextCompat.getColor(context, R.color.secondary_text),
                            R.drawable.ic_bookmark_border_black_24dp)
                }
                saveButton.apply {
                    setColorFilter(colorFilter)
                    setImageResource(imageResource)
                    if (isSaved && animate) animateBounce()
                }
            }
        }

        /**
         * Bind the item to the item's view.
         *
         * @param item the item containing the data used to populate the view
         */
        fun bind(item: BookInsertionModel) {
            loadThumbnail(item.bookPictureSources.firstOrNull())
            bindTextData(item)
            setupButtons(item)
            addClickListeners(item.insertionId)
        }

        private fun loadThumbnail(imageSource: String?) = with(itemView) {
            val source = imageSource ?: App.PLACEHOLDER_IMAGE_URL
            bookThumbnail.loadImage(source)
        }

        private fun bindTextData(item: BookInsertionModel) = with(itemView) {
            with(item) {

                with(bookInfo) {
                    bookTitle.text = title
                    bookAuthor.text = authors.take(MAX_AUTHORS).joinToString("\n")
                    bookYear.text = year.toString()
                }

                bookCondition.toBookWearCondition(context).let {
                    bookQuality.text = it
                }

                bookPriceValue.text = bookPrice.toCurrency()
            }
        }

        private fun setupButtons(item: BookInsertionModel) = with(itemView) {
            setupSaveButton(item.savedByUser)
            setupDeleteButton()
        }

        private fun setupSaveButton(isSaved: Boolean) = with(itemView) {
            val disableSaveButton = onSaveButtonClick == null
            if (disableSaveButton) {
                saveButton.apply {
                    isEnabled = false
                    setGone()
                }
            } else {
                updateSaveButton(isSaved, false)
            }
        }

        private fun setupDeleteButton() = with(itemView) {
            val disableDeleteButton = onDeleteButtonClick == null
            deleteButton.apply {
                if (disableDeleteButton) {
                    isEnabled = false
                    setGone()
                } else {
                    isEnabled = true
                    setVisible()
                }
            }
        }

        private fun addClickListeners(itemId: String) {
            itemView.setOnClickListener {
                Log.d(TAG, "Card clicked")
                onItemClick(itemId)
            }
            itemView.bookThumbnail.setOnClickListener {
                Log.d(TAG, "Thumbnail clicked")
                onThumbnailClick(itemId)
            }
            onSaveButtonClick?.let {
                itemView.saveButton.setOnClickListener {
                    Log.d(TAG, "Save button clicked")
                    it(itemId)
                }
            }
            onDeleteButtonClick?.let {
                itemView.deleteButton.setOnClickListener {
                    Log.d(TAG, "Delete button clicked")
                    it(itemId)
                }
            }
        }
    }
}
