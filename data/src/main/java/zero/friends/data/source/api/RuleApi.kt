package zero.friends.data.source.api

import retrofit2.http.GET
import zero.friends.domain.model.Rule

interface RuleApi {
    @GET("ZeroFriends/GoStopCalculator/develop/app/src/main/assets/rule.json")
    suspend fun getDefaultRule(): List<Rule>
}