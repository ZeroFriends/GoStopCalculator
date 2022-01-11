package zero.friends.gostopcalculator.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import zero.friends.domain.model.Gamer

@Composable
fun <T> GridItems(
    modifier: Modifier = Modifier,
    data: List<T>,
    nColumns: Int,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Center,
    itemContent: @Composable BoxScope.(index: Int, item: T) -> Unit,
) {
    Column(modifier = modifier) {
        val rows = if (data.count() == 0) 0 else 1 + (data.count() - 1) / nColumns
        (0 until rows).forEach { rowIndex ->
            Row(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(),
                horizontalArrangement = horizontalArrangement,
                verticalAlignment = Alignment.CenterVertically
            ) {
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
}

@Composable
@Preview(showBackground = true)
private fun gridItemPreview() {
    GridItems(
        data = listOf(
            Gamer(name = "zero"),
            Gamer(name = "dev"),
            Gamer(name = "hello"),
            Gamer(name = "world")
        ), nColumns = 2
    ) { index, s ->
        GamerItem(index = index, gamer = s)
    }
}