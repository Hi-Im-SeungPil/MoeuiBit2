package org.jeonfeel.moeuibit2.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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
import org.jeonfeel.moeuibit2.data.network.websocket.thunder.service.upbit.UpbitOrderBookSocketService
import org.jeonfeel.moeuibit2.data.network.websocket.thunder.service.upbit.UpBitExchangeSocketService
import org.jeonfeel.moeuibit2.data.network.websocket.thunder.service.upbit.UpbitCoinDetailSocketService
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.data.repository.network.BitthumbRepository
import org.jeonfeel.moeuibit2.data.repository.network.UpbitRepository
import org.jeonfeel.moeuibit2.data.usecase.UpbitExchangeUseCase
import org.jeonfeel.moeuibit2.data.repository.network.RemoteRepository
import org.jeonfeel.moeuibit2.data.usecase.UpbitCoinDetailUseCase
import org.jeonfeel.moeuibit2.data.usecase.UpbitCoinOrderUseCase
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class NetowrkModule {

    @Singleton
    @Provides
    fun provideUpBitOkHttpClient(): OkHttpClient {
        val connectionPool = ConnectionPool(0, 700L, TimeUnit.MILLISECONDS)
        val httpLoggingInterceptor =
            HttpLoggingInterceptor { message ->
//                Logger.d(message)
            }
                .setLevel(HttpLoggingInterceptor.Level.BODY)
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
    fun provideUpBitRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(retrofitBaseUrl)
            .client(okHttpClient)
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()
    }

    @Singleton
    @Provides
    fun provideUpBitRetrofitMarketCodeService(retrofit: Retrofit): UpBitService {
        return retrofit.create(UpBitService::class.java)
    }

    @Singleton
    @Provides
    fun provideUSDTPriceService(retrofit: Retrofit): USDTService {
        return retrofit.create(USDTService::class.java)
    }

    @Singleton
    @Provides
    fun provideBinanceService(retrofit: Retrofit): BinanceService {
        return retrofit.create(BinanceService::class.java)
    }

    @Singleton
    @Provides
    fun provideBitThumbService(retrofit: Retrofit): BitThumbService {
        return retrofit.create(BitThumbService::class.java)
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
    fun providerCoinDetailUseCase(
        localRepository: LocalRepository,
        upbitRepository: UpbitRepository,
        @SocketModule.CoinDetailSocket upbitSocketService: UpbitCoinDetailSocketService
    ): UpbitCoinDetailUseCase {
        return UpbitCoinDetailUseCase(localRepository, upbitRepository, upbitSocketService)
    }

    @Singleton
    @Provides
    fun provideUpbitUseCase(
        localRepository: LocalRepository,
        upbitRepository: UpbitRepository,
        @SocketModule.ExchangeTickerSocket upBitSocketService: UpBitExchangeSocketService,
    ): UpbitExchangeUseCase {
        return UpbitExchangeUseCase(upbitRepository, localRepository, upBitSocketService)
    }

    @Singleton
    @Provides
    fun provideOrderBookUseCase(
        upbitRepository: UpbitRepository,
        localRepository: LocalRepository,
        @SocketModule.CoinDetailOrderBookSocket upBitSocketService: UpbitOrderBookSocketService
    ): UpbitCoinOrderUseCase {
        return UpbitCoinOrderUseCase(upbitRepository, localRepository, upBitSocketService)
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
    fun provideRemoteRepository(
        upBitService: UpBitService,
        usdtService: USDTService,
        binanceService: BinanceService,
        bitThumbService: BitThumbService
    ): RemoteRepository {
        return RemoteRepository(upBitService, usdtService, binanceService, bitThumbService)
    }
}