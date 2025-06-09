package org.jeonfeel.moeuibit2.ui.coindetail.detail.root_exchange

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import org.jeonfeel.moeuibit2.data.usecase.UpbitCoinDetailUseCase
import org.jeonfeel.moeuibit2.utils.manager.CacheManager

class UpbitCoinDetail(
    private val upbitCoinDetailUseCase: UpbitCoinDetailUseCase,
    private val cacheManager: CacheManager,
) {
    private val _koreanCoinName = mutableStateOf("")
    val koreanCoinName: State<String> get() = _koreanCoinName

    private val _engCoinName = mutableStateOf("")
    val engCoinName: State<String> get() = _engCoinName

    private suspend fun getKoreanCoinName(market: String) {
        _koreanCoinName.value =
            cacheManager.readKoreanCoinNameMap()[market.substring(4)] ?: ""
    }

    private suspend fun getEngCoinName(market: String) {
        _engCoinName.value =
            cacheManager.readEnglishCoinNameMap()[market.substring(4)]?.replace(" ", "-") ?: ""
    }
}