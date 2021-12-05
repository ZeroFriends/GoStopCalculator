package zero.friends.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import zero.friends.data.repository.*
import zero.friends.data.source.api.RuleApi
import zero.friends.data.source.dao.*
import zero.friends.domain.repository.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {
    @Provides
    @Singleton
    fun providePlayerRepository(playerDao: PlayerDao): PlayerRepository =
        PlayerRepositoryImpl(playerDao)

    @Provides
    @Singleton
    fun provideGameRepository(gameDao: GameDao): GameRepository = GameRepositoryImpl(gameDao)

    @Provides
    @Singleton
    fun provideRuleRepository(
        @ApplicationContext context: Context,
        ruleApi: RuleApi,
        ruleDao: RuleDao,
    ): RuleRepository =
        RuleRepositoryImpl(context, ruleApi, ruleDao)

    @Provides
    @Singleton
    fun provideGamerRepository(gamerDao: GamerDao): GamerRepository = GamerRepositoryImpl(gamerDao)

    @Provides
    @Singleton
    fun provideRoundRepository(roundDao: RoundDao): RoundRepository = RoundRepositoryImpl(roundDao)
}