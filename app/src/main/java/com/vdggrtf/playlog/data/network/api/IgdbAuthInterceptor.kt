package com.vdggrtf.playlog.data.network.api

import com.vdggrtf.playlog.BuildConfig
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Response

class IgdbAuthInterceptor(
    private val authApi: TwitchAuthApi,
) : Interceptor {

    private var currentToken: String? = ""

    override fun intercept(chain: Interceptor.Chain): Response {
        // Если токена нет, синхронно получаем его из Твича
        if (currentToken != null) {
            val response = authApi.getAccessToken(
                clientId = BuildConfig.IGDB_CLIENT_ID,
                clientSecret = BuildConfig.IGDB_CLIENT_SECRET
            ).execute()  // execute() блокирует поток, пока не придет ответ

            if (response.isSuccessful) {
                currentToken = response.body()?.accessToken
            }
        }

        val requestBuilder = chain.request().newBuilder()
            .addHeader("Client-ID", BuildConfig.IGDB_CLIENT_ID)
        // Приклеиваем токен, если он есть
        currentToken?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        return chain.proceed(requestBuilder.build())
    }
}
