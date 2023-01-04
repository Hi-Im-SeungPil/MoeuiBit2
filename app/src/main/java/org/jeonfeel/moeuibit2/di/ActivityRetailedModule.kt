package org.jeonfeel.moeuibit2.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import org.jeonfeel.moeuibit2.ui.viewmodels.ChartUseCase
import org.jeonfeel.moeuibit2.ui.viewmodels.OrderScreenUseCase
import org.jeonfeel.moeuibit2.ui.viewmodels.ExchangeUseCase
import org.jeonfeel.moeuibit2.utils.manager.PreferenceManager
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.data.repository.remote.RemoteRepository
import org.jeonfeel.moeuibit2.utils.XAxisValueFormatter

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