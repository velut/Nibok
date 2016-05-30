package com.nibokapp.nibok.ui.adapter.delegate

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.ImageView
import com.nibokapp.nibok.R
import com.nibokapp.nibok.domain.model.BookModel
import com.nibokapp.nibok.extension.inflate
import com.nibokapp.nibok.extension.loadImg
import com.nibokapp.nibok.ui.adapter.common.ViewType
import com.nibokapp.nibok.ui.adapter.common.ViewTypeDelegateAdapter
import kotlinx.android.synthetic.main.book_card.view.*
import org.jetbrains.anko.toast

class BookDelegateAdapter : ViewTypeDelegateAdapter {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return BookVH(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType) {
        holder as BookVH
        holder.bind(item as BookModel)
    }

    class BookVH(parent: ViewGroup) : RecyclerView.ViewHolder(
            parent.inflate(R.layout.book_card)) {

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
                val toastMessage = if (item.saved) R.string.book_saved_to_collection
                                    else R.string.book_removed_from_collection
                context.toast(toastMessage)
            }

        }

        private fun updateSaveButton(saveButton: ImageView, saved: Boolean) {
            if (saved) {
                saveButton.apply {
                    setColorFilter(ContextCompat.getColor(context, R.color.primary))
                    setImageResource(R.drawable.ic_bookmark_black_48dp)
                }
            } else {
                saveButton.apply {
                    setColorFilter(ContextCompat.getColor(context, R.color.secondary_text))
                    setImageResource(R.drawable.ic_bookmark_border_black_48dp)
                }
            }
        }

    }

}