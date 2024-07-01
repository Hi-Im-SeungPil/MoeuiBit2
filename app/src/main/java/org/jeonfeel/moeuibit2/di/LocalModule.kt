package org.jeonfeel.moeuibit2.di

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.jeonfeel.moeuibit2.constants.ROOM_DATABASE_NAME
import org.jeonfeel.moeuibit2.data.local.room.MoeuiBitDatabase
import org.jeonfeel.moeuibit2.data.local.room.MoeuiBitDatabase.Companion.MIGRATION_2_3
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.utils.manager.PreferenceManager
import java.util.prefs.Preferences
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Module
@InstallIn(SingletonComponent::class)
class LocalModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): MoeuiBitDatabase {
        return Room.databaseBuilder(
            context,
            MoeuiBitDatabase::class.java,
            ROOM_DATABASE_NAME
        )
            .addMigrations(MoeuiBitDatabase.MIGRATION_1_2, MIGRATION_2_3)
            .build()
    }

    @Singleton
    @Provides
    fun provideLocalRepository(moeuiBitDatabase: MoeuiBitDatabase): LocalRepository {
        return LocalRepository(moeuiBitDatabase)
    }

    @Singleton
    @Provides
    fun providePrefrence(@ApplicationContext context: Context): DataStore<Preferences> {
        return da
    }

    @Singleton
    @Provides
    fun providePrefrenceManager(prefrence: SharedPreferences): PreferenceManager {
        return PreferenceManager(prefrence)
    }
}