package org.jeonfeel.moeuibit2.ui.main.setting

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.constants.ioDispatcher
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.ui.base.BaseViewModel
import org.jeonfeel.moeuibit2.utils.manager.PreferenceManager
import javax.inject.Inject

data class SettingScreenState(
    val openSourceState: MutableState<Boolean> = mutableStateOf(false)
)

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    val preferenceManager: PreferenceManager
) : BaseViewModel(preferenceManager) {
    val state = SettingScreenState()

    fun removeAll() {
        viewModelScope.launch(ioDispatcher) {
            localRepository.getUserDao().deleteAll()
            localRepository.getFavoriteDao().deleteAll()
            localRepository.getTransactionInfoDao().deleteAll()
            localRepository.getMyCoinDao().deleteAll()
        }
    }

    fun resetTransactionInfo() {
        viewModelScope.launch(ioDispatcher) {
            localRepository.getTransactionInfoDao().deleteAll()
        }
    }

    companion object {
        fun provideFactory(
            localRepository: LocalRepository,
            preferenceManager: PreferenceManager
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SettingViewModel(localRepository, preferenceManager) as T
            }
        }
    }
}