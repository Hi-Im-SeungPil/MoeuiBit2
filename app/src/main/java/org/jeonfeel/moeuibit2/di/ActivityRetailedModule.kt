package org.jeonfeel.moeuibit2.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import org.jeonfeel.moeuibit2.repository.remote.RemoteRepository
import org.jeonfeel.moeuibit2.util.XAxisValueFormatter
import org.jeonfeel.moeuibit2.viewmodel.coindetail.usecase.ChartUseCase

@Module
@InstallIn(ActivityRetainedComponent::class)
class ActivityRetailedModule {

    @Provides
    fun provideXAxisValueFormatter(): XAxisValueFormatter {
        return XAxisValueFormatter()
    }

    @Provides
    fun provideChartUseCase(remoteRepository: RemoteRepository,xAxisValueFormatter: XAxisValueFormatter): ChartUseCase {
        return ChartUseCase(remoteRepository,xAxisValueFormatter)
    }
}