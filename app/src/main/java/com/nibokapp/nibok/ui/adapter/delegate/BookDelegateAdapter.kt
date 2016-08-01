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
import com.nibokapp.nibok.ui.adapter.common.ViewType
import com.nibokapp.nibok.ui.adapter.common.ViewTypeDelegateAdapter
import kotlinx.android.synthetic.main.book_card.view.*
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
            parent.inflate(R.layout.book_card)) {

        /**
         * Binds the itemView of the view holder to the given item.
         *
         * @param item the item of which the properties will be bound in the itemView
         */
        fun bind(item: BookModel) = with(itemView) {
            bookThumbnail.loadImg(item.thumbnail)
            bookTitle.text = item.title
            bookAuthor.text = item.author
            bookYear.text = item.year.toString()
            bookQuality.text = item.quality
            bookPrice.text = "€ ${item.priceIntPart},${item.priceFracPart}"
            updateSaveButton(saveButton, item.saved)

            saveButton.setOnClickListener {
                item.saved = !item.saved
                updateSaveButton(saveButton, item.saved)
                if (item.saved) {
                    saveButton.animateBounce()
                }
                val toastMessage = if (item.saved) R.string.book_saved_to_collection
                                    else R.string.book_removed_from_collection
                context.toast(toastMessage)
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