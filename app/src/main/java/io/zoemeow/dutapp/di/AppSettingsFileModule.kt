package io.zoemeow.dutapp.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.zoemeow.dutapp.repository.AppSettingsRepository
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppSettingsFileModule {
    @Provides
    @Singleton
    fun provideAppSettingsRepository(@ApplicationContext context: Context): AppSettingsRepository {
        val filePath = "${context.filesDir.path}/appSettings.json"
        val file = File(filePath)
        return AppSettingsRepository(file)
    }
}