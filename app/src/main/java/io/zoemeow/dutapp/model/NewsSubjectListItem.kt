package io.zoemeow.dutapp.model

import com.google.gson.annotations.SerializedName

data class NewsSubjectListItem(
    val date: Long? = null,

    @SerializedName("newstype")
    val newsType: String? = null,

    @SerializedName("newslist")
    val newsList: ArrayList<NewsSubjectItem>? = null
)
