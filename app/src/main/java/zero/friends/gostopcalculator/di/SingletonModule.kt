package zero.friends.gostopcalculator.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import zero.friends.gostopcalculator.NetWorkChecker
import zero.friends.shared.MainDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SingletonModule {
    @Provides
    @Singleton
    fun provideNetworkChecker(
        @ApplicationContext context: Context,
        @MainDispatcher dispatcher: CoroutineDispatcher
    ): NetWorkChecker = NetWorkChecker(context, dispatcher)
}