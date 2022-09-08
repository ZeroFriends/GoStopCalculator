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
    fun bindPlayerRepository(playerRepository: PlayerRepositoryImpl): PlayerRepository

    @Binds
    @Singleton
    fun bindGameRepository(gameRepository: GameRepositoryImpl): GameRepository

    @Binds
    @Singleton
    fun bindRuleRepository(ruleRepository: RuleRepositoryImpl): RuleRepository

    @Binds
    @Singleton
    fun bindGamerRepository(gamerRepository: GamerRepositoryImpl): GamerRepository

    @Binds
    @Singleton
    fun bindRoundRepository(roundRepository: RoundRepositoryImpl): RoundRepository

    @Binds
    @Singleton
    fun bindsManualRepository(manualRepository: ManualRepositoryImpl): ManualRepository
}