package com.nibokapp.nibok.ui.adapter.delegate

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import com.nibokapp.nibok.R
import com.nibokapp.nibok.data.repository.UserManager
import com.nibokapp.nibok.domain.model.BookModel
import com.nibokapp.nibok.extension.animateBounce
import com.nibokapp.nibok.extension.inflate
import com.nibokapp.nibok.extension.loadImg
import com.nibokapp.nibok.extension.toCurrency
import com.nibokapp.nibok.ui.activity.InsertionDetailActivity
import com.nibokapp.nibok.ui.adapter.common.ViewType
import com.nibokapp.nibok.ui.adapter.common.ViewTypeDelegateAdapter
import com.nibokapp.nibok.ui.fragment.InsertionDetailFragment
import kotlinx.android.synthetic.main.card_book.view.*
import org.jetbrains.anko.startActivity

/**
 * Delegated adapter managing the creation and binding of book view holders.
 *
 */
class BookDelegateAdapter(val itemClickListener: (ViewType) -> Unit) : ViewTypeDelegateAdapter {

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
    class BookVH(parent: ViewGroup, val itemClickListener: (ViewType) -> Unit) : RecyclerView.ViewHolder(
            parent.inflate(R.layout.card_book)) {

        private var insertionId: Long? = null

        /**
         * Binds the itemView of the view holder to the given item.
         *
         * @param item the item of which the properties will be bound in the itemView
         */
        fun bind(item: BookModel) {
            insertionId = item.insertionId
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
                startDetailActivity()
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
            insertionId?.let {
                itemView.context.startActivity<InsertionDetailActivity>(
                        InsertionDetailFragment.INSERTION_ID to it
                )
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
                item.saved = toggleItemSave(item.insertionId)
                updateSaveButton(saveButton, item.saved)
                if (item.saved) {
                    saveButton.animateBounce()
                }
                itemClickListener(item)
            }
        }

        /**
         * Toggle the save status of the considered item.
         *
         * @param itemId the id of the item subject to the toggle of the save status
         */
        private fun toggleItemSave(itemId: Long) : Boolean =
                UserManager.toggleSaveInsertion(itemId)

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