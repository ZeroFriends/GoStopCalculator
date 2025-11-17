package zero.friends.gostopcalculator.ui.history.guide

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import kotlinx.coroutines.launch
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.ui.common.CenterTextTopBar
import zero.friends.gostopcalculator.ui.dialog.FullScreenDialog


@Composable
fun GuideFullScreenDialog(
    guideViewModel: GuideViewModel = hiltViewModel(),
    onDismiss: () -> Unit = {},
) {
    val uiState = guideViewModel.uiState().collectAsState()
    val pagerState = rememberPagerState(pageCount = { 6 })
    val scope = rememberCoroutineScope()

    BackHandler {
        onDismiss()
    }
    val isEndPage by remember {
        derivedStateOf { pagerState.currentPage == uiState.value.pagerList.size - 1 }
    }
    val isStartPage by remember {
        derivedStateOf { pagerState.currentPage == 0 }
    }

    FullScreenDialog(onDismiss = onDismiss) {
        Scaffold(
            modifier = Modifier,
            scaffoldState = rememberScaffoldState(),
            topBar = {
                CenterTextTopBar(
                    text = stringResource(id = R.string.guide),
                    onAction = onDismiss,
                    actionIcon = painterResource(id = R.drawable.ic_topbar_close),
                    isRed = false,
                    onBack = if (isStartPage) {
                        null
                    } else {
                        { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } }
                    }
                )
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = colorResource(id = R.color.orangey_red))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                ) {
                    Spacer(modifier = Modifier.padding(16.dp))
                    PagerIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        pageCount = 6,
                        currentPage = pagerState.currentPage,
                        inactiveColor = Color.White.copy(alpha = 0.5f),
                        activeColor = Color.White,
                    )
                    Spacer(modifier = Modifier.padding(16.dp))
                    HorizontalPager(state = pagerState) { page ->
                        Image(
                            modifier = Modifier.fillMaxSize(),
                            painter = painterResource(id = uiState.value.pagerList[page]),
                            contentDescription = null,
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .background(color = colorResource(id = android.R.color.transparent))
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    color = colorResource(id = android.R.color.transparent)
                ) {
                    Button(
                        modifier = Modifier,
                        colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.black)),
                        shape = RoundedCornerShape(26.dp),
                        contentPadding = PaddingValues(16.dp),
                        onClick = {
                            scope.launch {
                                if (isEndPage) onDismiss()
                                else pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    ) {
                        Text(
                            text = stringResource(if (isEndPage) R.string.start else R.string.next),
                            color = colorResource(id = R.color.white),
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }

    }

}

@Composable
private fun PagerIndicator(
    modifier: Modifier = Modifier,
    pageCount: Int,
    currentPage: Int,
    activeColor: Color = Color.White,
    inactiveColor: Color = Color.White.copy(alpha = 0.5f),
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(pageCount) { index ->
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = if (index == currentPage) activeColor else inactiveColor,
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
            )
        }
    }
}

@Preview
@Composable
private fun GuideScreenPreview() {
    GuideFullScreenDialog()
}
