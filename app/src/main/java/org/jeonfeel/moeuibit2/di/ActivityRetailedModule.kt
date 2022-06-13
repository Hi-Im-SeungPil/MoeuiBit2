package org.jeonfeel.moeuibit2.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import org.jeonfeel.moeuibit2.repository.remote.RemoteRepository
import org.jeonfeel.moeuibit2.util.XAxisValueFormatter
import org.jeonfeel.moeuibit2.activity.coindetail.viewmodel.usecase.ChartUseCase
import org.jeonfeel.moeuibit2.activity.coindetail.viewmodel.usecase.OrderScreenUseCase
import org.jeonfeel.moeuibit2.repository.local.LocalRepository

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
        localRepository: LocalRepository
    ): OrderScreenUseCase {
        return OrderScreenUseCase(remoteRepository, localRepository)
    }
}