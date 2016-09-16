package com.nibokapp.nibok.ui.adapter.viewtype.delegate

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import com.nibokapp.nibok.R
import com.nibokapp.nibok.domain.model.BookInsertionModel
import com.nibokapp.nibok.extension.animateBounce
import com.nibokapp.nibok.extension.inflate
import com.nibokapp.nibok.extension.loadImg
import com.nibokapp.nibok.extension.toCurrency
import com.nibokapp.nibok.ui.adapter.viewtype.ViewTypeAdapter
import com.nibokapp.nibok.ui.adapter.viewtype.common.ViewType
import com.nibokapp.nibok.ui.adapter.viewtype.common.ViewTypeDelegateAdapter
import com.nibokapp.nibok.ui.adapter.viewtype.common.ViewTypes
import kotlinx.android.synthetic.main.card_book.view.*

/**
 * Delegate adapter managing the creation and binding of book view holders.
 */
class BookInsertionDelegateAdapter(val itemClickManager: ViewTypeAdapter.ItemClickManager) : ViewTypeDelegateAdapter {

    companion object {
        private val TAG = BookInsertionDelegateAdapter::class.java.simpleName
    }

    /**
     * Creates a book view holder when needed.
     */
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return BookVH(parent, itemClickManager)
    }

    /**
     * Casts a view holder to a book view holder and then binds it to the given book model.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType) {
        holder as BookVH
        holder.bind(item as BookInsertionModel)
    }

    /**
     * Book view holder.
     */
    class BookVH(parent: ViewGroup, val itemClickManager: ViewTypeAdapter.ItemClickManager) :
            RecyclerView.ViewHolder(parent.inflate(R.layout.card_book)) {

        companion object {
            private val MAX_AUTHORS = 2
        }

        private var insertionId: Long? = null

        /**
         * Binds the itemView of the view holder to the given item's data.
         *
         * @param item the item containing data about the book preview
         */
        fun bind(item: BookInsertionModel) {
            insertionId = item.insertionId
            val hasThumbnail = item.bookPictureSources.size >= 1
            if (hasThumbnail) loadThumbnail(item.bookPictureSources[0])
            bindData(item)
            updateSaveButton(itemView.saveButton, item.savedByUser)
            addSaveButtonListener(item)
            addThumbnailListener()
            addCardListener()
        }

        /**
         * Load the thumbnail of the book's cover into the image view.
         *
         * @param imgSource the source of the book's cover image
         */
        private fun loadThumbnail(imgSource: String) = with(itemView) {
            bookThumbnail.loadImg(imgSource)
        }

        /**
         * Bind the textual data of the book to the view.
         *
         * @param item the item from which we extract the data
         */
        private fun bindData(item: BookInsertionModel) = with(itemView) {
            with(item) {
                with(bookInfo) {
                    bookTitle.text = title
                    bookAuthor.text = authors.take(MAX_AUTHORS).joinToString("\n")
                    bookYear.text = year.toString()
                }
                bookQuality.text = bookCondition
                bookPriceValue.text = bookPrice.toCurrency()
            }
        }

        /**
         * Listen to clicks on the card.
         */
        private fun addCardListener() = with(itemView) {
            setOnClickListener {
                insertionId?.let {
                    itemClickManager.onItemClick(it, ViewTypes.BOOK_INSERTION)
                }
            }
        }

        /**
         * Listen to clicks on the thumbnail.
         */
        private fun addThumbnailListener() = with(itemView) {
            bookThumbnail.setOnClickListener {
                insertionId?.let {
                    itemClickManager.onItemClick(it, ViewTypes.BOOK_INSERTION)
                }
            }
        }

        /**
         * Listen to clicks on the save button.
         *
         * Toggle the save status of the item.
         * If just saved change the graphics of the save button and show the save animation.
         * Display a message informing the user of the save status.
         *
         * @param item the item being saved/unsaved
         */
        private fun addSaveButtonListener(item: BookInsertionModel) = with(itemView) {
            saveButton.setOnClickListener {
                Log.d(TAG, "Save button clicked")
                insertionId?.let {
                    itemClickManager.onButtonClick(it, ViewTypes.BOOK_INSERTION)
                }
                if (itemClickManager.updateItemOnButtonClick()) {
                    item.savedByUser = !item.savedByUser
                    updateSaveButton(saveButton, item.savedByUser)
                    if (item.savedByUser) {
                        saveButton.animateBounce()
                    }
                }
            }
        }

        /**
         * Update the graphics of the save button given the saved status.
         *
         * @param saveButton the save button
         * @param saved the status of saved
         */
        private fun updateSaveButton(saveButton: ImageView, saved: Boolean) {

            if (saved) {
                saveButton.apply {
                    setColorFilter(ContextCompat.getColor(context, R.color.primary))
                    setImageResource(R.drawable.ic_bookmark_black_24dp)
                }
            } else {
                saveButton.apply {
                    setColorFilter(ContextCompat.getColor(context, R.color.secondary_text))
                    setImageResource(R.drawable.ic_bookmark_border_black_24dp)
                }
            }
        }

    }

}