package org.jeonfeel.moeuibit2.ui.coindetail.coininfo

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.orhanobut.logger.Logger
import org.jeonfeel.moeuibit2.data.repository.network.CoinCapIORepository
import org.jeonfeel.moeuibit2.data.repository.network.USDRepository
import org.jeonfeel.moeuibit2.ui.coindetail.coininfo.model.CoinInfoDataRes
import org.jeonfeel.moeuibit2.ui.coindetail.coininfo.model.CoinInfoModel

class CoinInfoUseCase(
    private val usdRepository: USDRepository,
    private val coinCapIORepository: CoinCapIORepository,
) {

    fun fetchCoinInfoData(symbol: String, callback: (CoinInfoModel) -> Unit, onCancel: () -> Unit) {
        val database =
            FirebaseDatabase.getInstance().getReference("coinInfoData/${symbol.uppercase()}")

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.getValue(CoinInfoDataRes::class.java)?.let {
                    if (snapshot.exists()) {
                        callback(it.mapToCoinInfoModel())
                    } else {
                        onCancel()
                    }
                } ?: run { onCancel() }
            }

            override fun onCancelled(error: DatabaseError) {
                Logger.e("Firebase", "Error: ${error.message}")
                onCancel()
            }
        })
    }
}