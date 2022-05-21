package io.zoemeow.dutapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.zoemeow.dutapp.network.DutFuncApi
import io.zoemeow.dutapp.repository.DutMainRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideNewsRepository(api: DutFuncApi) = DutMainRepository(api)

    @Singleton
    @Provides
    fun provideDutFuncApi(): DutFuncApi {
        return Retrofit.Builder()
            .baseUrl("https://dutapi.herokuapp.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DutFuncApi::class.java)
    }
}