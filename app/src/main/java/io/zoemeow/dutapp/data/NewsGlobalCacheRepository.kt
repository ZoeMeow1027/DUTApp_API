package io.zoemeow.dutapp.data

import io.zoemeow.dutapp.model.NewsGlobalItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NewsGlobalCacheRepository @Inject constructor(
    private val newsDatabaseDao: NewsGlobalCacheDatabaseDao
) {
    fun getAllNews(): Flow<List<NewsGlobalItem>> = newsDatabaseDao.getAllNewsGlobal()
    suspend fun getNewsById(id: String): NewsGlobalItem = newsDatabaseDao.getNewsGlobalById(id)
    suspend fun insertNews(news: NewsGlobalItem) = newsDatabaseDao.insertNewsGlobal(news)
    suspend fun updateNews(news: NewsGlobalItem) = newsDatabaseDao.updateNewsGlobal(news)
    suspend fun deleteNews(news: NewsGlobalItem) = newsDatabaseDao.deleteNewsGlobal(news)
    suspend fun deleteAllNews() = newsDatabaseDao.deleteAllNewsGlobal()
}