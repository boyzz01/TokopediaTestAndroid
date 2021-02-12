package com.tokopedia.maps.service

import com.tokopedia.maps.R
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiClient {
    companion object {
        fun getClient() : Retrofit {

           val BASE_URL = "https://restcountries-v1.p.rapidapi.com/name/"
            val apiKey = "7d60ce1a41msh35708c6f58ad8d1p14e1d8jsna6d5fa321e47"
            val apiHost = "restcountries-v1.p.rapidapi.com"

            val clientBuilder: OkHttpClient.Builder = buildClient()
            return Retrofit.Builder().
            baseUrl(BASE_URL)
                    .client(clientBuilder
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .writeTimeout(10, TimeUnit.SECONDS)
                            .readTimeout(10, TimeUnit.SECONDS)
                            .addInterceptor { chain ->
                                val newRequest = chain.request().newBuilder()
                                        .addHeader("x-rapidapi-key", apiKey)
                                        .addHeader("x-rapidapi-host", apiHost)
                                        .build()
                                chain.proceed(newRequest)
                            }
                            .retryOnConnectionFailure(true)
                            .build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()

        }

        private fun buildClient(): OkHttpClient.Builder {
            val clientBuilder = OkHttpClient.Builder()
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            clientBuilder.addInterceptor(loggingInterceptor)
                    .connectTimeout(5 , TimeUnit.MINUTES)
            return clientBuilder
        }
    }


}