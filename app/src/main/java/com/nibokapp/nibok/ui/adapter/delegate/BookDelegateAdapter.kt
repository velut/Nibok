package com.nibokapp.nibok.ui.adapter.delegate

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.ImageView
import com.nibokapp.nibok.R
import com.nibokapp.nibok.domain.model.BookModel
import com.nibokapp.nibok.extension.animateBounce
import com.nibokapp.nibok.extension.inflate
import com.nibokapp.nibok.extension.loadImg
import com.nibokapp.nibok.extension.toCurrency
import com.nibokapp.nibok.ui.activity.InsertionDetailActivity
import com.nibokapp.nibok.ui.adapter.common.ViewType
import com.nibokapp.nibok.ui.adapter.common.ViewTypeDelegateAdapter
import kotlinx.android.synthetic.main.card_book.view.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

/**
 * Delegated adapter managing the creation and binding of book view holders.
 *
 */
class BookDelegateAdapter : ViewTypeDelegateAdapter {

    /**
     * Creates a book view holder when needed.
     */
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return BookVH(parent)
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
    class BookVH(parent: ViewGroup) : RecyclerView.ViewHolder(
            parent.inflate(R.layout.card_book)) {

        /**
         * Binds the itemView of the view holder to the given item.
         *
         * @param item the item of which the properties will be bound in the itemView
         */
        fun bind(item: BookModel) {
            addThumbnail(item.thumbnail)
            bindData(item)
            updateSaveButton(itemView.saveButton, item.saved)
            addSaveButtonListener(item)
            addThumbnailListener()
            addCardListener()
        }

        /**
         * Load the thumbnail of the book's cover into the view.
         *
         * @param imgSrc the source of the image
         */
        private fun addThumbnail(imgSrc: String) = with(itemView) {
            bookThumbnail.loadImg(imgSrc)
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
                // TODO
                context.toast("Card CLICKED")
            }
        }

        /**
         * Listen to clicks on the thumbnail.
         */
        private fun addThumbnailListener() = with(itemView) {
            bookThumbnail.setOnClickListener {
                startDetailActivity()
            }
        }

        private fun startDetailActivity() {
            itemView.context.startActivity<InsertionDetailActivity>()
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
                val saved = toggleItemSave(item)
                updateSaveButton(saveButton, saved)
                if (saved) {
                    saveButton.animateBounce()
                }
                val toastMessage = if (saved) R.string.book_saved_to_collection
                else R.string.book_removed_from_collection
                context.toast(toastMessage)
            }
        }

        /**
         * Toggle the save status of the considered item.
         *
         * @param item the item subject to the toggle of the save status
         */
        private fun toggleItemSave(item: BookModel): Boolean {
            // TODO
            item.saved = !item.saved
            return item.saved
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