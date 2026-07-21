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

    val playerDao: PlayerDao get() = dataBase.PlayerDao()
    val gameDao: GameDao get() = dataBase.GameDao()
    val ruleDao: RuleDao get() = dataBase.RuleDao()
    val gamerDao: GamerDao get() = dataBase.GamerDao()
    val roundDao: RoundDao get() = dataBase.roundDao()

    @Provides
    fun providePlayerRepository(impl: PlayerRepositoryImpl): PlayerRepository = impl

    @Provides
    fun provideGameRepository(impl: GameRepositoryImpl): GameRepository = impl

    @Provides
    fun provideRuleRepository(impl: RuleRepositoryImpl): RuleRepository = impl

    @Provides
    fun provideGamerRepository(impl: GamerRepositoryImpl): GamerRepository = impl

    @Provides
    fun provideRoundRepository(impl: RoundRepositoryImpl): RoundRepository = impl

    @Provides
    fun provideManualRepository(impl: ManualRepositoryImpl): ManualRepository = impl
}


