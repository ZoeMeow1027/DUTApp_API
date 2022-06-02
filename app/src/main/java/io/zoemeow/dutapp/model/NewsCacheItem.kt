package io.zoemeow.dutapp.model

data class NewsCacheItem(
    var newsGlobalUpdateTime: Long = 0,
    val newsGlobalList: ArrayList<NewsGlobalItem> = ArrayList(),
    var newsSubjectUpdateTime: Long = 0,
    val newsSubjectList: ArrayList<NewsSubjectItem> = ArrayList(),
)
