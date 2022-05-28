package io.zoemeow.dutapp.data.db

import androidx.room.*
import io.zoemeow.dutapp.model.NewsSubjectItem
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsSubjectCacheDatabaseDao {
    @Query("SELECT * FROM newsSubjectCache")
    fun getAllNews(): Flow<List<NewsSubjectItem>>

    @Query("SELECT * FROM newsSubjectCache WHERE id=:id")
    suspend fun getNewsById(id: String): NewsSubjectItem

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(news: NewsSubjectItem)

    @Update
    suspend fun updateNews(news: NewsSubjectItem)

    @Delete
    suspend fun deleteNews(news: NewsSubjectItem)

    @Query("DELETE FROM newsSubjectCache")
    suspend fun deleteAllNews()
}