package zero.friends.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import zero.friends.domain.util.Inject
import zero.friends.data.source.parseAsset
import zero.friends.domain.model.Manual
import zero.friends.domain.repository.ManualRepository

class ManualRepositoryImpl @Inject constructor() : ManualRepository {
    override suspend fun getManuals(): Flow<List<Manual>> =
        flow {
            val localManual = parseAsset<List<Manual>>("manual.json")
            emit(localManual)
        }
}


