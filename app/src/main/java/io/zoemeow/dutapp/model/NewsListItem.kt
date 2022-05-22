package io.zoemeow.dutapp.model

data class NewsListItem(
    val date: Long? = null,
    val newstype: String? = null,
    val newslist: List<NewsItem>? = null
)
