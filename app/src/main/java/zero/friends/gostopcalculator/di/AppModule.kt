package zero.friends.gostopcalculator.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.PlayerRepository
import zero.friends.domain.repository.RuleRepository
import zero.friends.domain.usecase.player.DeletePlayerUseCase
import zero.friends.domain.usecase.player.EditPlayerUseCase
import zero.friends.domain.usecase.rule.GetDefaultRuleUseCase

@Module
@InstallIn(ActivityRetainedComponent::class)
class AppModule {

    @Provides
    fun provideDeletePlayerUseCase(
        gameRepository: GameRepository,
        playerRepository: PlayerRepository,
    ): DeletePlayerUseCase =
        DeletePlayerUseCase(gameRepository, playerRepository)

    @Provides
    fun provideEditPlayerUserCase(
        gameRepository: GameRepository,
        playerRepository: PlayerRepository,
    ): EditPlayerUseCase =
        EditPlayerUseCase(gameRepository, playerRepository)

    @Provides
    fun provideGetDefaultRuleUseCase(
        ruleRepository: RuleRepository,
    ): GetDefaultRuleUseCase = GetDefaultRuleUseCase(ruleRepository)
}