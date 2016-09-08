package com.nibokapp.nibok.ui.adapter.viewtype.delegate

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import com.nibokapp.nibok.R
import com.nibokapp.nibok.domain.model.BookModel
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
class BookDelegateAdapter(val itemClickListener: ViewTypeAdapter.ItemClickListener) : ViewTypeDelegateAdapter {

    companion object {
        private val TAG = BookDelegateAdapter::class.java.simpleName
    }

    /**
     * Creates a book view holder when needed.
     */
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return BookVH(parent, itemClickListener)
    }

    /**
     * Casts a view holder to a book view holder and then binds it to the given book model.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType) {
        holder as BookVH
        holder.bind(item as BookModel)
    }

    /**
     * Book view holder.
     */
    class BookVH(parent: ViewGroup, val itemClickListener: ViewTypeAdapter.ItemClickListener) :
            RecyclerView.ViewHolder(parent.inflate(R.layout.card_book)) {

        private var insertionId: Long? = null

        /**
         * Binds the itemView of the view holder to the given item's data.
         *
         * @param item the item containing data about the book preview
         */
        fun bind(item: BookModel) {
            insertionId = item.insertionId
            loadThumbnail(item.thumbnail)
            bindData(item)
            updateSaveButton(itemView.saveButton, item.saved)
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
        private fun bindData(item: BookModel) = with(itemView) {
            bookTitle.text = item.title
            bookAuthor.text = item.author
            bookYear.text = item.year.toString()
            bookQuality.text = item.quality
            bookPrice.text = item.price.toCurrency()
        }

        /**
         * Listen to clicks on the card.
         */
        private fun addCardListener() = with(itemView) {
            setOnClickListener {
                insertionId?.let {
                    itemClickListener.onItemClick(it, ViewTypes.BOOK)
                }
            }
        }

        /**
         * Listen to clicks on the thumbnail.
         */
        private fun addThumbnailListener() = with(itemView) {
            bookThumbnail.setOnClickListener {
                insertionId?.let {
                    itemClickListener.onItemClick(it, ViewTypes.BOOK)
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
        private fun addSaveButtonListener(item: BookModel) = with(itemView) {
            saveButton.setOnClickListener {
                Log.d(TAG, "Save button clicked")
                insertionId?.let {
                    itemClickListener.onButtonClick(it, ViewTypes.BOOK)
                }
                // Optimistic update of the save button
                item.saved = !item.saved
                updateSaveButton(saveButton, item.saved)
                if (item.saved) {
                    saveButton.animateBounce()
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