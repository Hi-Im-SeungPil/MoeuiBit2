package org.jeonfeel.moeuibit2.di

import android.content.Context
import com.jeremy.thunder.event.converter.ConverterType
import com.jeremy.thunder.makeWebSocketCore
import com.jeremy.thunder.thunder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import org.jeonfeel.moeuibit2.data.local.preferences.PreferencesManager
import org.jeonfeel.moeuibit2.data.network.websocket.thunder.service.upbit.UpbitCoinDetailSocketService
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.data.repository.network.UpbitRepository
import org.jeonfeel.moeuibit2.data.usecase.UpbitChartUseCase
import org.jeonfeel.moeuibit2.data.usecase.UpbitCoinDetailUseCase
import org.jeonfeel.moeuibit2.ui.coindetail.chart.utils.XAxisValueFormatter
import org.jeonfeel.moeuibit2.ui.coindetail.chart.utils.upbit.Chart
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
    ): Chart {
        return Chart(
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