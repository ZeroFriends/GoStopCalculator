package zero.friends.domain.repository

import zero.friends.domain.model.Manual

interface ManualRepository {
    fun getManuals(): List<Manual>
}