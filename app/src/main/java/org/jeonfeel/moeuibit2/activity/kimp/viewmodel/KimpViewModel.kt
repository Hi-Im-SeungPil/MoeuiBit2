package org.jeonfeel.moeuibit2.activity.kimp.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.data.remote.retrofit.ApiResult
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.UsdtPriceModel
import org.jeonfeel.moeuibit2.repository.remote.RemoteRepository
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class KimpViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository
) : ViewModel() {
    val gson = Gson()
    val usdtPrice = mutableStateOf(0)
    val date = mutableStateOf("")

    fun requestUSDTPrice() {
        viewModelScope.launch {
            remoteRepository.getUSDTPrice().collect() {
                when (it.status) {
                    ApiResult.Status.LOADING -> {}
                    ApiResult.Status.SUCCESS -> {
                        val data = it.data
                        if(data != null) {
                            val model = gson.fromJson(data,UsdtPriceModel::class.java)
                            usdtPrice.value = model.krw.roundToInt()
                            date.value = model.date
                        }
                    }
                    ApiResult.Status.NETWORK_ERROR -> {}
                    ApiResult.Status.API_ERROR -> {}
                }
            }
        }
    }
}