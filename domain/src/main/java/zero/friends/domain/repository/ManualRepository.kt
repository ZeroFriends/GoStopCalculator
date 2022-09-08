package zero.friends.domain.repository

import kotlinx.coroutines.flow.Flow
import zero.friends.domain.model.Manual

interface ManualRepository {
    suspend fun getManuals(): Flow<List<Manual>>
}