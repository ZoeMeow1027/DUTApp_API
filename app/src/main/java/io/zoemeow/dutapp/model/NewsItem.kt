package io.zoemeow.dutapp.model

data class NewsItem(
    val date: String? = null,
    val newstype: String? = null,
    val newslist: List<NewsListItem>? = null
)
