package io.zoemeow.dutapp.model.news

data class NewsSubjectListItem(
    val date: Long? = null,
    val news_type: String? = null,
    val news_list: ArrayList<NewsSubjectItem>? = null
)