package io.zoemeow.dutapp.model.cache

import io.zoemeow.dutapp.model.news.NewsGlobalItem
import io.zoemeow.dutapp.model.news.NewsSubjectItem

data class NewsCacheItem(
    var newsGlobalUpdateTime: Long = 0,
    val newsGlobalList: ArrayList<NewsGlobalItem> = ArrayList(),
    var newsSubjectUpdateTime: Long = 0,
    val newsSubjectList: ArrayList<NewsSubjectItem> = ArrayList(),
)
