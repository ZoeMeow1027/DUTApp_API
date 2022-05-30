package io.zoemeow.dutapp.model

data class NewsCacheItem(
    val newsGlobalUpdateTime: Long = 0,
    val newsGlobalList: ArrayList<NewsGlobalItem> = ArrayList(),
    val newsSubjectUpdateTime: Long = 0,
    val newsSubjectList: ArrayList<NewsSubjectItem> = ArrayList(),
)
