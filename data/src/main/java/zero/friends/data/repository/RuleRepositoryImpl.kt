package zero.friends.data.repository

import android.content.Context
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import zero.friends.data.dto.RuleDto
import zero.friends.data.source.api.RuleApi
import zero.friends.data.util.AssetUtil
import zero.friends.domain.model.Rule
import zero.friends.domain.repository.RuleRepository
import javax.inject.Inject

class RuleRepositoryImpl @Inject constructor(private val context: Context, private val ruleApi: RuleApi) :
    RuleRepository {
    override suspend fun getDefaultRule(): List<Rule> {
        val json = AssetUtil.loadAsset(context, "rule.json")
        val result = Json.decodeFromString<List<RuleDto>>(json)

        return runCatching {
            ruleApi.getDefaultRule()
                .map { Rule(it.title, it.isEssential, it.script, 0) }
        }.getOrDefault(result.map { Rule(it.title, it.isEssential, it.script, 0) })
    }
}