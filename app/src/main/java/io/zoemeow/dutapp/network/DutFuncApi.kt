package io.zoemeow.dutapp.network

import io.zoemeow.dutapp.model.NewsItem
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DutFuncApi {
    // https://stackoverflow.com/questions/58298010/how-to-send-parameters-for-get-request-using-retrofit-and-kotlin-coroutines
    @GET("/news")
    suspend fun getNews(
        @Query(value = "type") newsType: String,
        @Query(value = "page") page: Int = 1
    ): NewsItem
}