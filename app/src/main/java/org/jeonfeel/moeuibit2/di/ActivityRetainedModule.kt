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
import org.jeonfeel.moeuibit2.di.SocketModule.CoinDetailSocket
import org.jeonfeel.moeuibit2.di.SocketModule.CoinDetailSocketType
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

    @CoinDetailSocket
    @Provides
    fun provideCoinDetailSocketService(
        @CoinDetailSocketType okHttpClient: OkHttpClient,
        @ApplicationContext context: Context
    ): UpbitCoinDetailSocketService {
        return thunder {
            setWebSocketFactory(okHttpClient.makeWebSocketCore("wss://api.upbit.com/websocket/v1"))
            setApplicationContext(context)
            setConverterType(ConverterType.Serialization)
        }.create()
    }

    @Provides
    fun providerCoinDetailUseCase(
        localRepository: LocalRepository,
        upbitRepository: UpbitRepository,
        @CoinDetailSocketType okHttpClient: OkHttpClient,
        @ApplicationContext context: Context
//        @SocketModule.CoinDetailSocket upbitSocketService: UpbitCoinDetailSocketService
    ): UpbitCoinDetailUseCase {
        return UpbitCoinDetailUseCase(localRepository, upbitRepository, okHttpClient, context)
    }
}