package io.zoemeow.dutapp.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.zoemeow.dutapp.data.NewsGlobalCacheDatabase
import io.zoemeow.dutapp.data.NewsGlobalCacheDatabaseDao
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

    // Database for News global cache
    @Provides
    @Singleton
    fun provideNewsGlobalItemDao(newsDatabase: NewsGlobalCacheDatabase): NewsGlobalCacheDatabaseDao {
        return newsDatabase.getDao()
    }

    @Provides
    @Singleton
    fun provideNewsGlobalItemDatabase(@ApplicationContext context: Context): NewsGlobalCacheDatabase {
        return Room.databaseBuilder(
            context,
            NewsGlobalCacheDatabase::class.java,
            "newsGlobalCache"
        ).fallbackToDestructiveMigration().build()
    }
}