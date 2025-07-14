package org.jeonfeel.moeuibit2.di.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.data.repository.network.AlternativeRepository
import org.jeonfeel.moeuibit2.data.repository.network.BiThumbRepository
import org.jeonfeel.moeuibit2.data.repository.network.CoinCapIORepository
import org.jeonfeel.moeuibit2.data.repository.network.GitJsonRepository
import org.jeonfeel.moeuibit2.data.repository.network.USDRepository
import org.jeonfeel.moeuibit2.data.repository.network.UpbitRepository
import org.jeonfeel.moeuibit2.data.usecase.BiThumbChartUsecase
import org.jeonfeel.moeuibit2.data.usecase.BiThumbCoinDetailUseCase
import org.jeonfeel.moeuibit2.data.usecase.BiThumbPortfolioUsecase
import org.jeonfeel.moeuibit2.data.usecase.BitThumbUseCase
import org.jeonfeel.moeuibit2.data.usecase.UpBitExchangeUseCase
import org.jeonfeel.moeuibit2.data.usecase.UpbitCoinDetailUseCase
import org.jeonfeel.moeuibit2.data.usecase.UpbitCoinOrderUseCase
import org.jeonfeel.moeuibit2.data.usecase.UpbitPortfolioUsecase
import org.jeonfeel.moeuibit2.ui.coindetail.coininfo.CoinInfoUseCase
import org.jeonfeel.moeuibit2.data.usecase.CoinMarketConditionUseCase
import org.jeonfeel.moeuibit2.data.usecase.MiningUseCase
import org.jeonfeel.moeuibit2.utils.manager.CacheManager
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class UseCaseModule {

    @Singleton
    @Provides
    fun provideUpbitUseCase(
        localRepository: LocalRepository,
        upbitRepository: UpbitRepository,
    ): UpBitExchangeUseCase {
        return UpBitExchangeUseCase(
            localRepository = localRepository,
            upbitRepository = upbitRepository,
        )
    }

    @Singleton
    @Provides
    fun provideBitThumbUseCase(
        localRepository: LocalRepository,
        bitthumbRepository: BiThumbRepository,
        cacheManager: CacheManager
    ): BitThumbUseCase {
        return BitThumbUseCase(
            localRepository = localRepository,
            bitThumbRepository = bitthumbRepository,
            cacheManager = cacheManager
        )
    }

    @Singleton
    @Provides
    fun provideUpbitPortfolioUsecase(
        upbitRepository: UpbitRepository,
        localRepository: LocalRepository,
    ): UpbitPortfolioUsecase {
        return UpbitPortfolioUsecase(
            localRepository = localRepository,
            upbitRepository = upbitRepository,
        )
    }

    @Provides
    fun providerCoinDetailUseCase(
        localRepository: LocalRepository,
        upbitRepository: UpbitRepository,
    ): UpbitCoinDetailUseCase {
        return UpbitCoinDetailUseCase(
            localRepository = localRepository,
            upbitRepository = upbitRepository,
        )
    }

    @Provides
    fun provideBiThumbCoinDetailUseCase(
        localRepository: LocalRepository,
        bitThumbRepository: BiThumbRepository,
    ): BiThumbCoinDetailUseCase {
        return BiThumbCoinDetailUseCase(
            localRepository = localRepository,
            biThumbRepository = bitThumbRepository
        )
    }

    @Singleton
    @Provides
    fun provideOrderBookUseCase(
        upbitRepository: UpbitRepository,
        localRepository: LocalRepository,
    ): UpbitCoinOrderUseCase {
        return UpbitCoinOrderUseCase(
            localRepository = localRepository,
            upbitRepository = upbitRepository
        )
    }

    @Singleton
    @Provides
    fun provideCoinInfoUseCase(
        usdRepository: USDRepository,
        coinCapIORepository: CoinCapIORepository,
    ): CoinInfoUseCase {
        return CoinInfoUseCase(
            usdRepository = usdRepository,
            coinCapIORepository = coinCapIORepository
        )
    }

    @Singleton
    @Provides
    fun provideCoinMarketConditionUseCase(
        alternativeRepository: AlternativeRepository,
    ): CoinMarketConditionUseCase {
        return CoinMarketConditionUseCase(alternativeRepository = alternativeRepository)
    }

    @Singleton
    @Provides
    fun provideMiningUseCase(
        gitJsonRepository: GitJsonRepository,
    ): MiningUseCase {
        return MiningUseCase(gitJsonRepository)
    }

    @Singleton
    @Provides
    fun provideBiThumbPortfolioUsecase(
        bitThumbRepository: BiThumbRepository,
        localRepository: LocalRepository
    ): BiThumbPortfolioUsecase {
        return BiThumbPortfolioUsecase(
            biThumbRepository = bitThumbRepository,
            localRepository = localRepository
        )
    }

    @Singleton
    @Provides
    fun provideBiThumbChartUsecase(
        bitThumbRepository: BiThumbRepository,
        localRepository: LocalRepository
    ): BiThumbChartUsecase {
        return BiThumbChartUsecase(
            biThumbRepository = bitThumbRepository,
            localRepository = localRepository
        )
    }
}