package zero.friends.domain.repository

import kotlinx.coroutines.flow.Flow
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.Option
import zero.friends.domain.model.Player
import kotlin.reflect.KClass

interface GamerRepository {
    suspend fun getGamer(gamerId: Long): Gamer
    suspend fun getRoundGamers(roundId: Long): List<Gamer>
    fun observeGamers(gameId: Long): Flow<List<Gamer>>
    suspend fun createGamer(gameId: Long, roundId: Long, player: Player)
    suspend fun deleteGamer(roundId: Long, player: Player)
    suspend fun updateOption(id: Long, options: List<Option>)
    suspend fun clearOption(id: Long, kClass: KClass<out Option>)
    suspend fun updateAccount(gamer: Gamer, account: Int)
    suspend fun updateScore(gamer: Gamer, score: Int, go: Int = 0)
    suspend fun addAccount(gamer: Gamer, account: Int)
    suspend fun findWinner(roundId: Long): Gamer
    suspend fun findSeller(roundId: Long): Gamer?
    suspend fun updateTarget(gamer: Gamer, account: Int, target: Gamer)
    fun observeRoundGamers(roundId: Long): Flow<List<Gamer>>
}
