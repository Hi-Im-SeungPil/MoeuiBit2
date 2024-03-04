package org.jeonfeel.moeuibit2.ui.base

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import org.jeonfeel.moeuibit2.constants.INTERNET_CONNECTION

abstract class BaseViewModel : ViewModel() {
    protected val gson = Gson()
    protected val _networkErrorState = mutableStateOf(INTERNET_CONNECTION)
    val networkErrorState: State<Int> get() = _networkErrorState

    fun changeNetworkErrorState(networkState: Int) {
        _networkErrorState.value = networkState
    }
}