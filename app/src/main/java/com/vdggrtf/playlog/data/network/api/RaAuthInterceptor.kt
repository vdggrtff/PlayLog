package com.vdggrtf.playlog.data.network.api

import com.vdggrtf.playlog.BuildConfig
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Response

class RaAuthInterceptor : Interceptor {
    override fun intercept(chain: Chain): Response {
        val originRequest = chain.request()
        val originalUrl = originRequest.url

        val urlWithAuth = originalUrl.newBuilder()
            .addQueryParameter("z", BuildConfig.RA_USER)
            .addQueryParameter("y", BuildConfig.RA_API_KEY)
            .build()

        val request = originRequest.newBuilder().url(urlWithAuth).build()
        return chain.proceed(request)
    }
}