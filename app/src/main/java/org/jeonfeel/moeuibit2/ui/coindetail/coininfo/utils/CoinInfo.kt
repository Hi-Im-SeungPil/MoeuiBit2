package org.jeonfeel.moeuibit2.ui.coindetail.coininfo.utils

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.jeonfeel.moeuibit2.constants.*

class CoinInfo {
    private val _coinInfoMutableLiveData = MutableLiveData<HashMap<String, String>>()
    val coinInfoLiveData: LiveData<HashMap<String, String>> get() = _coinInfoMutableLiveData

    val _coinInfoLoading = mutableStateOf(false)
    val coinInfoLoading: State<Boolean> get() = _coinInfoLoading

    fun getCoinInfo(market: String) {
        _coinInfoLoading.value = true
        val mDatabase = FirebaseDatabase.getInstance().reference
        mDatabase.child("secondCoinInfo").child(market.substring(4))
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val coinInfoHashMap = HashMap<String, String>()
                    val homepage =
                        snapshot.child(KeyConst.INFO_HOMEPAGE_KEY).getValue(String::class.java)
                            ?: ""
                    val amount =
                        snapshot.child(KeyConst.INFO_AMOUNT_KEY).getValue(String::class.java) ?: ""
                    val twitter =
                        snapshot.child(KeyConst.INFO_TWITTER_KEY).getValue(String::class.java) ?: ""
                    val block =
                        snapshot.child(KeyConst.INFO_BLOCK_KEY).getValue(String::class.java) ?: ""
                    val info =
                        snapshot.child(KeyConst.INFO_INFO_KEY).getValue(String::class.java) ?: ""

                    if (homepage.isEmpty()) {
                        _coinInfoMutableLiveData.postValue(coinInfoHashMap)
                    } else {
                        coinInfoHashMap[KeyConst.INFO_HOMEPAGE_KEY] = homepage
                        coinInfoHashMap[KeyConst.INFO_AMOUNT_KEY] = amount
                        coinInfoHashMap[KeyConst.INFO_TWITTER_KEY] = twitter
                        coinInfoHashMap[KeyConst.INFO_BLOCK_KEY] = block
                        coinInfoHashMap[KeyConst.INFO_INFO_KEY] = info
                        _coinInfoMutableLiveData.postValue(coinInfoHashMap)
                    }
                    _coinInfoLoading.value = false
                }

                override fun onCancelled(error: DatabaseError) {
                    _coinInfoLoading.value = false
                }
            })
    }
}