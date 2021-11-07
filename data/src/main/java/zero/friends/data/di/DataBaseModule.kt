package zero.friends.data.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import zero.friends.data.source.DataBase
import zero.friends.data.source.dao.GameDao
import zero.friends.data.source.dao.PlayerDao
import zero.friends.data.source.dao.RuleDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataBaseModule {
    @Provides
    @Singleton
    fun provideDataBase(@ApplicationContext context: Context): DataBase =
        Room.databaseBuilder(context, DataBase::class.java, "GoStop").build()

    @Provides
    @Singleton
    fun providePlayerDao(dataBase: DataBase): PlayerDao = dataBase.PlayerDao()

    @Provides
    @Singleton
    fun provideGamerDao(dataBase: DataBase): GameDao = dataBase.GameDao()

    @Provides
    @Singleton
    fun provideRuleDao(dataBase: DataBase): RuleDao = dataBase.RuleDao()
}