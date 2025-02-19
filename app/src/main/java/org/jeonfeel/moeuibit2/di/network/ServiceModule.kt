package org.jeonfeel.moeuibit2.di.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.jeonfeel.moeuibit2.data.network.retrofit.api.BinanceService
import org.jeonfeel.moeuibit2.data.network.retrofit.api.BitThumbService
import org.jeonfeel.moeuibit2.data.network.retrofit.api.CoinCapIOService
import org.jeonfeel.moeuibit2.data.network.retrofit.api.USDService
import org.jeonfeel.moeuibit2.data.network.retrofit.api.UpBitService
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ServiceModule {

    @Singleton
    @Provides
    fun provideUpBitService(@RetrofitModule.UpBitRetrofit retrofit: Retrofit): UpBitService {
        return retrofit.create(UpBitService::class.java)
    }

    @Singleton
    @Provides
    fun provideBitThumbService(@RetrofitModule.UpBitRetrofit retrofit: Retrofit): BitThumbService {
        return retrofit.create(BitThumbService::class.java)
    }

    @Singleton
    @Provides
    fun provideUSDService(@RetrofitModule.UsdRetrofit retrofit: Retrofit): USDService {
        return retrofit.create(USDService::class.java)
    }

    @Singleton
    @Provides
    fun provideCoinCapIOService(@RetrofitModule.CoinCapIORetrofit retrofit: Retrofit): CoinCapIOService {
        return retrofit.create(CoinCapIOService::class.java)
    }

    @Singleton
    @Provides
    fun provideBinanceService(@RetrofitModule.UpBitRetrofit retrofit: Retrofit): BinanceService {
        return retrofit.create(BinanceService::class.java)
    }
}