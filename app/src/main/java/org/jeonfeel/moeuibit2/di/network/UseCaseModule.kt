package org.jeonfeel.moeuibit2.di.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.data.repository.network.UpbitRepository
import org.jeonfeel.moeuibit2.data.usecase.UpBitExchangeUseCase
import org.jeonfeel.moeuibit2.data.usecase.UpbitCoinDetailUseCase
import org.jeonfeel.moeuibit2.data.usecase.UpbitCoinOrderUseCase
import org.jeonfeel.moeuibit2.data.usecase.UpbitPortfolioUsecase
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
}