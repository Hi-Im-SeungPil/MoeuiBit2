package org.jeonfeel.moeuibit2.utils.manager

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import kotlinx.coroutines.flow.first
import org.jeonfeel.moeuibit2.EnglishCoinNameCache
import org.jeonfeel.moeuibit2.KoreanCoinNameCache
import org.jeonfeel.moeuibit2.KoreanCoinNameComponent
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.UpbitMarketCodeRes
import org.jeonfeel.moeuibit2.utils.cache.EnglishCoinNameCacheSerializer
import org.jeonfeel.moeuibit2.utils.cache.KoreanCoinNameCacheSerializer

private val Context.englishCoinNameCacheDataStore: DataStore<EnglishCoinNameCache> by dataStore(
    fileName = "english_coin_name_cache.pb",
    serializer = EnglishCoinNameCacheSerializer
)
private val Context.koreanCoinNameCacheDataStore: DataStore<KoreanCoinNameCache> by dataStore(
    fileName = "korean_coin_name_cache.pb",
    serializer = KoreanCoinNameCacheSerializer
)

class CacheManager(private val context: Context) {

    suspend fun saveKoreanCoinNameMap(codeMap: MutableMap<String, UpbitMarketCodeRes>) {
        val koreanNameKeyValueList = arrayListOf<KoreanCoinNameComponent>()
        codeMap.forEach { (key, value) ->
            val symbol = key.substring(4)
            val koreanName = value.koreanName
            val component =
                KoreanCoinNameComponent.newBuilder().setKey(symbol).setValue(koreanName).build()
            koreanNameKeyValueList.add(component)
        }
        val koreanCoinNameCache =
            KoreanCoinNameCache.newBuilder().addAllKoreanCoinNameComponents(koreanNameKeyValueList)
                .build()
        context.koreanCoinNameCacheDataStore.updateData { current ->
            current.toBuilder().clearKoreanCoinNameComponents()
                .addAllKoreanCoinNameComponents(koreanCoinNameCache.koreanCoinNameComponentsList)
                .build()
        }
    }

    suspend fun readKoreanCoinNameMap(): Map<String, String> {
        val koreanCoinNameCache = context.koreanCoinNameCacheDataStore.data.first()
        return koreanCoinNameCache.koreanCoinNameComponentsList.associate { it.key to it.value }
    }

    suspend fun saveEnglishCoinNameMap(englishCoinNameCache: EnglishCoinNameCache) {
        context.englishCoinNameCacheDataStore.updateData { current ->
            current.toBuilder().clearEnglishCoinNameMap()
                .addAllEnglishCoinNameMap(englishCoinNameCache.englishCoinNameMapList)
                .build()
        }
    }

    suspend fun readEnglishCoinNameMap(): Map<String, String> {
        val englishCoinNameCache = context.englishCoinNameCacheDataStore.data.first()
        return englishCoinNameCache.englishCoinNameMapList.associate { it.key to it.value }
    }
}