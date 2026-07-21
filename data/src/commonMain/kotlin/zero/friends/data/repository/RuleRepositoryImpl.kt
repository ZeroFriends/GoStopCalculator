package zero.friends.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import zero.friends.domain.util.Inject
import zero.friends.data.entity.RuleEntity
import zero.friends.data.entity.RuleEntity.Companion.toRule
import zero.friends.data.source.dao.RuleDao
import zero.friends.data.source.parseAsset
import zero.friends.domain.model.Rule
import zero.friends.domain.repository.RuleRepository

class RuleRepositoryImpl @Inject constructor(
    private val ruleDao: RuleDao,
) : RuleRepository {
    override suspend fun getDefaultRule(): List<Rule> {
        return runCatching {
            parseAsset<List<Rule>>("rule.json")
        }.getOrDefault(emptyList())
    }

    override suspend fun addNewRule(gameId: Long, rules: List<Rule>) {
        val entity = RuleEntity(
            rules = rules,
            gameId = gameId
        )
        ruleDao.insert(entity)
    }

    override suspend fun getRules(gameId: Long): List<Rule> {
        return withContext(Dispatchers.Default) {
            ruleDao.getRule(gameId).toRule()
        }
    }
}

