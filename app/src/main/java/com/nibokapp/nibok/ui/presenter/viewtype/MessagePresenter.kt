package com.nibokapp.nibok.ui.presenter.viewtype

import com.nibokapp.nibok.domain.model.ConversationModel
import com.nibokapp.nibok.ui.adapter.viewtype.common.ViewType
import com.nibokapp.nibok.ui.presenter.viewtype.common.ViewTypePresenter
import java.util.*

/**
 * Presenter that operates on messages conversations.
 */
class MessagePresenter : ViewTypePresenter {

    private var cachedData: List<ViewType> = emptyList()

    override fun getData(): List<ViewType> {
        cachedData = (1..20).map {
            val cal = Calendar.getInstance()
            val delta = it-1
            cal.add(Calendar.DATE, -delta)
            val date = cal.time
            ConversationModel(it.toLong(), "avatar", "John Doe",
                    "Once upon a time in a land far far away $it",
                    date) }
        return cachedData
    }

    override fun getCachedData(): List<ViewType> = cachedData

    override fun getQueryData(query: String): List<ViewType> {
        return emptyList() // TODO
    }
}
