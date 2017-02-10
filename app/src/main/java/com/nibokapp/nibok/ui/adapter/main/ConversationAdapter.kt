package com.nibokapp.nibok.ui.adapter.main

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.nibokapp.nibok.R
import com.nibokapp.nibok.domain.model.ConversationModel
import com.nibokapp.nibok.extension.*
import kotlinx.android.synthetic.main.item_message.view.*
import java.util.*
import kotlin.properties.Delegates

/**
 * Updatable adapter holding ConversationModel items.
 *
 * All listener functions receive the item's id.
 *
 * @param onItemClick the function to execute when the item is clicked
 */
class ConversationAdapter(
        val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<ConversationAdapter.ViewHolder>(), UpdatableAdapter<ConversationModel> {

    companion object {
        private val TAG = ConversationAdapter::class.java.simpleName

        private val KEY_DATE: String = "${TAG}:KEY_DATE"
        private val KEY_PREVIEW_TEXT: String = "${TAG}:KEY_PREVIEW_TEXT"
    }

    /**
     * Items held by the adapter.
     * When items change an adapter update is triggered. See [UpdatableAdapter].
     */
    override var items: List<ConversationModel> by Delegates.observable(emptyList()) {
        prop, oldItems, newItems ->
        Log.d(TAG, "Updating adapter's conversation items")
        update(oldItems, newItems,
                { o, n -> areItemsTheSame(o, n) },
                { o, n -> getChangePayload(o, n) })
    }

    private fun areItemsTheSame(old: ConversationModel, new: ConversationModel): Boolean {
        return old.conversationId == new.conversationId
    }

    private fun getChangePayload(old: ConversationModel, new: ConversationModel): Bundle? {
        val diffBundle = Bundle()
        if (old.previewText != new.previewText) {
            diffBundle.putString(KEY_PREVIEW_TEXT, new.previewText)
        }
        if (old.date != new.date) {
            diffBundle.putString(KEY_DATE, new.date.toStringDate())
        }
        return if (diffBundle.size() != 0) diffBundle else null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_message), onItemClick)
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
                KEY_DATE -> {
                    val newDate = updateBundle.getString(KEY_DATE)?.parseDate()
                    newDate?.let { holder.updateDate(it) }
                }
                KEY_PREVIEW_TEXT -> {
                    val newPreviewText = updateBundle.getString(KEY_PREVIEW_TEXT, "")
                    holder.updatePreviewText(newPreviewText)
                }
            }
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }


    class ViewHolder(itemView: View,
                     val onItemClick: (String) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        companion object {
            private val MAX_PARTNER_NAME_LENGTH_PORTRAIT = 15
            private val MAX_PARTNER_NAME_LENGTH_LANDSCAPE = 25
            private val MAX_PREVIEW_TEXT_LENGTH_PORTRAIT = 25
            private val MAX_PREVIEW_TEXT_LENGTH_LANDSCAPE = 40
        }

        private val maxPartnerNameLength: Int
            get() = if (itemView.context.isOrientationPortrait()) {
                MAX_PARTNER_NAME_LENGTH_PORTRAIT
            } else {
                MAX_PARTNER_NAME_LENGTH_LANDSCAPE
            }

        private val maxPreviewTextLength: Int
            get() = if (itemView.context.isOrientationPortrait()) {
                MAX_PREVIEW_TEXT_LENGTH_PORTRAIT
            } else {
                MAX_PREVIEW_TEXT_LENGTH_LANDSCAPE
            }

        fun updateDate(newDate: Date) {
            with(itemView) {
                messageDate.text = newDate.toPreviewDateString(context)
            }
        }

        fun updatePreviewText(newText: String) {
            with(itemView) {
                messageContent.text = newText.ellipsize(maxPreviewTextLength)
            }
        }

        /**
         * Bind the item to the item's view.
         *
         * @param item the item containing the data used to populate the view
         */
        fun bind(item: ConversationModel) {
            loadAvatar(item.partner.avatar)
            bindTextData(item)
            addClickListeners(item.conversationId)
        }

        private fun loadAvatar(imageSource: String) = with(itemView) {
            val placeholder = R.drawable.ic_account_circle_dark_green_48dp
            messageAvatar.loadImage(imageSource, placeholder, placeholder, animate = false)
        }

        private fun bindTextData(item: ConversationModel) = with(itemView) {
            with(item) {
                messagePartner.text = partner.username.ellipsize(maxPartnerNameLength)
                messageContent.text = previewText.ellipsize(maxPreviewTextLength)
                messageDate.text = date.toPreviewDateString(context)
            }
        }

        private fun addClickListeners(conversationId: String) {
            itemView.setOnClickListener {
                Log.d(TAG, "Conversation item clicked")
                onItemClick(conversationId)
            }
        }

        private fun Date.toPreviewDateString(context: Context): String {
            val yesterdayString = context.getString(R.string.yesterday)
            return this.toDeltaBasedSimpleDateString(yesterdayString)
        }

    }
}
