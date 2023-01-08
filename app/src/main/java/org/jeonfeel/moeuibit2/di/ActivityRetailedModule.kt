package org.jeonfeel.moeuibit2.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import org.jeonfeel.moeuibit2.activity.coindetail.viewmodel.usecase.ChartUseCase
import org.jeonfeel.moeuibit2.activity.coindetail.viewmodel.usecase.OrderScreenUseCase
import org.jeonfeel.moeuibit2.activity.main.viewmodel.usecase.ExchangeUseCase
import org.jeonfeel.moeuibit2.manager.PreferenceManager
import org.jeonfeel.moeuibit2.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.repository.remote.RemoteRepository
import org.jeonfeel.moeuibit2.util.XAxisValueFormatter

@Module
@InstallIn(ActivityRetainedComponent::class)
class ActivityRetailedModule {

    @Provides
    fun provideXAxisValueFormatter(): XAxisValueFormatter {
        return XAxisValueFormatter()
    }

    @Provides
    fun provideChartUseCase(
        remoteRepository: RemoteRepository,
        xAxisValueFormatter: XAxisValueFormatter,
        localRepository: LocalRepository
    ): ChartUseCase {
        return ChartUseCase(remoteRepository, xAxisValueFormatter, localRepository)
    }

    @Provides
    fun provideOrderScreenUseCase(
        remoteRepository: RemoteRepository,
        localRepository: LocalRepository,
        prefrenceManager: PreferenceManager
    ): OrderScreenUseCase {
        return OrderScreenUseCase(remoteRepository, localRepository,prefrenceManager)
    }

    @Provides
    fun provideExchangeUseCase(
        remoteRepository: RemoteRepository,
        localRepository: LocalRepository
    ): ExchangeUseCase {
        return ExchangeUseCase(localRepository,remoteRepository)
    }
}