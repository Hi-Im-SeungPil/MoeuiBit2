package org.jeonfeel.moeuibit2.di

import android.content.Context
import com.jeremy.thunder.event.converter.ConverterType
import com.jeremy.thunder.makeWebSocketCore
import com.jeremy.thunder.thunder
import com.orhanobut.logger.Logger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.jeonfeel.moeuibit2.data.network.websocket.thunder.service.upbit.UpbitOrderBookSocketService
import org.jeonfeel.moeuibit2.data.network.websocket.thunder.service.upbit.UpBitExchangeSocketService
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.data.repository.network.UpbitRepository
import org.jeonfeel.moeuibit2.data.usecase.UpbitChartUseCase
import org.jeonfeel.moeuibit2.ui.coindetail.chart.utils.upbit.Chart
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SocketModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class ExchangeTickerSocketType

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class ExchangeTickerSocket

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class CoinDetailTickerSocketType

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class CoinDetailTickerSocket

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class CoinDetailOrderBookSocketType

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class CoinDetailOrderBookSocket

    @Singleton
    @Provides
    @ExchangeTickerSocketType
    fun provideExchangeOkHttpClient(): OkHttpClient {
        val httpLoggingInterceptor =
            HttpLoggingInterceptor { message -> Logger.d(message) }
                .setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .pingInterval(
                10,
                TimeUnit.SECONDS
            ).build()
    }

    @Provides
    @Singleton
    @ExchangeTickerSocket
    fun provideExchangeTickerSocketService(
        @ExchangeTickerSocketType okHttpClient: OkHttpClient,
        @ApplicationContext context: Context
    ): UpBitExchangeSocketService {
        return thunder {
            setWebSocketFactory(okHttpClient.makeWebSocketCore("wss://api.upbit.com/websocket/v1"))
            setApplicationContext(context)
            setConverterType(ConverterType.Serialization)
        }.create()
    }

    @Singleton
    @Provides
    @CoinDetailTickerSocketType
    fun provideCoinDetailOkHttpClient(): OkHttpClient {
        val httpLoggingInterceptor =
            HttpLoggingInterceptor { message -> Logger.d(message) }
                .setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .pingInterval(
                10,
                TimeUnit.SECONDS
            ).build()
    }

    @Provides
    @Singleton
    @CoinDetailTickerSocket
    fun provideCoinDetailTickerSocketService(
        @CoinDetailTickerSocketType okHttpClient: OkHttpClient,
        @ApplicationContext context: Context
    ): UpBitExchangeSocketService {
        return thunder {
            setWebSocketFactory(okHttpClient.makeWebSocketCore("wss://api.upbit.com/websocket/v1"))
            setApplicationContext(context)
            setConverterType(ConverterType.Serialization)
        }.create()
    }

    @Singleton
    @Provides
    @CoinDetailOrderBookSocketType
    fun provideOrderBookOkHttpClient(): OkHttpClient {
        val httpLoggingInterceptor =
            HttpLoggingInterceptor { message -> Logger.d(message) }
                .setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .pingInterval(
                10,
                TimeUnit.SECONDS
            ).build()
    }

    @Provides
    @Singleton
    @CoinDetailOrderBookSocket
    fun provideCoinDetailOrderBookSocketService(
        @CoinDetailOrderBookSocketType okHttpClient: OkHttpClient,
        @ApplicationContext context: Context
    ): UpbitOrderBookSocketService {
        return thunder {
            setWebSocketFactory(okHttpClient.makeWebSocketCore("wss://api.upbit.com/websocket/v1"))
            setApplicationContext(context)
            setConverterType(ConverterType.Serialization)
        }.create()
    }
}
