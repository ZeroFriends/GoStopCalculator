package zero.friends.domain.repository

import kotlinx.coroutines.flow.Flow
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.Option
import zero.friends.domain.model.Player
import zero.friends.domain.model.ScoreOption
import kotlin.reflect.KClass

interface GamerRepository {
    suspend fun getGamer(gamerId: Long): Gamer
    suspend fun getAllGamer(gameId: Long, playerId: Long): List<Gamer>
    suspend fun getRoundGamers(roundId: Long): List<Gamer>
    fun observeGamers(gameId: Long): Flow<List<Gamer>>
    suspend fun createGamer(gameId: Long, roundId: Long, player: Player)
    suspend fun deleteGamer(roundId: Long, player: Player)
    suspend fun updateOption(id: Long, options: List<Option>)
    suspend fun clearOption(id: Long, kClass: KClass<out ScoreOption>)
}