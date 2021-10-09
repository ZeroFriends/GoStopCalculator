package zero.friends.domain.repository

import zero.friends.domain.model.GameAndPlayer

interface GameRepository{
    suspend fun newGame(name: String, time: String)
    fun getCurrentGameId():Long
    suspend fun getCurrentGameUser(): GameAndPlayer
    suspend fun clearGame()
}