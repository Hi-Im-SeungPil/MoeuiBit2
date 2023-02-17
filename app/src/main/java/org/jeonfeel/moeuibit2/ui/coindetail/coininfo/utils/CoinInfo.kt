package org.jeonfeel.moeuibit2.ui.coindetail.coininfo.utils

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.jeonfeel.moeuibit2.constants.*
import org.jeonfeel.moeuibit2.data.repository.remote.RemoteRepository

class CoinInfoState {
    val coinInfoDialog = mutableStateOf(false)
    val coinInfoLoading = mutableStateOf(false)
    val webViewLoading = mutableStateOf(false)
}

class CoinInfo {
    private val _coinInfoMutableLiveData = MutableLiveData<HashMap<String, String>>()
    val coinInfoLiveData: LiveData<HashMap<String, String>> get() = _coinInfoMutableLiveData
    val state = CoinInfoState()

    fun getCoinInfo(market: String) {
        state.coinInfoDialog.value = true
        val mDatabase = FirebaseDatabase.getInstance().reference
        mDatabase.child("secondCoinInfo").child(market.substring(4))
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val coinInfoHashMap = HashMap<String, String>()
                    val homepage =
                        snapshot.child(INFO_HOMEPAGE_KEY).getValue(String::class.java) ?: ""
                    val amount = snapshot.child(INFO_AMOUNT_KEY).getValue(String::class.java) ?: ""
                    val twitter =
                        snapshot.child(INFO_TWITTER_KEY).getValue(String::class.java) ?: ""
                    val block = snapshot.child(INFO_BLOCK_KEY).getValue(String::class.java) ?: ""
                    val info = snapshot.child(INFO_INFO_KEY).getValue(String::class.java) ?: ""

                    if (homepage.isEmpty()) {
                        _coinInfoMutableLiveData.postValue(coinInfoHashMap)
                    } else {
                        coinInfoHashMap[INFO_HOMEPAGE_KEY] = homepage
                        coinInfoHashMap[INFO_AMOUNT_KEY] = amount
                        coinInfoHashMap[INFO_TWITTER_KEY] = twitter
                        coinInfoHashMap[INFO_BLOCK_KEY] = block
                        coinInfoHashMap[INFO_INFO_KEY] = info
                        _coinInfoMutableLiveData.postValue(coinInfoHashMap)
                    }
                    state.coinInfoDialog.value = false
                    state.coinInfoLoading.value = true
                }

                override fun onCancelled(error: DatabaseError) {
                    state.coinInfoDialog.value = false
                }
            })
    }
}