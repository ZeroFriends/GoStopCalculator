package zero.friends.gostopcalculator.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.PlayerRepository
import zero.friends.domain.usecase.AddPlayerUseCase
import zero.friends.domain.usecase.DeletePlayerUseCase

@Module
@InstallIn(ActivityRetainedComponent::class)
class AppModule {
    @Provides
    fun provideAddPlayerUseCase(gameRepository: GameRepository, playerRepository: PlayerRepository): AddPlayerUseCase =
        AddPlayerUseCase(gameRepository, playerRepository)

    @Provides
    fun provideDeletePlayerUseCase(gameRepository: GameRepository, playerRepository: PlayerRepository): DeletePlayerUseCase =
        DeletePlayerUseCase(gameRepository, playerRepository)
}