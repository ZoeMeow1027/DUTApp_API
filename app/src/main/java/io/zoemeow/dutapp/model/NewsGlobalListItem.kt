package io.zoemeow.dutapp.model

data class NewsGlobalListItem(
    val date: Long? = null,
    val newstype: String? = null,
    val newslist: ArrayList<NewsGlobalItem>? = null
)
