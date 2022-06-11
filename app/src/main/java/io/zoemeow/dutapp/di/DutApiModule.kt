package io.zoemeow.dutapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.zoemeow.dutapp.network.DutFuncApi
import io.zoemeow.dutapp.repository.DutApiRepository
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DutApiModule {
    @Singleton
    @Provides
    fun provideDutApiRepository(api: DutFuncApi) = DutApiRepository(api)

    @Singleton
    @Provides
    fun provideDutFuncApi(): DutFuncApi {
        val okHttpClient = OkHttpClient().newBuilder()
        okHttpClient.connectTimeout(60, TimeUnit.SECONDS)
        okHttpClient.readTimeout(60, TimeUnit.SECONDS)
        okHttpClient.writeTimeout(60, TimeUnit.SECONDS)

        return Retrofit.Builder()
            .baseUrl("https://dutapi.herokuapp.com")
            .client(okHttpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DutFuncApi::class.java)
    }
}