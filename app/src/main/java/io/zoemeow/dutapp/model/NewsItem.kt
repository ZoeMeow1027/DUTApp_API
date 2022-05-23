package io.zoemeow.dutapp.model

data class NewsItem(
    val date: Long? = null,
    val title: String? = null,
    val contenttext: String? = null,
    val links: List<LinkItem>? = null,
)