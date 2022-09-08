package zero.friends.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import zero.friends.data.entity.RuleEntity
import zero.friends.data.entity.RuleEntity.Companion.toRule
import zero.friends.data.source.AssetProvider
import zero.friends.data.source.api.RuleApi
import zero.friends.data.source.dao.RuleDao
import zero.friends.domain.model.Rule
import zero.friends.domain.repository.RuleRepository
import zero.friends.shared.IoDispatcher
import javax.inject.Inject

class RuleRepositoryImpl @Inject constructor(
    private val assetProvider: AssetProvider,
    private val ruleApi: RuleApi,
    private val ruleDao: RuleDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : RuleRepository {
    override suspend fun getDefaultRule(): List<Rule> {
        return runCatching {
             assetProvider<List<Rule>>("rule.json")
        }
            .recoverCatching { ruleApi.getDefaultRule() }
            .getOrThrow()
    }

    override suspend fun addNewRule(gameId: Long, rules: List<Rule>) {
        val entity = RuleEntity(
            rules = rules,
            gameId = gameId
        )
        ruleDao.insert(entity)
    }

    override suspend fun getRules(gameId: Long): List<Rule> {
        return withContext(dispatcher) {
            ruleDao.getRule(gameId).toRule()
        }
    }
}