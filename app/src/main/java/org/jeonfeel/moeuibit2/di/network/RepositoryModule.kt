package org.jeonfeel.moeuibit2.di.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.jeonfeel.moeuibit2.data.network.retrofit.service.AlternativeService
import org.jeonfeel.moeuibit2.data.network.retrofit.service.BitThumbService
import org.jeonfeel.moeuibit2.data.network.retrofit.service.CoinCapIOService
import org.jeonfeel.moeuibit2.data.network.retrofit.service.GitJsonService
import org.jeonfeel.moeuibit2.data.network.retrofit.service.USDService
import org.jeonfeel.moeuibit2.data.network.retrofit.service.UpBitService
import org.jeonfeel.moeuibit2.data.repository.network.AlternativeRepository
import org.jeonfeel.moeuibit2.data.repository.network.BiThumbRepository
import org.jeonfeel.moeuibit2.data.repository.network.CoinCapIORepository
import org.jeonfeel.moeuibit2.data.repository.network.GitJsonRepository
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
        bitThumbService: BitThumbService
    ): BiThumbRepository {
        return BiThumbRepository(bitThumbService)
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

    @Singleton
    @Provides
    fun provideAlternativeRepository(
        alternativeService: AlternativeService,
    ): AlternativeRepository {
        return AlternativeRepository(alternativeService)
    }

    @Singleton
    @Provides
    fun provideGitJsonRepository(
        gitJsonService: GitJsonService,
    ): GitJsonRepository {
        return GitJsonRepository(gitJsonService)
    }
}