package io.zoemeow.dutapp.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.zoemeow.dutapp.data.NewsCacheDatabase
import io.zoemeow.dutapp.data.NewsGlobalCacheDatabaseDao
import io.zoemeow.dutapp.data.NewsSubjectCacheDatabaseDao
import io.zoemeow.dutapp.network.DutFuncApi
import io.zoemeow.dutapp.repository.DutNewsRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideNewsRepository(api: DutFuncApi) = DutNewsRepository(api)

    @Singleton
    @Provides
    fun provideDutFuncApi(): DutFuncApi {
        return Retrofit.Builder()
            .baseUrl("https://dutapi.herokuapp.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DutFuncApi::class.java)
    }

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