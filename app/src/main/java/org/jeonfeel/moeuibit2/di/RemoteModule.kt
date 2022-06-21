package org.jeonfeel.moeuibit2.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.jeonfeel.moeuibit2.data.remote.retrofit.api.UpBitService
import org.jeonfeel.moeuibit2.repository.remote.RemoteRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RemoteModule {

    @Singleton
    @Provides
    fun provideUpBitRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.upbit.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideUpBitRetrofitMarketCodeService(retrofit: Retrofit): UpBitService {
        return retrofit.create(UpBitService::class.java)
    }

    @Singleton
    @Provides
    fun provideRemoteRepository(upBitService: UpBitService): RemoteRepository {
        return RemoteRepository(upBitService)
    }
}