package org.jeonfeel.moeuibit2.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import org.jeonfeel.moeuibit2.utils.manager.PreferenceManager
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.data.repository.remote.RemoteRepository
import org.jeonfeel.moeuibit2.ui.coindetail.chart.utils.Chart
import org.jeonfeel.moeuibit2.ui.coindetail.order.CoinOrder
import org.jeonfeel.moeuibit2.utils.XAxisValueFormatter
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
        remoteRepository: RemoteRepository,
        localRepository: LocalRepository
    ): Chart {
        return Chart(remoteRepository, localRepository)
    }

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
}