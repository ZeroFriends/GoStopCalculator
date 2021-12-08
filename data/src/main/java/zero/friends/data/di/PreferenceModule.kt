package zero.friends.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import zero.friends.data.source.preference.Preference
import zero.friends.domain.preference.AppPreference
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface PreferenceModule {
    @Binds
    @Singleton
    fun providesPreference(preference: Preference): AppPreference
}