package org.jeonfeel.moeuibit2.ui.coindetail.coininfo

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import org.jeonfeel.moeuibit2.constants.KeyConst
import org.jeonfeel.moeuibit2.ui.coindetail.coininfo.utils.CoinInfo
import java.math.BigDecimal

enum class CoinInfoScreenState {
    LOADING, SUCCESS, ERROR
}

data class CoinInfoScreenUIState(
    val coinInfo: CoinInfo?,
    val usdPrice: BigDecimal,
    val coinLinkMap: Map<String, String>,
    val state: CoinInfoScreenState,
)

@HiltViewModel
class CoinInfoViewModel(coinInfoUseCase: CoinInfoUseCase) : ViewModel() {
    private val _coinInfoLoading = mutableStateOf(false)
    val coinInfoLoading: State<Boolean> get() = _coinInfoLoading

    private val _coinLinkState = mutableStateOf<Map<String, String>?>(emptyMap())
    val coinLinkState: State<Map<String, String>?> get() = _coinLinkState


}