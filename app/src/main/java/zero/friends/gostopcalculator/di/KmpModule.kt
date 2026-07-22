package zero.friends.gostopcalculator.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import zero.friends.data.repository.GameRepositoryImpl
import zero.friends.data.repository.GamerRepositoryImpl
import zero.friends.data.repository.ManualRepositoryImpl
import zero.friends.data.repository.PlayerRepositoryImpl
import zero.friends.data.repository.RoundRepositoryImpl
import zero.friends.data.repository.RuleRepositoryImpl
import zero.friends.domain.preference.AppPreference
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.GamerRepository
import zero.friends.domain.repository.ManualRepository
import zero.friends.domain.repository.PlayerRepository
import zero.friends.domain.repository.RoundRepository
import zero.friends.domain.repository.RuleRepository
import zero.friends.domain.usecase.ObservePlayerResultsUseCase
import zero.friends.domain.usecase.StartNewGameUseCase
import zero.friends.domain.usecase.calculate.AggregateGamerResultsUseCase
import zero.friends.domain.usecase.calculate.BuildRoundTraceUseCase
import zero.friends.domain.usecase.calculate.CalculateGameResultUseCase
import zero.friends.domain.usecase.calculate.CalculateLoserScoreUseCase
import zero.friends.domain.usecase.calculate.CalculateScoreOptionUseCase
import zero.friends.domain.usecase.calculate.CalculateSellScoreUseCase
import zero.friends.domain.usecase.calculate.UpdateAccountUseCase
import zero.friends.domain.usecase.option.SellingUseCase
import zero.friends.domain.usecase.option.ToggleLoserOptionUseCase
import zero.friends.domain.usecase.option.ToggleScoreOptionUseCase
import zero.friends.domain.usecase.option.UpdateOptionsUseCase
import zero.friends.domain.usecase.option.UpdateWinnerUseCase
import zero.friends.domain.usecase.round.CreateNewRoundUseCase
import zero.friends.domain.usecase.round.ObserveRoundListUseCase
import zero.friends.domain.usecase.rule.GetDefaultRuleUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object KmpModule {

    @Provides
    @Singleton
    fun provideAppComponent(): AppComponent = createAppComponent()

    @Provides
    fun provideGameRepository(appComponent: AppComponent): GameRepository =
        GameRepositoryImpl(appComponent.dataComponent.provideGameDao())

    @Provides
    fun providePlayerRepository(appComponent: AppComponent): PlayerRepository =
        PlayerRepositoryImpl(appComponent.dataComponent.providePlayerDao())

    @Provides
    fun provideRuleRepository(appComponent: AppComponent): RuleRepository =
        RuleRepositoryImpl(appComponent.dataComponent.provideRuleDao())

    @Provides
    fun provideGamerRepository(appComponent: AppComponent): GamerRepository =
        GamerRepositoryImpl(appComponent.dataComponent.provideGamerDao())

    @Provides
    fun provideRoundRepository(appComponent: AppComponent): RoundRepository =
        RoundRepositoryImpl(appComponent.dataComponent.provideRoundDao())

    @Provides
    fun provideManualRepository(appComponent: AppComponent): ManualRepository =
        ManualRepositoryImpl()

    @Provides
    fun provideAppPreference(appComponent: AppComponent): AppPreference =
        appComponent.dataComponent.provideAppPreference()

    @Provides
    fun provideUpdateAccountUseCase(gamerRepository: GamerRepository): UpdateAccountUseCase =
        UpdateAccountUseCase(gamerRepository)

    @Provides
    fun provideCalculateSellScoreUseCase(
        ruleRepository: RuleRepository,
        gamerRepository: GamerRepository
    ): CalculateSellScoreUseCase =
        CalculateSellScoreUseCase(ruleRepository, gamerRepository)

    @Provides
    fun provideCalculateLoserScoreUseCase(
        ruleRepository: RuleRepository,
        gamerRepository: GamerRepository
    ): CalculateLoserScoreUseCase =
        CalculateLoserScoreUseCase(ruleRepository, gamerRepository)

    @Provides
    fun provideCalculateScoreOptionUseCase(
        ruleRepository: RuleRepository,
        gamerRepository: GamerRepository
    ): CalculateScoreOptionUseCase =
        CalculateScoreOptionUseCase(ruleRepository, gamerRepository)

    @Provides
    fun provideCalculateGameResultUseCase(
        ruleRepository: RuleRepository,
        gamerRepository: GamerRepository,
        updateAccountUseCase: UpdateAccountUseCase,
        calculateSellScoreUseCase: CalculateSellScoreUseCase,
        calculateLoserScoreUseCase: CalculateLoserScoreUseCase,
        calculateScoreOptionUseCase: CalculateScoreOptionUseCase
    ): CalculateGameResultUseCase = CalculateGameResultUseCase(
        ruleRepository, gamerRepository, updateAccountUseCase,
        calculateSellScoreUseCase, calculateLoserScoreUseCase, calculateScoreOptionUseCase
    )

    @Provides
    fun provideSellingUseCase(
        gamerRepository: GamerRepository
    ): SellingUseCase = SellingUseCase(gamerRepository)

    @Provides
    fun provideStartNewGameUseCase(
        gameRepository: GameRepository,
        playerRepository: PlayerRepository,
        ruleRepository: RuleRepository
    ): StartNewGameUseCase = StartNewGameUseCase(gameRepository, playerRepository, ruleRepository)

    @Provides
    fun provideObservePlayerResultsUseCase(
        playerRepository: PlayerRepository,
        gamerRepository: GamerRepository
    ): ObservePlayerResultsUseCase = ObservePlayerResultsUseCase(playerRepository, gamerRepository)

    @Provides
    fun provideAggregateGamerResultsUseCase(): AggregateGamerResultsUseCase =
        AggregateGamerResultsUseCase()

    @Provides
    fun provideBuildRoundTraceUseCase(
        ruleRepository: RuleRepository,
        gamerRepository: GamerRepository,
        calculateLoserScoreUseCase: CalculateLoserScoreUseCase
    ): BuildRoundTraceUseCase =
        BuildRoundTraceUseCase(ruleRepository, gamerRepository, calculateLoserScoreUseCase)

    @Provides
    fun provideToggleLoserOptionUseCase(
        gamerRepository: GamerRepository,
        updateOptionsUseCase: UpdateOptionsUseCase
    ): ToggleLoserOptionUseCase =
        ToggleLoserOptionUseCase(gamerRepository, updateOptionsUseCase)

    @Provides
    fun provideToggleScoreOptionUseCase(
        gamerRepository: GamerRepository,
        updateOptionsUseCase: UpdateOptionsUseCase
    ): ToggleScoreOptionUseCase =
        ToggleScoreOptionUseCase(gamerRepository, updateOptionsUseCase)

    @Provides
    fun provideUpdateOptionsUseCase(gamerRepository: GamerRepository): UpdateOptionsUseCase =
        UpdateOptionsUseCase(gamerRepository)

    @Provides
    fun provideUpdateWinnerUseCase(gamerRepository: GamerRepository): UpdateWinnerUseCase =
        UpdateWinnerUseCase(gamerRepository)

    @Provides
    fun provideCreateNewRoundUseCase(
        gamerRepository: GamerRepository,
        roundRepository: RoundRepository
    ): CreateNewRoundUseCase = CreateNewRoundUseCase(gamerRepository, roundRepository)

    @Provides
    fun provideObserveRoundListUseCase(
        gamerRepository: GamerRepository
    ): ObserveRoundListUseCase = ObserveRoundListUseCase(gamerRepository)

    @Provides
    fun provideGetDefaultRuleUseCase(ruleRepository: RuleRepository): GetDefaultRuleUseCase =
        GetDefaultRuleUseCase(ruleRepository)
}
