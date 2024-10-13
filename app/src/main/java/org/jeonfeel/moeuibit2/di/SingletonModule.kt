package org.jeonfeel.moeuibit2.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.data.repository.network.UpbitRepository
import org.jeonfeel.moeuibit2.data.usecase.UpbitChartUseCase
import org.jeonfeel.moeuibit2.ui.coindetail.chart.utils.upbit.Chart
import org.jeonfeel.moeuibit2.ui.coindetail.coininfo.utils.CoinInfo
import org.jeonfeel.moeuibit2.ui.coindetail.order.utils.CoinOrder
import org.jeonfeel.moeuibit2.utils.manager.PreferencesManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SingletonModule {

    @Singleton
    @Provides
    fun provideChart(
        remoteRepository: UpbitRepository,
        localRepository: LocalRepository,
        upbitChartUseCase: UpbitChartUseCase
    ): Chart {
        return Chart(
            upbitRepository = remoteRepository,
            localRepository = localRepository,
            upbitChartUseCase = upbitChartUseCase
        )
    }

    @Provides
    fun provideCoinOrder(
        localRepository: LocalRepository,
        prefrenceManager: PreferencesManager
    ): CoinOrder {
        return CoinOrder(
            preferenceManager = prefrenceManager,
            localRepository = localRepository
        )
    }

    @Provides
    fun provideCoinInfo(): CoinInfo {
        return CoinInfo()
    }
}