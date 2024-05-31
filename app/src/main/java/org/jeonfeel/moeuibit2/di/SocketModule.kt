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
import org.jeonfeel.moeuibit2.data.network.websocket.thunder.service.UpBitSocketService
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SocketModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class SocketType

    @Singleton
    @Provides
    @SocketType
    fun provideOkHttpClient(): OkHttpClient {
        val httpLoggingInterceptor =
            HttpLoggingInterceptor { message -> Logger.d(message) }
                .setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .pingInterval(
                10,
                TimeUnit.SECONDS
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideSocketService(
        @SocketType okHttpClient: OkHttpClient,
        @ApplicationContext context: Context
    ): UpBitSocketService {
        return thunder {
            setWebSocketFactory(okHttpClient.makeWebSocketCore("wss://api.upbit.com/websocket/v1"))
            setApplicationContext(context)
            setConverterType(ConverterType.Serialization)
        }.create()
    }
}
