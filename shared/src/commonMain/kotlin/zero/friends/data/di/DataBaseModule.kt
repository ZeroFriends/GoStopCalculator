package zero.friends.data.di

import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import zero.friends.data.repository.GameRepositoryImpl
import zero.friends.data.repository.GamerRepositoryImpl
import zero.friends.data.repository.PlayerRepositoryImpl
import zero.friends.data.repository.RoundRepositoryImpl
import zero.friends.data.repository.RuleRepositoryImpl
import zero.friends.data.source.DataBase
import zero.friends.data.source.dao.GameDao
import zero.friends.data.source.dao.GamerDao
import zero.friends.data.source.dao.PlayerDao
import zero.friends.data.source.dao.RoundDao
import zero.friends.data.source.dao.RuleDao
import zero.friends.data.source.getDatabaseBuilder
import zero.friends.data.source.getRoomDatabase
import zero.friends.data.source.preference.Preference
import zero.friends.domain.preference.AppPreference
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.GamerRepository
import zero.friends.domain.repository.PlayerRepository
import zero.friends.domain.repository.RoundRepository
import zero.friends.domain.repository.RuleRepository
import zero.friends.domain.usecase.calculate.audit.AuditDeduplicator
import zero.friends.domain.usecase.calculate.audit.CalculationAuditReporter
import zero.friends.domain.usecase.calculate.audit.CalculationAuditReporterRegistry
import zero.friends.domain.usecase.calculate.audit.CalculationAuditor
import zero.friends.domain.usecase.calculate.audit.CalculationInvariantValidator
import zero.friends.domain.usecase.calculate.audit.DefaultCalculationAuditor

@Component
abstract class DataComponent {
    val dataBase: DataBase by lazy {
        getRoomDatabase(getDatabaseBuilder())
    }

    val appPreference: AppPreference by lazy {
        Preference()
    }

    private val calculationAuditDeduplicator: AuditDeduplicator by lazy {
        AuditDeduplicator()
    }

    @Provides
    fun provideGameDao(): GameDao = dataBase.GameDao()

    @Provides
    fun providePlayerDao(): PlayerDao = dataBase.PlayerDao()

    @Provides
    fun provideRuleDao(): RuleDao = dataBase.RuleDao()

    @Provides
    fun provideGamerDao(): GamerDao = dataBase.GamerDao()

    @Provides
    fun provideRoundDao(): RoundDao = dataBase.roundDao()

    @Provides
    fun provideAppPreference(): AppPreference = appPreference

    @Provides
    fun providePlayerRepository(dao: PlayerDao): PlayerRepository = PlayerRepositoryImpl(dao)

    @Provides
    fun provideGameRepository(dao: GameDao): GameRepository = GameRepositoryImpl(dao)

    @Provides
    fun provideRuleRepository(dao: RuleDao): RuleRepository = RuleRepositoryImpl(dao)

    @Provides
    fun provideGamerRepository(dao: GamerDao): GamerRepository = GamerRepositoryImpl(dao)

    @Provides
    fun provideRoundRepository(dao: RoundDao): RoundRepository = RoundRepositoryImpl(dao)

    @Provides
    fun provideCleanEmptyRoundsUseCase(
        gameRepository: GameRepository,
        roundRepository: RoundRepository,
        gamerRepository: GamerRepository
    ): zero.friends.domain.usecase.CleanEmptyRoundsUseCase =
        zero.friends.domain.usecase.CleanEmptyRoundsUseCase(gameRepository, roundRepository, gamerRepository)

    @Provides
    fun provideCalculationAuditReporter(): CalculationAuditReporter =
        CalculationAuditReporterRegistry.delegatingReporter()

    @Provides
    fun provideCalculationInvariantValidator(): CalculationInvariantValidator =
        CalculationInvariantValidator()

    @Provides
    fun provideCalculationAuditor(
        validator: CalculationInvariantValidator,
        reporter: CalculationAuditReporter,
    ): CalculationAuditor = DefaultCalculationAuditor(
        validator = validator,
        reporter = reporter,
        deduplicator = calculationAuditDeduplicator,
    )

}

