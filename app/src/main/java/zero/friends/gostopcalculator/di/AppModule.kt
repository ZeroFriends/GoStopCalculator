package zero.friends.gostopcalculator.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import zero.friends.domain.repository.PlayerRepository
import zero.friends.domain.usecase.AddPlayerUseCase

@Module
@InstallIn(ActivityRetainedComponent::class)
class AppModule {
    @Provides
    fun provideUseCase(playerRepository: PlayerRepository): AddPlayerUseCase = AddPlayerUseCase(playerRepository)
}