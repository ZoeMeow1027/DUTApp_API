package io.zoemeow.dutapp.model

data class NewsItem(
    val date: Long? = null,
    val gmt: Double? = null,
    val newstype: String? = null,
    val newslist: List<NewsListItem>? = null
)
