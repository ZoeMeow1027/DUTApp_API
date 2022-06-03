package io.zoemeow.dutapp.model.news

data class NewsGlobalListItem(
    val date: Long? = null,
    val news_type: String? = null,
    val news_list: ArrayList<NewsGlobalItem>? = null
)