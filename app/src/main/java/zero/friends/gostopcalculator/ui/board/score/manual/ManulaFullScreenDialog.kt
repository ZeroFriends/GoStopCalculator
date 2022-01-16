package zero.friends.gostopcalculator.ui.board.score.manual

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
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

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ManualFullScreenDialog(manualViewModel: ManualViewModel = hiltViewModel(), onDismiss: () -> Unit = {}) {

    val uiState by manualViewModel.uiState().collectAsState()
    val pagerState = rememberPagerState()
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
                    count = uiState.manuals.size,
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
        cells = GridCells.Fixed(2),
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
                Image(painter = painterResource(id = imageRes[index]), contentDescription = null)
                Spacer(modifier = Modifier.padding(15.dp))
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
                .border(1.dp, color = colorResource(id = R.color.line))
                .padding(vertical = 12.dp)
                .clickable { onClick(index) },
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
    TextManual(items = listOf(Script("header", "body")))
}

@Preview
@Composable
private fun ImagePreview() {
    ImageManual(script = listOf(Script("header", "body")), emptyList())
}