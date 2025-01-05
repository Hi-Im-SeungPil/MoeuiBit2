package org.jeonfeel.moeuibit2.di

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.orhanobut.logger.Logger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.ConnectionPool
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.jeonfeel.moeuibit2.constants.retrofitBaseUrl
import org.jeonfeel.moeuibit2.data.network.retrofit.api.BinanceService
import org.jeonfeel.moeuibit2.data.network.retrofit.api.BitThumbService
import org.jeonfeel.moeuibit2.data.network.retrofit.api.USDTService
import org.jeonfeel.moeuibit2.data.network.retrofit.api.UpBitService
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.data.repository.network.BitthumbRepository
import org.jeonfeel.moeuibit2.data.repository.network.UpbitRepository
import org.jeonfeel.moeuibit2.data.usecase.UpbitCoinDetailUseCase
import org.jeonfeel.moeuibit2.data.usecase.UpBitExchangeUseCase
import org.jeonfeel.moeuibit2.data.usecase.UpbitCoinOrderUseCase
import org.jeonfeel.moeuibit2.data.usecase.UpbitPortfolioUsecase
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class RetrofitOkHttpClient

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class SocketOkHttpClient

    @Singleton
    @Provides
    @RetrofitOkHttpClient
    fun provideRetrofitOkHttpClient(): OkHttpClient {
        val connectionPool = ConnectionPool(0, 700L, TimeUnit.MILLISECONDS)
        val httpLoggingInterceptor =
            HttpLoggingInterceptor { message ->
//                Logger.d(message)
            }.setLevel(HttpLoggingInterceptor.Level.BODY)

        val httpClient = OkHttpClient.Builder()

        return httpClient.connectionPool(connectionPool)
            .addInterceptor(httpLoggingInterceptor)
            .retryOnConnectionFailure(true)
            .connectTimeout(5, TimeUnit.MINUTES)
            .readTimeout(5, TimeUnit.MINUTES)
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

    @OptIn(ExperimentalSerializationApi::class)
    @Singleton
    @Provides
    fun provideUpBitRetrofit(
        @RetrofitOkHttpClient okHttpClient: OkHttpClient
    ): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(retrofitBaseUrl)
            .client(okHttpClient)
            .addConverterFactory(Json {
                ignoreUnknownKeys = true
                explicitNulls = false
            }.asConverterFactory(contentType))
            .build()
    }

    @Singleton
    @Provides
    fun provideUpBitRetrofitMarketCodeService(retrofit: Retrofit): UpBitService {
        return retrofit.create(UpBitService::class.java)
    }

    @Singleton
    @Provides
    fun provideBitThumbService(retrofit: Retrofit): BitThumbService {
        return retrofit.create(BitThumbService::class.java)
    }

    @Singleton
    @Provides
    fun provideBinanceService(retrofit: Retrofit): BinanceService {
        return retrofit.create(BinanceService::class.java)
    }

    @Singleton
    @Provides
    fun provideUSDTPriceService(retrofit: Retrofit): USDTService {
        return retrofit.create(USDTService::class.java)
    }

    @Singleton
    @Provides
    fun provideUpbitRepository(
        upBitService: UpBitService,
    ): UpbitRepository {
        return UpbitRepository(upBitService)
    }

    @Singleton
    @Provides
    fun provideBitthumbRepository(
        bitThumbService: BitThumbService
    ): BitthumbRepository {
        return BitthumbRepository(bitThumbService)
    }

    @Singleton
    @Provides
    fun provideUpbitUseCase(
        localRepository: LocalRepository,
        upbitRepository: UpbitRepository,
        @SocketOkHttpClient okHttpClient: OkHttpClient,
        @ApplicationContext context: Context
    ): UpBitExchangeUseCase {
        return UpBitExchangeUseCase(
            localRepository = localRepository,
            upbitRepository = upbitRepository,
            okHttpClient = okHttpClient,
            context = context
        )
    }

    @Singleton
    @Provides
    fun provideUpbitPortfolioUsecase(
        upbitRepository: UpbitRepository,
        localRepository: LocalRepository,
        @SocketOkHttpClient okHttpClient: OkHttpClient,
        @ApplicationContext context: Context
    ): UpbitPortfolioUsecase {
        return UpbitPortfolioUsecase(
            localRepository = localRepository,
            upbitRepository = upbitRepository,
            okHttpClient = okHttpClient,
            context = context
        )
    }

    @Provides
    fun providerCoinDetailUseCase(
        localRepository: LocalRepository,
        upbitRepository: UpbitRepository,
        @SocketOkHttpClient okHttpClient: OkHttpClient,
        @ApplicationContext context: Context
    ): UpbitCoinDetailUseCase {
        return UpbitCoinDetailUseCase(
            localRepository = localRepository,
            upbitRepository = upbitRepository,
            okHttpClient = okHttpClient,
            context = context
        )
    }

    @Singleton
    @Provides
    fun provideOrderBookUseCase(
        upbitRepository: UpbitRepository,
        localRepository: LocalRepository,
        @SocketOkHttpClient okHttpClient: OkHttpClient,
        @ApplicationContext context: Context
    ): UpbitCoinOrderUseCase {
        return UpbitCoinOrderUseCase(
            localRepository = localRepository,
            upbitRepository = upbitRepository,
            okHttpClient = okHttpClient,
            context = context
        )
    }
}