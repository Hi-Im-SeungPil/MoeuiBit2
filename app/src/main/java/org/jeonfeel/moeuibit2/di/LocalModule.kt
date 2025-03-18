package org.jeonfeel.moeuibit2.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.jeonfeel.moeuibit2.constants.ROOM_DATABASE_NAME
import org.jeonfeel.moeuibit2.data.local.room.MoeuiBitDatabase
import org.jeonfeel.moeuibit2.data.local.room.MoeuiBitDatabase.Companion.MIGRATION_2_3
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.utils.manager.CacheManager
import org.jeonfeel.moeuibit2.data.local.preferences.PreferencesManager
import org.jeonfeel.moeuibit2.data.local.room.MoeuiBitDatabase.Companion.MIGRATION_3_4
import org.jeonfeel.moeuibit2.data.local.room.MoeuiBitDatabase.Companion.MIGRATION_4_5
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LocalModule {

    /**
     * 데이터 베이스 (ROOM)
     */
    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): MoeuiBitDatabase {
        return Room.databaseBuilder(
            context,
            MoeuiBitDatabase::class.java,
            ROOM_DATABASE_NAME
        )
            .addMigrations(
                MoeuiBitDatabase.MIGRATION_1_2,
                MIGRATION_2_3,
                MIGRATION_3_4,
                MIGRATION_4_5
            )
            .build()
    }

    @Singleton
    @Provides
    fun provideLocalRepository(moeuiBitDatabase: MoeuiBitDatabase): LocalRepository {
        return LocalRepository(moeuiBitDatabase)
    }

    /**
     * dataStore preferences
     */
    @Singleton
    @Provides
    fun providePreferencesManager(@ApplicationContext context: Context): PreferencesManager {
        return PreferencesManager(context)
    }

    /**
     * dataStore proto
     */
    @Singleton
    @Provides
    fun provideCacheManager(@ApplicationContext context: Context): CacheManager {
        return CacheManager(context)
    }
}