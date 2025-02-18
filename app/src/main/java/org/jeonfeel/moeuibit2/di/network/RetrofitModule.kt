package org.jeonfeel.moeuibit2.di.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.jeonfeel.moeuibit2.constants.COIN_CAP_IO_BASE_URL
import org.jeonfeel.moeuibit2.constants.UPBIT_BASE_URL
import org.jeonfeel.moeuibit2.constants.USD_BASE_URL
import retrofit2.Retrofit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RetrofitModule {
    private val converterFactory = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }.asConverterFactory("application/json".toMediaType())

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class UpBitRetrofit

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class UsdRetrofit

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class CoinCapIORetrofit

    @Singleton
    @Provides
    @UpBitRetrofit
    fun provideUpBitRetrofit(
        @OKHttpModule.RetrofitOkHttpClient okHttpClient: OkHttpClient,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(UPBIT_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .build()
    }

    @Singleton
    @Provides
    @UsdRetrofit
    fun provideUsdRetrofit(
        @OKHttpModule.RetrofitOKHttpClientNoConnectionPool okHttpClient: OkHttpClient,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(USD_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .build()
    }

    @Singleton
    @Provides
    @CoinCapIORetrofit
    fun provideCoinCapIORetrofit(
        @OKHttpModule.RetrofitOKHttpClientNoConnectionPool okHttpClient: OkHttpClient,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(COIN_CAP_IO_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .build()
    }
}