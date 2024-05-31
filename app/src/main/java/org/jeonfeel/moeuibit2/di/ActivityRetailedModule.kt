package org.jeonfeel.moeuibit2.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import org.jeonfeel.moeuibit2.utils.manager.PreferenceManager
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.ui.coindetail.coininfo.utils.CoinInfo
import org.jeonfeel.moeuibit2.ui.coindetail.order.utils.CoinOrder
import org.jeonfeel.moeuibit2.ui.coindetail.chart.utils.XAxisValueFormatter
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

//    @Provides
//    fun provideChart(
//        remoteRepository: RemoteRepository,
//        localRepository: LocalRepository
//    ): Chart {
//        return Chart(
//            remoteRepository = remoteRepository,
//            localRepository = localRepository
//        )
//    }

    @Provides
    fun provideCoinOrder(
        localRepository: LocalRepository,
        prefrenceManager: PreferenceManager
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