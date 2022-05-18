package io.zoemeow.dutapp.repository

import io.zoemeow.dutapp.model.NewsItem
import io.zoemeow.dutapp.model.NewsType
import io.zoemeow.dutapp.network.DutFuncApi
import javax.inject.Inject

class DutNewsRepository @Inject constructor(private val api: DutFuncApi)  {
    private var data = NewsItem()

    private var loading: Boolean = false;
    private var ex: Exception? = null;

    suspend fun getAllNews(newsType: NewsType, page: Int = 1): NewsItem {
        try {
            loading = true
            data = api.getNews(
                if (newsType == NewsType.Global)
                    "global"
                else "subjects",
                page
            )
            loading = false
        } catch (ex: Exception) {
            this.ex = ex
        }

        return data
    }
}