package com.eurosportdemo.app.di

import com.eurosportdemo.app.BuildConfig
import com.eurosportdemo.app.data.api.Webservice
import com.google.gson.GsonBuilder
import com.grapesnberries.curllogger.CurlLoggerInterceptor
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object WebserviceModule {

    @Provides
    @Singleton
    fun providesWebservice(): Webservice {
        val client = OkHttpClient.Builder()

        if (BuildConfig.LOG_CURL) {
            client.addInterceptor(CurlLoggerInterceptor())
        }

        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client.build())
            .build().create(Webservice::class.java)
    }
}