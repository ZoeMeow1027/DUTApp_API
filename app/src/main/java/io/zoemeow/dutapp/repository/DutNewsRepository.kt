package io.zoemeow.dutapp.repository

import io.zoemeow.dutapp.model.NewsListItem
import io.zoemeow.dutapp.model.NewsType
import io.zoemeow.dutapp.network.DutFuncApi
import javax.inject.Inject

class DutNewsRepository @Inject constructor(private val api: DutFuncApi) {
    // Get news
    private var data = NewsListItem()
    private var loading: Boolean = false
    private var ex: Exception? = null

    suspend fun getAllNews(newsType: NewsType, page: Int = 1): NewsListItem {
        try {
            loading = true
            data = api.getNews(
                if (newsType == NewsType.Global)
                    "global"
                else "subjects",
                page
            )
        } catch (ex: Exception) {
            this.ex = ex
        }

        loading = false
        return data
    }
}