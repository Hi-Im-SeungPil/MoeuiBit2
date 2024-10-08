package org.jeonfeel.moeuibit2.ui.main

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.MoeuiBitDataStore
import org.jeonfeel.moeuibit2.constants.*
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.utils.manager.AdMobManager
import org.jeonfeel.moeuibit2.utils.manager.PreferencesManager
import javax.inject.Inject

data class MainViewModelState(
    val errorState: MutableState<Int> = mutableStateOf(INTERNET_CONNECTION),
    val isLoadingSuccess: MutableState<Boolean> = mutableStateOf(false)
)

@HiltViewModel
class MainViewModel @Inject constructor(
    val localRepository: LocalRepository,
    val adMobManager: AdMobManager,
    val preferenceManager: PreferencesManager
) : ViewModel() {
    val state = MainViewModelState()

    fun updateFavorite(market: String, isFavorite: Boolean) {
        viewModelScope.launch(ioDispatcher) {
            when {
                MoeuiBitDataStore.upBitFavoriteHashMap[market] == null && isFavorite -> {
                    MoeuiBitDataStore.upBitFavoriteHashMap[market] = 0
                    try {
                        localRepository.getFavoriteDao().insert(market)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                MoeuiBitDataStore.upBitFavoriteHashMap[market] != null && !isFavorite -> {
                    MoeuiBitDataStore.upBitFavoriteHashMap.remove(market)
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