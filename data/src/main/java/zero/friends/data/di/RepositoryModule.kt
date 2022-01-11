package zero.friends.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import zero.friends.data.repository.*
import zero.friends.domain.repository.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    @Singleton
    fun providePlayerRepository(playerRepository: PlayerRepositoryImpl): PlayerRepository

    @Binds
    @Singleton
    fun provideGameRepository(gameRepository: GameRepositoryImpl): GameRepository

    @Binds
    @Singleton
    fun provideRuleRepository(ruleRepository: RuleRepositoryImpl): RuleRepository

    @Binds
    @Singleton
    fun provideGamerRepository(gamerRepository: GamerRepositoryImpl): GamerRepository

    @Binds
    @Singleton
    fun provideRoundRepository(roundRepository: RoundRepositoryImpl): RoundRepository
}