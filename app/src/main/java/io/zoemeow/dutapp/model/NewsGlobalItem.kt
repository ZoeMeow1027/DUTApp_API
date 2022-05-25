package io.zoemeow.dutapp.model

data class NewsGlobalItem(
    val date: Long? = null,
    val title: String? = null,
    val contenttext: String? = null,
    val links: List<LinkItem>? = null,
)