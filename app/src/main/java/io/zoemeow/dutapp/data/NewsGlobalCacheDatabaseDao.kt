package io.zoemeow.dutapp.data

import androidx.room.*
import io.zoemeow.dutapp.model.NewsGlobalItem
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsGlobalCacheDatabaseDao {
    @Query("SELECT * FROM newsGlobalCache")
    fun getAllNewsGlobal(): Flow<List<NewsGlobalItem>>

    @Query("SELECT * FROM newsGlobalCache WHERE id=:id")
    suspend fun getNewsGlobalById(id: String): NewsGlobalItem

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewsGlobal(news: NewsGlobalItem)

    @Update
    suspend fun updateNewsGlobal(news: NewsGlobalItem)

    @Delete
    suspend fun deleteNewsGlobal(news: NewsGlobalItem)

    @Query("DELETE FROM newsGlobalCache")
    suspend fun deleteAllNewsGlobal()
}