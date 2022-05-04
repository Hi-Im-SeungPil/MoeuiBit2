package org.jeonfeel.moeuibit2.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.jeonfeel.moeuibit2.data.local.room.AppDatabase
import org.jeonfeel.moeuibit2.repository.local.LocalRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LocalModule {
    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context,
            AppDatabase::class.java,
            "AppDatabase")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }

    @Singleton
    @Provides
    fun provideLocalRepository(appDatabase: AppDatabase): LocalRepository {
        return LocalRepository(appDatabase)
    }
}