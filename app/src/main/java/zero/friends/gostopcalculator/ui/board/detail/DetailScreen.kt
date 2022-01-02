package zero.friends.gostopcalculator.ui.board.detail

import androidx.activity.compose.BackHandler
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import zero.friends.gostopcalculator.di.entrypoint.EntryPoint
import zero.friends.gostopcalculator.ui.common.CenterTextTopBar
import zero.friends.gostopcalculator.util.getEntryPointFromActivity

@Composable
fun createDetailViewModel(roundId: Long): DetailViewModel {
    val entryPoint = getEntryPointFromActivity<EntryPoint>()
    val factory = entryPoint.detailFactory()
    return viewModel(factory = DetailViewModel.provideFactory(factory, roundId))
}

@Composable
fun DetailScreen(detailViewModel: DetailViewModel = hiltViewModel(), onBack: () -> Unit = {}) {
    val scaffoldState = rememberScaffoldState()
    val uiState by detailViewModel.uiState().collectAsState()

    BackHandler(true) {
        onBack()
    }

    DetailScreen(
        scaffoldState = scaffoldState,
        uiState = uiState,
        onBack = onBack
    )
}

@Composable
private fun DetailScreen(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    uiState: DetailUiState,
    onBack: () -> Unit = {}
) {
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            CenterTextTopBar(
                text = uiState.game.name,
                onBack = onBack,
                isRed = false
            )
        }
    ) {
        //todo Draw DetailScreen
    }
}

@Preview
@Composable
private fun Preview() {
    DetailScreen()
}