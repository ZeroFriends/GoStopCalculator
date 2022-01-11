package zero.friends.domain.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

fun <T> Flow<T>.pairwise(): Flow<Pair<T, T>> {
    var prev: T? = null
    var hasPrev = false

    return transform {
        @Suppress("UNCHECKED_CAST")
        if (hasPrev)
            emit(prev as T to it)

        prev = it
        hasPrev = true
    }
}