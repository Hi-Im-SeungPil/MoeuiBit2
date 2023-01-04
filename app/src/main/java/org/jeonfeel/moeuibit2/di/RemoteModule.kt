package org.jeonfeel.moeuibit2.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.jeonfeel.moeuibit2.constants.retrofitBaseUrl
import org.jeonfeel.moeuibit2.data.remote.retrofit.api.BinanceService
import org.jeonfeel.moeuibit2.data.remote.retrofit.api.USDTService
import org.jeonfeel.moeuibit2.data.remote.retrofit.api.UpBitService
import org.jeonfeel.moeuibit2.data.repository.remote.RemoteRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RemoteModule {

    @Singleton
    @Provides
    fun provideUpBitRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(retrofitBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
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
    fun provideRemoteRepository(upBitService: UpBitService, usdtService: USDTService, binanceService: BinanceService): RemoteRepository {
        return RemoteRepository(upBitService, usdtService, binanceService)
    }
}