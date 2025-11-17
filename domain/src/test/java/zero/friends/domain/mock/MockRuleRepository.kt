package zero.friends.domain.mock

import zero.friends.domain.model.Rule
import zero.friends.domain.repository.RuleRepository

/**
 * Mock Rule Repository for Testing
 * 
 * 테스트에서 사용하는 RuleRepository의 Mock 구현체
 */
class MockRuleRepository : RuleRepository {
    private val rulesMap = mutableMapOf<Long, List<Rule>>()
    
    fun setRules(gameId: Long, rules: List<Rule>) {
        rulesMap[gameId] = rules
    }
    
    override suspend fun getDefaultRule(): List<Rule> {
        return emptyList()
    }
    
    override suspend fun addNewRule(gameId: Long, rules: List<Rule>) {
        rulesMap[gameId] = rules
    }
    
    override suspend fun getRules(gameId: Long): List<Rule> {
        return rulesMap[gameId] ?: emptyList()
    }
}

