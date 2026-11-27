package zero.friends.gostopcalculator.ui.history

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.geometry.Offset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class HistoryDragDropStateTest {

    @Test
    fun `computeEdgeScrollDelta returns 0f until pointer moves beyond touchSlop`() {
        val state = HistoryDragDropState(
            lazyListState = LazyListState(),
            touchSlop = 12f,
            currentIdsProvider = { listOf(1L, 2L, 3L) },
            onMove = { _, _ -> },
            onCommit = {},
            onCancel = {},
        )

        assertEquals(0f, state.computeEdgeScrollDelta(edgeThresholdPx = 56f, maxStepPx = 14f))
    }

    @Test
    fun `onDragStart returns false on empty layout without invoking commit or cancel`() {
        var committed = false
        var cancelled = false
        val state = HistoryDragDropState(
            lazyListState = LazyListState(),
            touchSlop = 12f,
            currentIdsProvider = { listOf(10L, 20L) },
            onMove = { _, _ -> },
            onCommit = { committed = true },
            onCancel = { cancelled = true },
        )

        // 빈 레이아웃에서는 hitItem 부재로 드래그 시작 안 함
        assertFalse(state.onDragStart(Offset(10f, 10f)))
        assertFalse(committed)
        assertFalse(cancelled)
    }
}
