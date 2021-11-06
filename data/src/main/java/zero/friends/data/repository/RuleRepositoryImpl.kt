package zero.friends.data.repository

import zero.friends.data.source.api.RuleApi
import zero.friends.domain.model.Rule
import zero.friends.domain.repository.RuleRepository
import javax.inject.Inject

class RuleRepositoryImpl @Inject constructor(private val ruleApi: RuleApi) : RuleRepository {
    override suspend fun getDefaultRule(): List<Rule> {
        val rules = ruleApi.getDefaultRule()
        return rules.map {
            Rule(it.title, it.isEssential, it.script, 0)
        }
    }
}