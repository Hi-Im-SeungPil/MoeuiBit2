package org.jeonfeel.moeuibit2.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.jeonfeel.moeuibit2.constant.ROOM_DATABASE_NAME
import org.jeonfeel.moeuibit2.data.local.room.MoeuiBitDatabase
import org.jeonfeel.moeuibit2.data.local.room.MoeuiBitDatabase.Companion.MIGRATION_2_3
import org.jeonfeel.moeuibit2.repository.local.LocalRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LocalModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): MoeuiBitDatabase {
        return Room.databaseBuilder(context,
            MoeuiBitDatabase::class.java,
            ROOM_DATABASE_NAME)
            .addMigrations(MoeuiBitDatabase.MIGRATION_1_2,MIGRATION_2_3)
            .build()
    }

    @Singleton
    @Provides
    fun provideLocalRepository(moeuiBitDatabase: MoeuiBitDatabase): LocalRepository {
        return LocalRepository(moeuiBitDatabase)
    }
}