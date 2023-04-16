package org.jeonfeel.moeuibit2.ui.viewmodels

import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.MoeuiBitDataStore
import org.jeonfeel.moeuibit2.constants.*
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.CommonExchangeModel
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.TickerModel
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitPortfolioWebSocket
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitTickerWebSocket
import org.jeonfeel.moeuibit2.data.remote.websocket.listener.PortfolioOnTickerMessageReceiveListener
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.data.repository.remote.RemoteRepository
import org.jeonfeel.moeuibit2.ui.main.portfolio.dto.UserHoldCoinDTO
import org.jeonfeel.moeuibit2.utils.Utils
import org.jeonfeel.moeuibit2.utils.NetworkMonitorUtil.Companion.currentNetworkState
import org.jeonfeel.moeuibit2.utils.manager.AdMobManager
import org.jeonfeel.moeuibit2.utils.manager.PreferenceManager
import javax.inject.Inject

data class MainViewModelState(
    val errorState: MutableState<Int> = mutableStateOf(INTERNET_CONNECTION),
    val isLoadingSuccess: MutableState<Boolean> = mutableStateOf(false)
)

@HiltViewModel
class MainViewModel @Inject constructor(
    val remoteRepository: RemoteRepository,
    val localRepository: LocalRepository,
    val adMobManager: AdMobManager,
    val preferenceManager: PreferenceManager
) : ViewModel() {
    val state = MainViewModelState()

    fun updateFavorite(market: String, isFavorite: Boolean) {
        viewModelScope.launch(ioDispatcher) {
            when {
                MoeuiBitDataStore.favoriteHashMap[market] == null && isFavorite -> {
                    MoeuiBitDataStore.favoriteHashMap[market] = 0
                    try {
                        localRepository.getFavoriteDao().insert(market)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                MoeuiBitDataStore.favoriteHashMap[market] != null && !isFavorite -> {
                    MoeuiBitDataStore.favoriteHashMap.remove(market)
                    try {
                        localRepository.getFavoriteDao().delete(market)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}