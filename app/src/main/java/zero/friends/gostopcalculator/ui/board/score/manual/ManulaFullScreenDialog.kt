package zero.friends.gostopcalculator.ui.board.score.manual

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import kotlinx.coroutines.launch
import zero.friends.domain.model.Image
import zero.friends.domain.model.Manual
import zero.friends.domain.model.Script
import zero.friends.domain.model.Text
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.ui.common.CenterTextTopBar
import zero.friends.gostopcalculator.ui.common.GoStopDivider
import zero.friends.gostopcalculator.ui.common.GridItems
import zero.friends.gostopcalculator.ui.dialog.FullScreenDialog

@Composable
fun ManualFullScreenDialog(manualViewModel: ManualViewModel = hiltViewModel(), onDismiss: () -> Unit = {}) {

    val uiState by manualViewModel.uiState().collectAsState()
    val pagerState = rememberPagerState(pageCount = { uiState.manuals.size })
    val scope = rememberCoroutineScope()

    BackHandler {
        onDismiss()
    }

    FullScreenDialog(onDismiss = onDismiss) {
        Scaffold(
            modifier = Modifier,
            scaffoldState = rememberScaffoldState(),
            topBar = {
                CenterTextTopBar(
                    text = stringResource(id = R.string.manual_title),
                    onBack = onDismiss,
                    isRed = false
                )
            }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = stringResource(R.string.manual),
                    fontSize = 24.sp,
                    color = colorResource(id = R.color.nero),
                    fontWeight = FontWeight.Bold
                )
                ManualGrids(
                    uiState.manuals,
                    pagerState.currentPage,
                    onClick = { scope.launch { pagerState.scrollToPage(it) } }
                )
                GoStopDivider()
                HorizontalPager(
                    modifier = Modifier.background(color = colorResource(id = R.color.manual_background)),
                    state = pagerState
                ) { page ->
                    when (uiState.manuals[page]) {
                        is Image -> ImageManual(uiState.manuals[page].script, uiState.images)
                        is Text -> TextManual(uiState.manuals[page].script)
                    }
                }
            }

        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageManual(script: List<Script>, imageRes: List<Int>) {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(vertical = 10.dp)
    ) {
        itemsIndexed(script) { index, item ->
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .background(color = colorResource(id = R.color.white), shape = RoundedCornerShape(4.dp))
                    .shadow(1.dp, shape = RoundedCornerShape(1.dp)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.padding(10.dp))
                Image(
                    painter = rememberVectorPainter(image = ImageVector.vectorResource(id = imageRes[index])),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.padding(7.dp))
                Text(
                    text = item.header,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.nero)
                )
                Text(
                    text = item.body,
                    fontSize = 8.sp,
                    color = colorResource(id = R.color.nero)
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
                    color = colorResource(id = R.color.nero)
                )
                Text(
                    text = item.body,
                    fontSize = 14.sp,
                    color = colorResource(id = R.color.nero)
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
                .border(1.dp, color = colorResource(id = R.color.line))
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier,
                text = manual.title,
                fontSize = 14.sp,
                fontWeight = if (focusIndex == index) FontWeight.Bold else FontWeight.Normal,
                color = colorResource(id = if (focusIndex == index) R.color.nero else R.color.gray)
            )
        }
    }
}

@Preview
@Composable
private fun TextPreview() {
    TextManual(items = listOf(Script("피박", "승자가 피 10장 이상을 모아 점수를 얻었는데 피가 5장 이하인 경우. 승자에게 2배의 돈을 지불한다.")))
}

@Preview
@Composable
private fun ImagePreview() {
    ImageManual(
        script = listOf(Script("10장•1점", "1장 추가 시 1점씩 추가\n/ 쌍피 2점, 쓰리피 3점추가")), listOf(R.drawable.pees)
    )
}
