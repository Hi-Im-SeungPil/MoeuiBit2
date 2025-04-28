package org.jeonfeel.moeuibit2.di.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.jeonfeel.moeuibit2.data.network.retrofit.service.AlternativeService
import org.jeonfeel.moeuibit2.data.network.retrofit.service.BinanceService
import org.jeonfeel.moeuibit2.data.network.retrofit.service.BitThumbService
import org.jeonfeel.moeuibit2.data.network.retrofit.service.CoinCapIOService
import org.jeonfeel.moeuibit2.data.network.retrofit.service.GitJsonService
import org.jeonfeel.moeuibit2.data.network.retrofit.service.USDService
import org.jeonfeel.moeuibit2.data.network.retrofit.service.UpBitService
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

    @Singleton
    @Provides
    fun provideAlternativeService(@RetrofitModule.AlternativeRetrofit retrofit: Retrofit): AlternativeService {
        return retrofit.create(AlternativeService::class.java)
    }

    @Singleton
    @Provides
    fun provideGitJsonService(@RetrofitModule.GitJsonRetrofit retrofit: Retrofit): GitJsonService {
        return retrofit.create(GitJsonService::class.java)
    }
}