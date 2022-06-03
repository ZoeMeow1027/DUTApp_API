package io.zoemeow.dutapp.model.news

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*
import kotlin.collections.ArrayList

data class NewsSubjectItem(
    @Expose
    var id: String = UUID.randomUUID().toString(),

    var date: Long? = null,
    var title: String? = null,
    var content: String? = null,
    var links: ArrayList<LinkItem>? = null,
)