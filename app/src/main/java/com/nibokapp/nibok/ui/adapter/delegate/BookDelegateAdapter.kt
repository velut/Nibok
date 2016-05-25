package com.nibokapp.nibok.ui.adapter.delegate

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.nibokapp.nibok.R
import com.nibokapp.nibok.domain.model.BookModel
import com.nibokapp.nibok.extension.inflate
import com.nibokapp.nibok.extension.loadImg
import com.nibokapp.nibok.ui.adapter.common.ViewType
import com.nibokapp.nibok.ui.adapter.common.ViewTypeDelegateAdapter
import kotlinx.android.synthetic.main.book_card.view.*


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
        }
    }
}