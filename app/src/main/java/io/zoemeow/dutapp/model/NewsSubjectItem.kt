package io.zoemeow.dutapp.model

data class NewsSubjectItem(
    val date: Long? = null,
    val title: String? = null,
    val contenttext: String? = null,
    val links: ArrayList<LinkItem>? = null,
)