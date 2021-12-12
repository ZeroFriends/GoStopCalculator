package zero.friends.gostopcalculator.util

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun <T> GridItems(
    data: List<T>,
    nColumns: Int,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    itemContent: @Composable BoxScope.(index: Int, item: T) -> Unit,
) {
    val rows = if (data.count() == 0) 0 else 1 + (data.count() - 1) / nColumns
    (0..rows).forEach { rowIndex ->
        Row(modifier = Modifier.padding(vertical = 5.dp), horizontalArrangement = horizontalArrangement) {
            for (columnIndex in 0 until nColumns) {
                val itemIndex = rowIndex * nColumns + columnIndex
                if (itemIndex < data.count()) {
                    Box(
                        modifier = Modifier.weight(1f, fill = true),
                        propagateMinConstraints = true
                    ) {
                        itemContent(this, itemIndex, data[itemIndex])
                    }
                } else {
                    Spacer(Modifier.weight(1f, fill = true))
                }
            }
        }
    }
}

@Composable
@Preview
private fun gridItemPreview() {
    GridItems(listOf("hello", "world", "zero", "world", "zzzz"), 2) { index, s ->
        Text(s)
    }
}