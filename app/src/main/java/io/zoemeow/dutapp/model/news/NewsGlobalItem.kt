package io.zoemeow.dutapp.model.news

import com.google.gson.annotations.Expose
import java.util.*

data class NewsGlobalItem(
    @Expose
    var id: String = UUID.randomUUID().toString(),
	
    var date: Long? = null,
    var title: String? = null,
    var content: String? = null,
    var links: ArrayList<LinkItem>? = null,
)