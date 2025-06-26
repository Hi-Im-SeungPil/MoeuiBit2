package org.jeonfeel.moeuibit2.di.activity

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import org.jeonfeel.moeuibit2.data.local.preferences.PreferencesManager
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.data.repository.network.UpbitRepository
import org.jeonfeel.moeuibit2.data.usecase.UpbitChartUseCase
import org.jeonfeel.moeuibit2.ui.coindetail.chart.utils.XAxisValueFormatter
import org.jeonfeel.moeuibit2.ui.coindetail.chart.utils.upbit.UpbitChart
import org.jeonfeel.moeuibit2.ui.coindetail.coininfo.utils.CoinInfo
import org.jeonfeel.moeuibit2.utils.manager.AdMobManager

@Module
@InstallIn(ActivityRetainedComponent::class)
class ActivityRetailedModule {

    @Provides
    fun provideXAxisValueFormatter(): XAxisValueFormatter {
        return XAxisValueFormatter()
    }

    @Provides
    fun provideAdMobManager(): AdMobManager {
        return AdMobManager()
    }

    @Provides
    fun provideChart(
        remoteRepository: UpbitRepository,
        localRepository: LocalRepository,
        upbitChartUseCase: UpbitChartUseCase,
        prefrenceManager: PreferencesManager
    ): UpbitChart {
        return UpbitChart(
            upbitRepository = remoteRepository,
            localRepository = localRepository,
            upbitChartUseCase = upbitChartUseCase,
            preferenceManager = prefrenceManager
        )
    }

    @Provides
    fun provideCoinInfo(): CoinInfo {
        return CoinInfo()
    }
}