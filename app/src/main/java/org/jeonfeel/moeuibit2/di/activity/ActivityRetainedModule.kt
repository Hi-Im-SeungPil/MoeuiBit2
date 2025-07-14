package org.jeonfeel.moeuibit2.di.activity

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import org.jeonfeel.moeuibit2.data.local.preferences.PreferencesManager
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.data.repository.network.UpbitRepository
import org.jeonfeel.moeuibit2.data.usecase.BiThumbChartUsecase
import org.jeonfeel.moeuibit2.data.usecase.UpbitChartUseCase
import org.jeonfeel.moeuibit2.ui.coindetail.chart.utils.XAxisValueFormatter
import org.jeonfeel.moeuibit2.ui.coindetail.chart.utils.bithumb.BiThumbChart
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
    fun provideUpbitChart(
        localRepository: LocalRepository,
        upbitChartUseCase: UpbitChartUseCase,
        prefrenceManager: PreferencesManager
    ): UpbitChart {
        return UpbitChart(
            localRepository = localRepository,
            upbitChartUseCase = upbitChartUseCase,
            preferenceManager = prefrenceManager
        )
    }

    @Provides
    fun provideBithumbChart(
        biThumbChartUsecase: BiThumbChartUsecase,
        prefrenceManager: PreferencesManager
    ): BiThumbChart {
        return BiThumbChart(
            biThumbChartUsecase = biThumbChartUsecase,
            preferenceManager = prefrenceManager
        )
    }

    @Provides
    fun provideCoinInfo(): CoinInfo {
        return CoinInfo()
    }
}