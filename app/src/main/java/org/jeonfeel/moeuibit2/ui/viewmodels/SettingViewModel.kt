package org.jeonfeel.moeuibit2.ui.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.constants.ioDispatcher
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.ui.base.BaseViewModel
import javax.inject.Inject

data class SettingScreenState(
    val openSourceState: MutableState<Boolean> = mutableStateOf(false)
)

class SettingViewModel @Inject constructor(
    private val localRepository: LocalRepository
) : BaseViewModel() {
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
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SettingViewModel(localRepository) as T
            }
        }
    }
}