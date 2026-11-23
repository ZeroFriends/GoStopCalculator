package zero.friends.domain.mock

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.LoserOption
import zero.friends.domain.model.Option
import zero.friends.domain.model.Player
import zero.friends.domain.model.ScoreOption
import zero.friends.domain.model.SellerOption
import zero.friends.domain.model.Target
import zero.friends.domain.model.WinnerOption
import zero.friends.domain.repository.GamerRepository
import kotlin.reflect.KClass

/**
 * Mock Gamer Repository for Testing
 *
 * 실제 Room DB 기반 GamerRepositoryImpl과 동일한 상태 갱신/관찰 계약을 제공한다.
 */
class MockGamerRepository : GamerRepository {
    private val roundGamersMap = mutableMapOf<Long, List<Gamer>>()
    private val accountMap = mutableMapOf<Long, Int>()
    private val targetMap = mutableMapOf<Long, MutableMap<Long, Int>>()
    private val allGamersFlow = MutableStateFlow<List<Gamer>>(emptyList())
    private val roundFlows = mutableMapOf<Long, MutableStateFlow<List<Gamer>>>()

    fun clear() {
        roundGamersMap.clear()
        accountMap.clear()
        targetMap.clear()
        allGamersFlow.value = emptyList()
        roundFlows.values.forEach { it.value = emptyList() }
        roundFlows.clear()
    }

    fun setRoundGamers(roundId: Long, gamers: List<Gamer>) {
        roundGamersMap[roundId] = gamers
        gamers.forEach { accountMap[it.id] = it.account }
        notifyFlows(roundId)
    }

    fun setAllGamers(gamers: List<Gamer>) {
        allGamersFlow.value = gamers
    }

    private fun buildHydratedRoundGamers(roundId: Long): List<Gamer> {
        val gamers = roundGamersMap[roundId] ?: return emptyList()
        val allGamers = roundGamersMap.values.flatten()
        return gamers.map { gamer ->
            val account = accountMap[gamer.id] ?: 0
            val targets = targetMap[gamer.id]?.map { (targetPlayerId, targetAccount) ->
                val targetGamer = allGamers.firstOrNull {
                    it.playerId == targetPlayerId || it.id == targetPlayerId
                }
                Target(
                    playerId = targetPlayerId,
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

    private fun notifyFlows(roundId: Long? = null) {
        if (roundId != null) {
            roundFlows[roundId]?.value = buildHydratedRoundGamers(roundId)
        } else {
            roundFlows.forEach { (rId, flow) ->
                flow.value = buildHydratedRoundGamers(rId)
            }
        }
        allGamersFlow.value = roundGamersMap.keys.flatMap { buildHydratedRoundGamers(it) }
    }

    private fun mutateGamer(gamerId: Long, transform: (Gamer) -> Gamer) {
        var affectedRoundId: Long? = null
        roundGamersMap.forEach { (roundId, list) ->
            if (list.any { it.id == gamerId }) {
                affectedRoundId = roundId
                roundGamersMap[roundId] = list.map { if (it.id == gamerId) transform(it) else it }
            }
        }
        notifyFlows(affectedRoundId)
    }

    fun getGamerAccount(gamerId: Long): Int {
        return accountMap[gamerId] ?: 0
    }

    fun getAllAccounts(): Map<Long, Int> {
        return accountMap.toMap()
    }

    override suspend fun getGamer(gamerId: Long): Gamer {
        val roundId = roundGamersMap.entries.firstOrNull { (_, list) ->
            list.any { it.id == gamerId }
        }?.key ?: throw NoSuchElementException("Gamer with id $gamerId not found")
        return buildHydratedRoundGamers(roundId).first { it.id == gamerId }
    }

    override suspend fun getRoundGamers(roundId: Long): List<Gamer> {
        return buildHydratedRoundGamers(roundId)
    }

    override fun observeGamers(gameId: Long): Flow<List<Gamer>> {
        return allGamersFlow
    }

    override suspend fun createGamer(gameId: Long, roundId: Long, player: Player) {
        val nextId = (roundGamersMap.values.flatten().maxOfOrNull { it.id } ?: 0L) + 1L
        val current = roundGamersMap[roundId].orEmpty()
        val created = Gamer(
            id = nextId,
            gameId = gameId,
            roundId = roundId,
            playerId = player.id,
            name = player.name
        )
        roundGamersMap[roundId] = current + created
        accountMap[nextId] = 0
        notifyFlows(roundId)
    }

    override suspend fun deleteGamer(roundId: Long, player: Player) {
        val current = roundGamersMap[roundId].orEmpty()
        roundGamersMap[roundId] = current.filterNot { it.playerId == player.id }
        notifyFlows(roundId)
    }

    override suspend fun updateOption(id: Long, options: List<Option>) {
        mutateGamer(id) { gamer ->
            var updated = gamer
            options.groupBy { it::class }.forEach { (kClass, values) ->
                updated = when (kClass) {
                    WinnerOption::class -> updated.copy(winnerOption = values.filterIsInstance<WinnerOption>().firstOrNull())
                    SellerOption::class -> updated.copy(sellerOption = values.filterIsInstance<SellerOption>().firstOrNull())
                    ScoreOption::class -> updated.copy(scoreOption = values.filterIsInstance<ScoreOption>())
                    LoserOption::class -> updated.copy(loserOption = values.filterIsInstance<LoserOption>())
                    else -> updated
                }
            }
            updated
        }
    }

    override suspend fun clearOption(id: Long, kClass: KClass<out Option>) {
        mutateGamer(id) { gamer ->
            when (kClass) {
                WinnerOption::class -> gamer.copy(winnerOption = null)
                SellerOption::class -> gamer.copy(sellerOption = null)
                ScoreOption::class -> gamer.copy(scoreOption = emptyList())
                LoserOption::class -> gamer.copy(loserOption = emptyList())
                else -> gamer
            }
        }
    }

    override suspend fun updateAccount(gamer: Gamer, account: Int) {
        accountMap[gamer.id] = account
        notifyFlows(gamer.roundId)
    }

    override suspend fun updateScore(gamer: Gamer, score: Int, go: Int) {
        mutateGamer(gamer.id) { it.copy(score = score, go = go) }
    }

    override suspend fun addAccount(gamer: Gamer, account: Int) {
        val current = accountMap[gamer.id] ?: 0
        accountMap[gamer.id] = current + account
        notifyFlows(gamer.roundId)
    }

    override suspend fun findWinner(roundId: Long): Gamer {
        return getRoundGamers(roundId).first { it.winnerOption != null }
    }

    override suspend fun findSeller(roundId: Long): Gamer? {
        return getRoundGamers(roundId).firstOrNull { it.sellerOption != null }
    }

    override suspend fun updateTarget(gamer: Gamer, account: Int, target: Gamer) {
        val gamerTargets = targetMap.getOrPut(gamer.id) { mutableMapOf() }
        val targetKey = if (target.playerId != 0L) target.playerId else target.id
        val currentAmount = gamerTargets[targetKey] ?: 0
        gamerTargets[targetKey] = currentAmount + account
        notifyFlows(gamer.roundId)
    }

    override fun observeRoundGamers(roundId: Long): Flow<List<Gamer>> {
        return roundFlows.getOrPut(roundId) {
            MutableStateFlow(buildHydratedRoundGamers(roundId))
        }
    }
}
