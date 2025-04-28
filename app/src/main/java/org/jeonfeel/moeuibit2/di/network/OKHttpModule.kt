package org.jeonfeel.moeuibit2.di.network

import com.orhanobut.logger.Logger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class OKHttpModule {
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class RetrofitOkHttpClient

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class SocketOkHttpClient

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class RetrofitOKHttpClientNoConnectionPool

    @Singleton
    @Provides
    @RetrofitOkHttpClient
    fun provideRetrofitOkHttpClient(): OkHttpClient {
        val connectionPool = ConnectionPool(0, 700L, TimeUnit.MILLISECONDS)
        val httpLoggingInterceptor =
            HttpLoggingInterceptor { message ->
                Logger.d(message)
            }.setLevel(HttpLoggingInterceptor.Level.BODY)

        val httpClient = OkHttpClient.Builder()

        return httpClient.connectionPool(connectionPool)
            .addInterceptor(httpLoggingInterceptor)
            .retryOnConnectionFailure(true)
            .connectTimeout(3, TimeUnit.MINUTES)
            .readTimeout(3, TimeUnit.MINUTES)
            .build()
    }

    @Singleton
    @Provides
    @SocketOkHttpClient
    fun provideSocketOkHttpClient(): OkHttpClient {
        val httpLoggingInterceptor =
            HttpLoggingInterceptor { message -> Logger.d(message) }
                .setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    @RetrofitOKHttpClientNoConnectionPool
    fun provideRetrofitOkHttpClientNoConnectionPool(): OkHttpClient {
        val httpLoggingInterceptor =
            HttpLoggingInterceptor { message ->
//                Logger.d(message)
            }.setLevel(HttpLoggingInterceptor.Level.BODY)

        val httpClient = OkHttpClient.Builder()

        return httpClient
            .addInterceptor(httpLoggingInterceptor)
            .retryOnConnectionFailure(true)
            .connectTimeout(3, TimeUnit.MINUTES)
            .readTimeout(3, TimeUnit.MINUTES)
            .build()
    }
}