package zero.friends.data.source.api

import retrofit2.http.GET
import zero.friends.domain.model.Manual

interface ManualApi {
    @GET("ZeroFriends/GoStopCalculator/main/app/src/main/assets/manual.json")
    suspend fun getManual(): List<Manual>
}