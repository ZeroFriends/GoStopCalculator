package zero.friends.data.repository

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import zero.friends.data.source.AssetProvider
import zero.friends.domain.model.Manual
import zero.friends.domain.repository.ManualRepository
import javax.inject.Inject

class ManualRepositoryImpl @Inject constructor(
    private val assetProvider: AssetProvider
) : ManualRepository {
    override fun getManuals(): List<Manual> {
        val json = assetProvider.loadAsset("manual.json")
        return Json.decodeFromString(json)
    }
}

