package io.zoemeow.dutapp.data.db

import androidx.room.*
import io.zoemeow.dutapp.model.NewsGlobalItem
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsGlobalCacheDatabaseDao {
    @Query("SELECT * FROM newsGlobalCache")
    fun getAllNews(): Flow<List<NewsGlobalItem>>

    @Query("SELECT * FROM newsGlobalCache WHERE id=:id")
    suspend fun getNewsById(id: String): NewsGlobalItem

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(news: NewsGlobalItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(newsList: ArrayList<NewsGlobalItem>)

    @Update
    suspend fun updateNews(news: NewsGlobalItem)

    @Delete
    suspend fun deleteNews(news: NewsGlobalItem)

    @Query("DELETE FROM newsGlobalCache")
    suspend fun deleteAllNews()
}