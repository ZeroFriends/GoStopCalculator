package zero.friends.data.di

import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import zero.friends.data.repository.GameRepositoryImpl
import zero.friends.data.repository.GamerRepositoryImpl
import zero.friends.data.repository.ManualRepositoryImpl
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
import zero.friends.domain.repository.ManualRepository
import zero.friends.domain.repository.PlayerRepository
import zero.friends.domain.repository.RoundRepository
import zero.friends.domain.repository.RuleRepository

@Component
abstract class DataComponent {
    val dataBase: DataBase by lazy {
        getRoomDatabase(getDatabaseBuilder())
    }

    val appPreference: AppPreference by lazy {
        Preference()
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
    fun provideManualRepository(): ManualRepository = ManualRepositoryImpl()
}
