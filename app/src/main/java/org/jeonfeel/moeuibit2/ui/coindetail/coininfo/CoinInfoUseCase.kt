package org.jeonfeel.moeuibit2.ui.coindetail.coininfo

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.jeonfeel.moeuibit2.constants.KeyConst
import org.jeonfeel.moeuibit2.data.local.preferences.PreferencesManager
import org.jeonfeel.moeuibit2.data.repository.network.CoinCapIORepository
import org.jeonfeel.moeuibit2.data.repository.network.USDRepository

class CoinInfoUseCase(
    private val preferencesManager: PreferencesManager
    private val usdRepository: USDRepository,
    private val coinCapIORepository: CoinCapIORepository,
) {
    suspend fun init(symbol: String) {
        usdRepository.fetchUSDToKRWPrice()
//        coinCapIORepository.fetchCoinInfo(symbol)
    }

    private fun fetchCoinInfoFromFirebase(symbol: String, callback: (Map<String, String>) -> Unit) {
        val mDatabase = FirebaseDatabase.getInstance().reference
        mDatabase.child("secondCoinInfo").child(symbol)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val homepage = getSnapshotItem(snapshot, KeyConst.INFO_HOMEPAGE_KEY)
                    val amount = getSnapshotItem(snapshot, KeyConst.INFO_AMOUNT_KEY)
                    val twitter = getSnapshotItem(snapshot, KeyConst.INFO_TWITTER_KEY)
                    val block = getSnapshotItem(snapshot, KeyConst.INFO_BLOCK_KEY)
                    val info = getSnapshotItem(snapshot, KeyConst.INFO_INFO_KEY)

                    val linkMap = if (
                        homepage.isEmpty()
                        || amount.isEmpty()
                        || twitter.isEmpty()
                        || block.isEmpty()
                        || info.isEmpty()
                    ) {
                        emptyMap()
                    } else {
                        mapOf(
                            KeyConst.INFO_HOMEPAGE_KEY to homepage,
                            KeyConst.INFO_AMOUNT_KEY to amount,
                            KeyConst.INFO_TWITTER_KEY to twitter,
                            KeyConst.INFO_BLOCK_KEY to block,
                            KeyConst.INFO_INFO_KEY to info
                        )
                    }

                    callback.invoke(linkMap)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback.invoke(emptyMap())
                }
            })
    }

    fun getSnapshotItem(snapshot: DataSnapshot, key: String): String {
        return snapshot.child(key).getValue(String::class.java) ?: ""
    }
}