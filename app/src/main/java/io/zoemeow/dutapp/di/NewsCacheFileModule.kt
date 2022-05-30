package io.zoemeow.dutapp.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.zoemeow.dutapp.repository.NewsCacheFileRepository
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NewsCacheFileModule {
    @Provides
    fun provideNewsCacheRepository(@ApplicationContext context: Context): NewsCacheFileRepository {
        val filePath = "${context.cacheDir.path}/newsCache.json"
        val file = File(filePath)
        return NewsCacheFileRepository(file)
    }
}
