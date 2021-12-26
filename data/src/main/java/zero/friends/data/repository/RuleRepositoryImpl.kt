package zero.friends.data.repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
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
    @ApplicationContext private val context: Context,
    private val ruleApi: RuleApi,
    private val ruleDao: RuleDao,
) : RuleRepository {
    override suspend fun getDefaultRule(): List<Rule> {
        return runCatching {
            val json = AssetUtil.loadAsset(context, "rule.json")
            Json.decodeFromString<List<Rule>>(json)
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
}