package zero.friends.domain.repository

import zero.friends.domain.model.Player

interface PlayerRepository {
    suspend fun deletePlayer(gameId: Long, player: Player)
    suspend fun getPlayers(gameId: Long): List<Player>
    suspend fun editPlayer(gameId: Long, player: Player, editPlayer: Player)
    suspend fun isExistPlayer(gameId: Long, name: String): Boolean
    suspend fun addPlayers(gameId: Long, players: List<Player>)
}