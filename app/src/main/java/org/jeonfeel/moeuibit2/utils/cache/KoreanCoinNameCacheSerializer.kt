package org.jeonfeel.moeuibit2.utils.cache

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.metadata.internal.protobuf.InvalidProtocolBufferException
import org.jeonfeel.moeuibit2.KoreanCoinNameCache
import java.io.InputStream
import java.io.OutputStream

object KoreanCoinNameCacheSerializer : Serializer<KoreanCoinNameCache> {
    override val defaultValue: KoreanCoinNameCache
        get() = KoreanCoinNameCache.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): KoreanCoinNameCache {
        try {
            return KoreanCoinNameCache.parseFrom(input)
        } catch (e: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", e)
        }
    }

    override suspend fun writeTo(t: KoreanCoinNameCache, output: OutputStream) {
        t.writeTo(output)
    }
}