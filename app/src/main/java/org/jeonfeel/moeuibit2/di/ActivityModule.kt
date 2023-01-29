package org.jeonfeel.moeuibit2.di

import android.app.Activity
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import org.jeonfeel.moeuibit2.utils.manager.AdMobManager
import org.jeonfeel.moeuibit2.utils.NetworkMonitorUtil

@Module
@InstallIn(ActivityComponent::class)
class ActivityModule {

    @Provides
    fun provideNetworkMonitorUtil(@ActivityContext context: Context): NetworkMonitorUtil {
        return NetworkMonitorUtil(context)
    }
}