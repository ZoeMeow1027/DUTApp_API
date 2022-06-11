package io.zoemeow.dutapp.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.zoemeow.dutapp.repository.AppSettingsFileRepository
import io.zoemeow.dutapp.repository.NewsCacheFileRepository
import io.zoemeow.dutapp.repository.SubjectCacheFileRepository
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppFileModule {
    @Provides
    fun provideNewsCacheRepository(@ApplicationContext context: Context): NewsCacheFileRepository {
        val filePath = "${context.cacheDir.path}/newsCache.json"
        val file = File(filePath)
        return NewsCacheFileRepository(file)
    }

    @Provides
    @Singleton
    fun provideSubjectCacheRepository(@ApplicationContext context: Context): SubjectCacheFileRepository {
        val filePath = "${context.cacheDir.path}/subjectCache.json"
        val file = File(filePath)
        return SubjectCacheFileRepository(file)
    }

    @Provides
    @Singleton
    fun provideAppSettings2Repository(@ApplicationContext context: Context): AppSettingsFileRepository {
        val filePath = "${context.filesDir.path}/appSettings2.json"
        val file = File(filePath)
        return AppSettingsFileRepository(file)
    }
}
