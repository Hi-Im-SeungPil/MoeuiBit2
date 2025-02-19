package org.jeonfeel.moeuibit2.di.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.jeonfeel.moeuibit2.data.network.retrofit.api.BitThumbService
import org.jeonfeel.moeuibit2.data.network.retrofit.api.CoinCapIOService
import org.jeonfeel.moeuibit2.data.network.retrofit.api.USDService
import org.jeonfeel.moeuibit2.data.network.retrofit.api.UpBitService
import org.jeonfeel.moeuibit2.data.repository.network.BitthumbRepository
import org.jeonfeel.moeuibit2.data.repository.network.CoinCapIORepository
import org.jeonfeel.moeuibit2.data.repository.network.USDRepository
import org.jeonfeel.moeuibit2.data.repository.network.UpbitRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Singleton
    @Provides
    fun provideUpBitRepository(
        upBitService: UpBitService,
    ): UpbitRepository {
        return UpbitRepository(upBitService)
    }

    @Singleton
    @Provides
    fun provideBitThumbRepository(
        bitThumbService: BitThumbService,
    ): BitthumbRepository {
        return BitthumbRepository(bitThumbService)
    }

    @Singleton
    @Provides
    fun provideUSDRepository(
        usdService: USDService,
    ): USDRepository {
        return USDRepository(usdService)
    }

    @Singleton
    @Provides
    fun provideCoinCapIORepository(
        coinCapIOService: CoinCapIOService,
    ): CoinCapIORepository {
        return CoinCapIORepository(coinCapIOService)
    }
}