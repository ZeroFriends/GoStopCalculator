package zero.friends.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import zero.friends.data.source.AssetProvider
import zero.friends.data.source.api.ManualApi
import zero.friends.domain.model.Manual
import zero.friends.domain.repository.ManualRepository
import javax.inject.Inject

class ManualRepositoryImpl @Inject constructor(
    private val assetProvider: AssetProvider,
    private val manualApi: ManualApi
) : ManualRepository {
    override suspend fun getManuals(): Flow<List<Manual>> =
        flow {
            val localJson = assetProvider.loadAsset("manual.json")
            val localManual = Json.decodeFromString<List<Manual>>(localJson)
            emit(localManual)

            val remoteManual = manualApi.getManual()
            emit(remoteManual)
        }
}

