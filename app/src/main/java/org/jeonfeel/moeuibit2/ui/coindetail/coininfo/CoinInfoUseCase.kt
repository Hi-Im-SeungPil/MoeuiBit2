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
import org.jeonfeel.moeuibit2.ui.coindetail.coininfo.model.CoinLinkModel
import org.jeonfeel.moeuibit2.ui.coindetail.coininfo.model.LinkType
import org.jeonfeel.moeuibit2.ui.common.ResultState
import java.math.BigDecimal

class CoinInfoUseCase(
    private val usdRepository: USDRepository,
    private val coinCapIORepository: CoinCapIORepository,
) {
    suspend fun fetchCoinInfo(engName: String): Flow<ResultState<FetchCoinInfoRes?>> {
        return coinCapIORepository.fetchCoinInfo(engName = engName).map { res ->
            when (res.status) {
                ApiResult.Status.SUCCESS -> {
                    ResultState.Success(res.data)
                }

                ApiResult.Status.API_ERROR, ApiResult.Status.NETWORK_ERROR -> {
                    ResultState.Error(res.message ?: "Unknown error")
                }

                ApiResult.Status.LOADING -> {
                    ResultState.Loading
                }
            }
        }
    }

    suspend fun fetchUSDPrice(): Flow<ResultState<BigDecimal>> {
        return usdRepository.fetchUSDToKRWPrice().map { res ->
            when (res.status) {
                ApiResult.Status.SUCCESS -> {
                    val usd = res.data?.usd?.krw?.toBigDecimal() ?: BigDecimal.ZERO
                    ResultState.Success(usd)
                }

                ApiResult.Status.API_ERROR, ApiResult.Status.NETWORK_ERROR -> {
                    ResultState.Success(BigDecimal.ZERO)
                }

                ApiResult.Status.LOADING -> {
                    ResultState.Loading
                }
            }
        }
    }

    fun fetchCoinInfoFromFirebase(symbol: String, callback: (list: List<CoinLinkModel>) -> Unit) {
        val mDatabase = FirebaseDatabase.getInstance().reference
        mDatabase.child("secondCoinInfo").child(symbol)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val titleList = listOf(
                        "블럭조회" to LinkType.BROWSER,
                        "홈페이지" to LinkType.BROWSER,
                        "정보" to LinkType.BROWSER,
                        "트위터" to LinkType.IN_APP,
                        "코인마켓캡" to LinkType.IN_APP
                    )
                    val homepage = getSnapshotItem(snapshot, KeyConst.INFO_HOMEPAGE_KEY)
                    val amount = getSnapshotItem(snapshot, KeyConst.INFO_AMOUNT_KEY)
                    val twitter = getSnapshotItem(snapshot, KeyConst.INFO_TWITTER_KEY)
                    val block = getSnapshotItem(snapshot, KeyConst.INFO_BLOCK_KEY)
                    val info = getSnapshotItem(snapshot, KeyConst.INFO_INFO_KEY)

                    val linkList = if (
                        homepage.isEmpty()
                        || amount.isEmpty()
                        || twitter.isEmpty()
                        || block.isEmpty()
                        || info.isEmpty()
                    ) {
                        emptyList()
                    } else {
                        listOf(block, homepage, info, twitter, amount)
                    }

                    val coinLinkModelList = if (linkList.isNotEmpty()) {
                        titleList.mapIndexed { index, item ->
                            val (title, linkType) = item

                            CoinLinkModel(
                                title = title,
                                url = linkList[index],
                                linkType = linkType
                            )
                        }
                    } else {
                        emptyList()
                    }

                    callback.invoke(coinLinkModelList)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback.invoke(emptyList())
                }
            })
    }

    private fun getSnapshotItem(snapshot: DataSnapshot, key: String): String {
        return snapshot.child(key).getValue(String::class.java) ?: ""
    }
}