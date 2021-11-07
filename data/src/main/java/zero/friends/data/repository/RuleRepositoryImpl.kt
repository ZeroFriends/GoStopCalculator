package zero.friends.data.repository

import android.content.Context
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import zero.friends.data.entity.RuleEntity
import zero.friends.data.source.api.RuleApi
import zero.friends.data.source.dao.RuleDao
import zero.friends.data.util.AssetUtil
import zero.friends.domain.model.Rule
import zero.friends.domain.repository.RuleRepository
import javax.inject.Inject

class RuleRepositoryImpl @Inject constructor(
    private val context: Context,
    private val ruleApi: RuleApi,
    private val ruleDao: RuleDao,
) : RuleRepository {
    override suspend fun getDefaultRule(): List<Rule> {
        return runCatching {
            val json = AssetUtil.loadAsset(context, "rule.json")
            Json.decodeFromString<List<Rule>>(json)
        }
            .getOrDefault(ruleApi.getDefaultRule())
            .map { Rule(it.title, it.isEssential, it.script, 0) }
    }

    override suspend fun addNewRule(gameId: Long, ruleName: String, rules: List<Rule>) {
        val entity = RuleEntity(
            ruleName = ruleName,
            rules = rules,
            gameId = gameId
        )
        ruleDao.insert(entity)
    }
}