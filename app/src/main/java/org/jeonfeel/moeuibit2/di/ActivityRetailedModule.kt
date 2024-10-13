package org.jeonfeel.moeuibit2.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import org.jeonfeel.moeuibit2.utils.manager.PreferencesManager
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.data.repository.network.UpbitRepository
import org.jeonfeel.moeuibit2.data.usecase.UpbitChartUseCase
import org.jeonfeel.moeuibit2.ui.coindetail.coininfo.utils.CoinInfo
import org.jeonfeel.moeuibit2.ui.coindetail.order.utils.CoinOrder
import org.jeonfeel.moeuibit2.ui.coindetail.chart.utils.XAxisValueFormatter
import org.jeonfeel.moeuibit2.ui.coindetail.chart.utils.upbit.Chart
import org.jeonfeel.moeuibit2.utils.manager.AdMobManager
import javax.inject.Singleton

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



//    @Provides
//    fun provideUpbitChartUsecase(
//        upbitRepository: UpbitRepository,
//        localRepository: LocalRepository
//    ): UpbitChartUseCase {
//        return UpbitChartUseCase(
//            upbitRepository = upbitRepository,
//            localRepository = localRepository
//        )
//    }

//    private val upbitRepository: UpbitRepository,
//    private val localRepository: LocalRepository,
//    private val upbitChartUseCase: UpbitChartUseCase
}