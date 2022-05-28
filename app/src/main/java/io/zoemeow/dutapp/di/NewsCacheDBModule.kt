package io.zoemeow.dutapp.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.zoemeow.dutapp.data.db.NewsCacheDatabase
import io.zoemeow.dutapp.data.db.NewsGlobalCacheDatabaseDao
import io.zoemeow.dutapp.data.db.NewsSubjectCacheDatabaseDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NewsCacheDBModule {
    // Database for News global cache and news subject cache
    @Provides
    @Singleton
    fun provideNewsGlobalCacheDao(newsDatabase: NewsCacheDatabase): NewsGlobalCacheDatabaseDao {
        return newsDatabase.getNewsGlobalDbDao()
    }

    @Provides
    @Singleton
    fun provideNewsSubjectCacheDao(newsDatabase: NewsCacheDatabase): NewsSubjectCacheDatabaseDao {
        return newsDatabase.getNewsSubjectDbDao()
    }

    @Provides
    @Singleton
    fun provideNewsCacheDatabase(@ApplicationContext context: Context): NewsCacheDatabase {
        return Room.databaseBuilder(
            context,
            NewsCacheDatabase::class.java,
            "${context.cacheDir.path}/newsCache.db"
        ).fallbackToDestructiveMigration().build()
    }
}