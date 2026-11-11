package zero.friends.gostopcalculator.ui.board.score.manual

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import zero.friends.domain.model.FullImage
import zero.friends.domain.model.Image
import zero.friends.domain.model.Manual
import zero.friends.domain.model.Script
import zero.friends.domain.model.Text
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.all_card
import zero.friends.gostopcalculator.shared.generated.resources.manual
import zero.friends.gostopcalculator.shared.generated.resources.manual_asset_file
import zero.friends.gostopcalculator.shared.generated.resources.manual_title
import zero.friends.gostopcalculator.theme.Gray
import zero.friends.gostopcalculator.theme.Line
import zero.friends.gostopcalculator.theme.ManualBackground
import zero.friends.gostopcalculator.theme.Nero
import zero.friends.gostopcalculator.ui.common.CenterTextTopBar
import zero.friends.gostopcalculator.ui.common.GoStopDivider
import zero.friends.gostopcalculator.ui.common.GridItems
import zero.friends.gostopcalculator.ui.dialog.FullScreenDialog

@Composable
fun ManualFullScreenDialog(
    manualViewModel: ManualViewModel,
    onDismiss: () -> Unit = {}
) {
    val manualAssetFile = stringResource(Res.string.manual_asset_file)
    LaunchedEffect(manualViewModel, manualAssetFile) {
        manualViewModel.loadManuals(manualAssetFile)
    }
    val uiState by manualViewModel.uiState().collectAsState()
    val pagerState = rememberPagerState(pageCount = { uiState.manuals.size })
    val scope = rememberCoroutineScope()

    FullScreenDialog(onDismiss = onDismiss) {
        Scaffold(
            modifier = Modifier,
            scaffoldState = rememberScaffoldState(),
            topBar = {
                CenterTextTopBar(
                    text = stringResource(Res.string.manual_title),
                    onBack = onDismiss,
                    isRed = false
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = stringResource(Res.string.manual),
                    fontSize = 24.sp,
                    color = Nero,
                    fontWeight = FontWeight.Bold
                )
                ManualGrids(
                    uiState.manuals,
                    pagerState.currentPage,
                    onClick = { scope.launch { pagerState.scrollToPage(it) } }
                )
                GoStopDivider()
                HorizontalPager(
                    modifier = Modifier.background(color = ManualBackground),
                    state = pagerState
                ) { page ->
                    when (val manual = uiState.manuals[page]) {
                        is Image -> ImageManual(manual.script, uiState.images)
                        is Text -> TextManual(manual.script)
                        is FullImage -> AllCardManual()
                    }
                }
            }
        }
    }
}

@Composable
fun AllCardManual() {
    Image(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        painter = painterResource(Res.drawable.all_card),
        contentDescription = null
    )
}

@Composable
fun ImageManual(script: List<Script>, imageRes: List<DrawableResource>) {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(vertical = 10.dp)
    ) {
        itemsIndexed(script) { index, item ->
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .background(color = Color.White, shape = RoundedCornerShape(4.dp))
                    .shadow(1.dp, shape = RoundedCornerShape(1.dp)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.padding(10.dp))
                Image(
                    painter = painterResource(imageRes[index]),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.padding(7.dp))
                Text(
                    text = item.header,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Nero
                )
                Text(
                    text = item.body,
                    fontSize = 8.sp,
                    color = Nero
                )
                Spacer(modifier = Modifier.padding(4.dp))
            }
        }
    }
}

@Composable
private fun TextManual(items: List<Script>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 10.dp)
    ) {
        items(items) { item ->
            Column(Modifier.padding(vertical = 10.dp)) {
                Text(
                    text = item.header,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Nero
                )
                Text(
                    text = item.body,
                    fontSize = 14.sp,
                    color = Nero
                )
            }
        }
    }
}

@Composable
private fun ManualGrids(manuals: List<Manual>, focusIndex: Int, onClick: (index: Int) -> Unit = {}) {
    GridItems(data = manuals, nColumns = 3) { index, manual ->
        Box(
            modifier = Modifier
                .clickable { onClick(index) }
                .border(1.dp, color = Line)
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier,
                text = manual.title,
                fontSize = 14.sp,
                fontWeight = if (focusIndex == index) FontWeight.Bold else FontWeight.Normal,
                color = if (focusIndex == index) Nero else Gray
            )
        }
    }
}
