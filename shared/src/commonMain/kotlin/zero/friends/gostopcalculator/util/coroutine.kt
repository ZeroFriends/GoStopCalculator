package zero.friends.gostopcalculator.util

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch

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

public suspend fun <T, R> Iterable<T>.mapAsync(
    transformation: suspend (T) -> R,
): List<R> = coroutineScope {
    map { async { transformation(it) } }.awaitAll()
}

public suspend fun <T, R> Iterable<T>.mapAsyncNotNull(
    transformation: suspend (T) -> R?,
): List<R> = coroutineScope {
    map { async { transformation(it) } }.mapNotNull { it.await() }
}

public fun <T, R> Flow<T>.mapAsync(transform: suspend (T) -> R): Flow<R> = channelFlow {
    collect { value ->
        launch { send(transform(value)) }
    }
}
