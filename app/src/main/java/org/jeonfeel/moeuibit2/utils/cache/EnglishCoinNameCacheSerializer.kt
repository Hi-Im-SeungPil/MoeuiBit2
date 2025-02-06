package org.jeonfeel.moeuibit2.utils.cache

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.metadata.internal.protobuf.InvalidProtocolBufferException
import org.jeonfeel.moeuibit2.EnglishCoinNameCache
import java.io.InputStream
import java.io.OutputStream

object EnglishCoinNameCacheSerializer : Serializer<EnglishCoinNameCache> {
    override val defaultValue: EnglishCoinNameCache
        get() = EnglishCoinNameCache.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): EnglishCoinNameCache {
        try {
            return EnglishCoinNameCache.parseFrom(input)
        } catch (e: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", e)
        }
    }

    override suspend fun writeTo(t: EnglishCoinNameCache, output: OutputStream) {
        t.writeTo(output)
    }
}