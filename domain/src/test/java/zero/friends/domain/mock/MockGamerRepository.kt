package zero.friends.domain.mock

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.Option
import zero.friends.domain.model.Player
import zero.friends.domain.model.Target
import zero.friends.domain.repository.GamerRepository
import kotlin.reflect.KClass

/**
 * Mock Gamer Repository for Testing
 * 
 * 테스트에서 사용하는 GamerRepository의 Mock 구현체
 */
class MockGamerRepository : GamerRepository {
    private val roundGamersMap = mutableMapOf<Long, List<Gamer>>()
    private val accountMap = mutableMapOf<Long, Int>()
    private val targetMap = mutableMapOf<Long, MutableMap<Long, Int>>()
    private val allGamersFlow = MutableStateFlow<List<Gamer>>(emptyList())
    
    /**
     * 테스트용: 라운드의 플레이어 목록 설정
     */
    fun setRoundGamers(roundId: Long, gamers: List<Gamer>) {
        roundGamersMap[roundId] = gamers
        // 초기 계정 설정
        gamers.forEach { accountMap[it.id] = 0 }
        // allGamersFlow 업데이트
        updateAllGamersFlow()
    }

    /**
     * 테스트용: 게임의 모든 플레이어 설정
     */
    fun setAllGamers(gamers: List<Gamer>) {
        allGamersFlow.value = gamers
    }

    /**
     * 테스트용: allGamersFlow 업데이트
     */
    private fun updateAllGamersFlow() {
        allGamersFlow.value = roundGamersMap.values.flatten()
    }
    
    /**
     * 테스트용: 특정 플레이어의 계정 조회
     */
    fun getGamerAccount(gamerId: Long): Int {
        return accountMap[gamerId] ?: 0
    }
    
    /**
     * 테스트용: 모든 계정 조회
     */
    fun getAllAccounts(): Map<Long, Int> {
        return accountMap.toMap()
    }
    
    override suspend fun getGamer(gamerId: Long): Gamer {
        return roundGamersMap.values.flatten().firstOrNull { it.id == gamerId }
            ?: throw NoSuchElementException("Gamer with id $gamerId not found")
    }
    
    override suspend fun getRoundGamers(roundId: Long): List<Gamer> {
        val gamers = roundGamersMap[roundId] ?: return emptyList()
        // account와 calculate(target) 정보를 반영하여 반환
        return gamers.map { gamer ->
            val account = accountMap[gamer.id] ?: 0
            val targets = targetMap[gamer.id]?.map { (targetId, targetAccount) ->
                // targetId로 원본 gamer 찾기
                val targetGamer = roundGamersMap.values.flatten().firstOrNull { it.id == targetId }
                Target(
                    playerId = targetId,
                    name = targetGamer?.name ?: "",
                    account = targetAccount
                )
            } ?: emptyList()

            gamer.copy(
                account = account,
                calculate = targets
            )
        }
    }
    
    override fun observeGamers(gameId: Long): Flow<List<Gamer>> {
        return allGamersFlow
    }
    
    override suspend fun createGamer(gameId: Long, roundId: Long, player: Player) {
        throw NotImplementedError("createGamer is not implemented in MockGamerRepository")
    }
    
    override suspend fun deleteGamer(roundId: Long, player: Player) {
        throw NotImplementedError("deleteGamer is not implemented in MockGamerRepository")
    }
    
    override suspend fun updateOption(id: Long, options: List<Option>) {
        throw NotImplementedError("updateOption is not implemented in MockGamerRepository")
    }
    
    override suspend fun clearOption(id: Long, kClass: KClass<out Option>) {
        throw NotImplementedError("clearOption is not implemented in MockGamerRepository")
    }
    
    override suspend fun updateAccount(gamer: Gamer, account: Int) {
        accountMap[gamer.id] = account
    }
    
    override suspend fun updateScore(gamer: Gamer, score: Int) {
        throw NotImplementedError("updateScore is not implemented in MockGamerRepository")
    }
    
    override suspend fun addAccount(gamer: Gamer, account: Int) {
        val current = accountMap[gamer.id] ?: 0
        accountMap[gamer.id] = current + account
    }
    
    override suspend fun findWinner(roundId: Long): Gamer {
        throw NotImplementedError("findWinner is not implemented in MockGamerRepository")
    }
    
    override suspend fun findSeller(roundId: Long): Gamer? {
        throw NotImplementedError("findSeller is not implemented in MockGamerRepository")
    }
    
    override suspend fun updateTarget(gamer: Gamer, account: Int, target: Gamer) {
        val gamerTargets = targetMap.getOrPut(gamer.id) { mutableMapOf() }
        val currentAmount = gamerTargets[target.id] ?: 0
        gamerTargets[target.id] = currentAmount + account
    }
    
    override fun observeRoundGamers(roundId: Long): Flow<List<Gamer>> {
        throw NotImplementedError("observeRoundGamers is not implemented in MockGamerRepository")
    }
}

