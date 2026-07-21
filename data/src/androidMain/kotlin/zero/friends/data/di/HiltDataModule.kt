package zero.friends.data.di

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
import zero.friends.data.source.initAssetContext
import zero.friends.data.source.initDatabaseContext
import zero.friends.data.source.preference.Preference
import zero.friends.data.source.preference.initPreferenceContext
import zero.friends.domain.preference.AppPreference
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.GamerRepository
import zero.friends.domain.repository.ManualRepository
import zero.friends.domain.repository.PlayerRepository
import zero.friends.domain.repository.RoundRepository
import zero.friends.domain.repository.RuleRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HiltDataBaseModule {
    @Provides
    @Singleton
    fun provideDataBase(@ApplicationContext context: Context): DataBase {
        initDatabaseContext(context)
        initAssetContext(context)
        return Room.databaseBuilder(context, DataBase::class.java, "zero.db")
            .build()
    }

    @Provides
    @Singleton
    fun providePlayerDao(dataBase: DataBase): PlayerDao = dataBase.PlayerDao()

    @Provides
    @Singleton
    fun provideGameDao(dataBase: DataBase): GameDao = dataBase.GameDao()

    @Provides
    @Singleton
    fun provideRuleDao(dataBase: DataBase): RuleDao = dataBase.RuleDao()

    @Provides
    @Singleton
    fun provideGamerDao(dataBase: DataBase): GamerDao = dataBase.GamerDao()

    @Provides
    @Singleton
    fun provideRoundDao(dataBase: DataBase): RoundDao = dataBase.roundDao()
}

@Module
@InstallIn(SingletonComponent::class)
object HiltRepositoryModule {
    @Provides
    @Singleton
    fun providePlayerRepository(impl: PlayerRepositoryImpl): PlayerRepository = impl

    @Provides
    @Singleton
    fun provideGameRepository(impl: GameRepositoryImpl): GameRepository = impl

    @Provides
    @Singleton
    fun provideRuleRepository(impl: RuleRepositoryImpl): RuleRepository = impl

    @Provides
    @Singleton
    fun provideGamerRepository(impl: GamerRepositoryImpl): GamerRepository = impl

    @Provides
    @Singleton
    fun provideRoundRepository(impl: RoundRepositoryImpl): RoundRepository = impl

    @Provides
    @Singleton
    fun provideManualRepository(impl: ManualRepositoryImpl): ManualRepository = impl
}

@Module
@InstallIn(SingletonComponent::class)
object HiltPreferenceModule {
    @Provides
    @Singleton
    fun providePreference(@ApplicationContext context: Context): AppPreference {
        initPreferenceContext(context)
        return Preference()
    }
}
