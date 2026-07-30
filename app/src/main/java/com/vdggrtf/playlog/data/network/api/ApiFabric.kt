package com.vdggrtf.playlog.data.network.api

import com.vdggrtf.playlog.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiFabric {

    @Provides
    @Singleton
    fun provideOkhttpClient(): OkHttpClient {
        val apiKeyInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val originalUrl = originalRequest.url

            val newUrl =
                originalUrl.newBuilder().addQueryParameter("key", BuildConfig.RAWG_API_KEY).build()

            val newRequest = originalRequest.newBuilder().url(newUrl).build()

            chain.proceed(newRequest)
        }

        return OkHttpClient.Builder().addInterceptor(apiKeyInterceptor).build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.RAWG_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideRawApi(retrofit: Retrofit): RawgApi {
        return retrofit.create(RawgApi::class.java)
    }


    @Provides
    @Singleton
    fun provideCheapSharkApi(): CheapSharkApi {
        val sharkInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .header("User-Agent", "PlayLog/1.0 (playlogandroidapp@gmail.com)")
                .build()
            chain.proceed(request)
        }

        val sharkClient = OkHttpClient.Builder()
            .addInterceptor(sharkInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://www.cheapshark.com/api/1.0/")
            .client(sharkClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CheapSharkApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRetroAchievementsApi(): RetroAchievementsApi {
        val client = OkHttpClient.Builder()
            .addInterceptor(RaAuthInterceptor()) // Наш шпион с ключами
            .build()

        return Retrofit.Builder()
            .baseUrl("https://retroachievements.org/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetroAchievementsApi::class.java)
    }
}