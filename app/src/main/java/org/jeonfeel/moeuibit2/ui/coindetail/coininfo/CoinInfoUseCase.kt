package org.jeonfeel.moeuibit2.ui.coindetail.coininfo

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.orhanobut.logger.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.jeonfeel.moeuibit2.constants.KeyConst
import org.jeonfeel.moeuibit2.data.local.preferences.PreferencesManager
import org.jeonfeel.moeuibit2.data.network.retrofit.ApiResult
import org.jeonfeel.moeuibit2.data.network.retrofit.response.coincapio.FetchCoinInfoRes
import org.jeonfeel.moeuibit2.data.repository.network.CoinCapIORepository
import org.jeonfeel.moeuibit2.data.repository.network.USDRepository
import org.jeonfeel.moeuibit2.ui.coindetail.coininfo.model.CoinInfoDataRes
import org.jeonfeel.moeuibit2.ui.coindetail.coininfo.model.CoinLinkModel
import org.jeonfeel.moeuibit2.ui.coindetail.coininfo.model.LinkType
import org.jeonfeel.moeuibit2.ui.common.ResultState
import java.math.BigDecimal

class CoinInfoUseCase(
    private val usdRepository: USDRepository,
    private val coinCapIORepository: CoinCapIORepository,
) {
    private val database = FirebaseDatabase.getInstance().getReference("coinInfoData")

    private fun fetchCoinInfoData(callback: (CoinInfoDataRes) -> Unit) {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.getValue(CoinInfoDataRes::class.java)?.let {
                    _coreCoin.value = it
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Logger.e("Firebase", "Error: ${error.message}")
            }
        })
    }

    private fun getSnapshotItem(snapshot: DataSnapshot, key: String): String {
        return snapshot.child(key).getValue(String::class.java) ?: ""
    }
}