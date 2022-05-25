package io.zoemeow.dutapp.model

data class NewsSubjectListItem(
    val date: Long? = null,
    val newstype: String? = null,
    val newslist: List<NewsSubjectItem>? = null
)
