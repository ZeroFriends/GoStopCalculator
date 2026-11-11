package zero.friends.data.repository

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.jetbrains.compose.resources.ExperimentalResourceApi
import zero.friends.data.source.parseAsset
import zero.friends.data.source.safeJson
import zero.friends.domain.model.Manual
import zero.friends.domain.repository.ManualRepository
import zero.friends.domain.util.Inject
import zero.friends.gostopcalculator.shared.generated.resources.Res

class ManualRepositoryImpl @Inject constructor() : ManualRepository {
    override suspend fun getManuals(fileName: String): Flow<List<Manual>> =
        flow {
            emit(loadManualAsset(fileName))
        }.flowOn(Dispatchers.Default)

    @OptIn(ExperimentalResourceApi::class)
    private suspend fun loadManualAsset(fileName: String): List<Manual> {
        return try {
            val raw = Res.readBytes("files/$fileName").decodeToString()
            safeJson.decodeFromString<List<Manual>>(raw)
        } catch (e: CancellationException) {
            throw e
        } catch (_: Throwable) {
            parseAsset<List<Manual>>(fileName)
        }
    }
}


