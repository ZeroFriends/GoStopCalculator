package zero.friends.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import zero.friends.data.repository.PlayerRepositoryImpl
import zero.friends.data.source.PlayerDao
import zero.friends.domain.repository.PlayerRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {
    @Provides
    @Singleton
    fun provideRepository(playerDao: PlayerDao): PlayerRepository = PlayerRepositoryImpl(playerDao)
}