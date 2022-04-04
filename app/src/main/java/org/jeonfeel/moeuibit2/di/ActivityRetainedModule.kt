package org.jeonfeel.moeuibit2.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import org.jeonfeel.moeuibit2.data.remote.retrofit.api.UpBitService
import org.jeonfeel.moeuibit2.repository.ExchangeViewModelRepository
import org.jeonfeel.moeuibit2.repository.CoinDetailRepository

@Module
@InstallIn(ActivityRetainedComponent::class)
class ActivityRetainedModule {

    @Provides
    fun exchangeRepository(upBitService: UpBitService): ExchangeViewModelRepository {
        return ExchangeViewModelRepository(upBitService)
    }

    @Provides
    fun orderBookRepository(upBitService: UpBitService): CoinDetailRepository {
        return CoinDetailRepository(upBitService)
    }

}