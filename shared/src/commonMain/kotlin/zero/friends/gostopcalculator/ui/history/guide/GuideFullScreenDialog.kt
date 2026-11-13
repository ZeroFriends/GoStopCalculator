package zero.friends.gostopcalculator.ui.history.guide

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.guide
import zero.friends.gostopcalculator.shared.generated.resources.ic_topbar_close
import zero.friends.gostopcalculator.shared.generated.resources.next
import zero.friends.gostopcalculator.shared.generated.resources.start
import zero.friends.gostopcalculator.theme.OrangeyRed
import zero.friends.gostopcalculator.ui.common.BackHandler
import zero.friends.gostopcalculator.ui.common.CenterTextTopBar
import zero.friends.gostopcalculator.ui.common.SystemBarAppearance
import zero.friends.gostopcalculator.ui.dialog.FullScreenDialog

@Composable
fun GuideFullScreenDialog(
    guideViewModel: GuideViewModel = viewModel { GuideViewModel() },
    onDismiss: () -> Unit = {},
) {
    val uiState by guideViewModel.uiState().collectAsState()
    val pageCount = uiState.pagerList.size

    FullScreenDialog(onDismiss = onDismiss) {
        val pagerState = rememberPagerState(pageCount = { pageCount })
        val scope = rememberCoroutineScope()
        val isEndPage by remember(pageCount) {
            derivedStateOf { pageCount == 0 || pagerState.currentPage == pageCount - 1 }
        }
        val isStartPage by remember {
            derivedStateOf { pagerState.currentPage == 0 }
        }

        BackHandler(enabled = true) {
            if (!isStartPage) {
                scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
            } else {
                onDismiss()
            }
        }

        Scaffold(
            modifier = Modifier,
            scaffoldState = rememberScaffoldState(),
            topBar = {
                CenterTextTopBar(
                    text = stringResource(Res.string.guide),
                    onAction = onDismiss,
                    actionIcon = painterResource(Res.drawable.ic_topbar_close),
                    isRed = false,
                    onBack = if (isStartPage) {
                        null
                    } else {
                        { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } }
                    }
                )
            }
        ) { innerPadding ->
            SystemBarAppearance(
                isLightStatusBar = true,
                isLightNavigationBar = false,
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(color = OrangeyRed)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    PagerIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        pageCount = pageCount,
                        currentPage = pagerState.currentPage,
                        inactiveColor = Color.White.copy(alpha = 0.5f),
                        activeColor = Color.White,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalPager(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        state = pagerState
                    ) { page ->
                        Image(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            painter = painterResource(uiState.pagerList[page]),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        color = Color.Transparent
                    ) {
                        Button(
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black),
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
                                text = stringResource(if (isEndPage) Res.string.start else Res.string.next),
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        }
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
                        shape = CircleShape
                    )
            )
        }
    }
}
