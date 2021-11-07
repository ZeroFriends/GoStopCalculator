package zero.friends.domain.repository

import zero.friends.domain.model.Rule

interface RuleRepository {
    suspend fun getDefaultRule(): List<Rule>
    suspend fun addNewRule(gameId: Long, ruleName: String, rules: List<Rule>)
}