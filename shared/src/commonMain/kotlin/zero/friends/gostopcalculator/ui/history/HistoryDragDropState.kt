package zero.friends.gostopcalculator.ui.history

import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import kotlin.math.abs

class HistoryDragDropState(
    val lazyListState: LazyListState,
    private val touchSlop: Float,
    private val currentIdsProvider: () -> List<Long>,
    private val onMove: (fromIndex: Int, toIndex: Int) -> Unit,
    private val onCommit: () -> Unit,
    private val onCancel: () -> Unit,
) {
    var draggingItemId by mutableStateOf<Long?>(null)
        private set

    var draggingItemOffset by mutableStateOf(0f)
        private set

    val isDragging: Boolean
        get() = draggingItemId != null

    private var hasMovedBeyondSlop = false
    private var hasReordered = false
    private var accumulatedSlop = 0f

    fun containsItemAt(offset: Offset): Boolean = findHitItem(offset) != null

    fun onDragStart(offset: Offset): Boolean {
        if (currentIdsProvider().size < 2) return false
        val hitItem = findHitItem(offset) ?: return false
        val hitId = hitItem.key as Long

        draggingItemId = hitId
        draggingItemOffset = 0f
        accumulatedSlop = 0f
        hasMovedBeyondSlop = false
        hasReordered = false
        return true
    }

    private fun findHitItem(offset: Offset): LazyListItemInfo? {
        val contentY = offset.y - lazyListState.layoutInfo.beforeContentPadding
        return lazyListState.layoutInfo.visibleItemsInfo.firstOrNull { item ->
            item.key is Long && contentY >= item.offset && contentY <= item.offset + item.size
        }
    }

    fun onDrag(deltaY: Float) {
        if (draggingItemId == null) return
        draggingItemOffset += deltaY
        accumulatedSlop += abs(deltaY)
        if (accumulatedSlop > touchSlop) {
            hasMovedBeyondSlop = true
        }
        trySwapIfNeeded()
    }

    fun onAutoScroll(consumedDelta: Float) {
        if (draggingItemId == null || consumedDelta == 0f) return
        draggingItemOffset += consumedDelta
        hasMovedBeyondSlop = true
        trySwapIfNeeded()
    }

    fun onDragEnd() {
        if (draggingItemId == null) return
        val shouldCommit = hasReordered
        reset()
        if (shouldCommit) {
            onCommit()
        } else {
            onCancel()
        }
    }

    fun onDragCancel() {
        if (draggingItemId == null) return
        val shouldCommit = hasReordered
        reset()
        if (shouldCommit) {
            onCommit()
        } else {
            onCancel()
        }
    }

    fun computeEdgeScrollDelta(edgeThresholdPx: Float, maxStepPx: Float): Float {
        if (!hasMovedBeyondSlop) return 0f
        val currentInfo = findDraggingItemInfo() ?: return 0f
        val layoutInfo = lazyListState.layoutInfo
        val itemTop = currentInfo.offset + draggingItemOffset
        val itemBottom = itemTop + currentInfo.size
        val viewportTop = layoutInfo.viewportStartOffset.toFloat()
        val viewportBottom = layoutInfo.viewportEndOffset.toFloat()

        return when {
            itemTop < viewportTop + edgeThresholdPx && lazyListState.canScrollBackward -> {
                val ratio = ((viewportTop + edgeThresholdPx - itemTop) / edgeThresholdPx).coerceIn(0f, 1f)
                -maxStepPx * ratio
            }
            itemBottom > viewportBottom - edgeThresholdPx && lazyListState.canScrollForward -> {
                val ratio = ((itemBottom - (viewportBottom - edgeThresholdPx)) / edgeThresholdPx).coerceIn(0f, 1f)
                maxStepPx * ratio
            }
            else -> 0f
        }
    }

    private fun trySwapIfNeeded() {
        val currentInfo = findDraggingItemInfo() ?: return
        val currentId = currentInfo.key as Long
        val draggedCenterY = currentInfo.offset + draggingItemOffset + currentInfo.size / 2f

        val gameItems = lazyListState.layoutInfo.visibleItemsInfo.filter { it.key is Long }
        val targetInfo = gameItems.firstOrNull { item ->
            item.key != currentId &&
                draggedCenterY >= item.offset &&
                draggedCenterY <= item.offset + item.size
        } ?: gameItems.lastOrNull()?.takeIf { last ->
            last.key != currentId && draggedCenterY > last.offset + last.size
        } ?: gameItems.firstOrNull()?.takeIf { first ->
            first.key != currentId && draggedCenterY < first.offset
        } ?: return

        val currentIds = currentIdsProvider()
        val fromIndex = currentIds.indexOf(currentId)
        val toIndex = currentIds.indexOf(targetInfo.key as Long)
        if (fromIndex < 0 || toIndex < 0 || fromIndex == toIndex) return
        if (currentInfo.index != fromIndex || targetInfo.index != toIndex) return

        val slotAfterSwap = if (toIndex > fromIndex) {
            targetInfo.offset + targetInfo.size - currentInfo.size
        } else {
            targetInfo.offset
        }
        draggingItemOffset += (currentInfo.offset - slotAfterSwap).toFloat()
        hasReordered = true
        hasMovedBeyondSlop = true
        onMove(fromIndex, toIndex)
    }

    private fun findDraggingItemInfo(): LazyListItemInfo? {
        val id = draggingItemId ?: return null
        return lazyListState.layoutInfo.visibleItemsInfo.firstOrNull { it.key == id }
    }

    private fun reset() {
        draggingItemId = null
        draggingItemOffset = 0f
        accumulatedSlop = 0f
        hasMovedBeyondSlop = false
        hasReordered = false
    }
}

@Composable
fun rememberHistoryDragDropState(
    lazyListState: LazyListState,
    touchSlop: Float,
    currentIdsProvider: () -> List<Long>,
    onMove: (fromIndex: Int, toIndex: Int) -> Unit,
    onCommit: () -> Unit,
    onCancel: () -> Unit,
): HistoryDragDropState {
    val latestIdsProvider by rememberUpdatedState(currentIdsProvider)
    val latestOnMove by rememberUpdatedState(onMove)
    val latestOnCommit by rememberUpdatedState(onCommit)
    val latestOnCancel by rememberUpdatedState(onCancel)

    return remember(lazyListState, touchSlop) {
        HistoryDragDropState(
            lazyListState = lazyListState,
            touchSlop = touchSlop,
            currentIdsProvider = { latestIdsProvider() },
            onMove = { from, to -> latestOnMove(from, to) },
            onCommit = { latestOnCommit() },
            onCancel = { latestOnCancel() },
        )
    }
}
