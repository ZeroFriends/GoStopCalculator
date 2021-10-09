package zero.friends.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import zero.friends.data.repository.GameRepositoryImpl
import zero.friends.data.repository.PlayerRepositoryImpl
import zero.friends.data.source.dao.GameDao
import zero.friends.data.source.dao.PlayerDao
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.PlayerRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {
    @Provides
    @Singleton
    fun providePlayerRepository(playerDao: PlayerDao): PlayerRepository = PlayerRepositoryImpl(playerDao)

    @Provides
    @Singleton
    fun provideGameRepository(gameDao: GameDao): GameRepository = GameRepositoryImpl(gameDao)
}