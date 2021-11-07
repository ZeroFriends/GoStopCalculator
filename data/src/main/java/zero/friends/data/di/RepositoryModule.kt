package zero.friends.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import zero.friends.data.repository.GameRepositoryImpl
import zero.friends.data.repository.PlayerRepositoryImpl
import zero.friends.data.repository.RuleRepositoryImpl
import zero.friends.data.source.api.RuleApi
import zero.friends.data.source.dao.GameDao
import zero.friends.data.source.dao.PlayerDao
import zero.friends.data.source.dao.RuleDao
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.PlayerRepository
import zero.friends.domain.repository.RuleRepository
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
}