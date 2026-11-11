package zero.friends.data.repository

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.ExperimentalResourceApi
import zero.friends.domain.util.Inject
import zero.friends.data.entity.RuleEntity
import zero.friends.data.entity.RuleEntity.Companion.toRule
import zero.friends.data.source.dao.RuleDao
import zero.friends.data.source.parseAsset
import zero.friends.data.source.safeJson
import zero.friends.domain.model.Rule
import zero.friends.domain.repository.RuleRepository
import zero.friends.gostopcalculator.shared.generated.resources.Res

class RuleRepositoryImpl @Inject constructor(
    private val ruleDao: RuleDao,
) : RuleRepository {
    @OptIn(ExperimentalResourceApi::class)
    override suspend fun getDefaultRule(): List<Rule> = withContext(Dispatchers.Default) {
        try {
            val raw = Res.readBytes("files/rule.json").decodeToString()
            safeJson.decodeFromString<List<Rule>>(raw)
        } catch (e: CancellationException) {
            throw e
        } catch (_: Throwable) {
            runCatching {
                parseAsset<List<Rule>>("rule.json")
            }.getOrDefault(emptyList())
        }
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

