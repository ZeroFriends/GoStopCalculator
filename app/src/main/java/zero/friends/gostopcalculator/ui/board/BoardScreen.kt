package zero.friends.gostopcalculator.ui.board

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import zero.friends.domain.model.Game
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.di.provider.ViewModelProvider
import zero.friends.gostopcalculator.ui.common.CenterTextTopBar
import zero.friends.gostopcalculator.ui.common.RoundedCornerText
import zero.friends.gostopcalculator.util.getEntryPointFromActivity

private sealed interface BoardEvent {
    object Back : BoardEvent

}

@Composable
fun createBoardViewModel(gameId: Long): BoardViewModel {
    val entryPoint = getEntryPointFromActivity<ViewModelProvider.FactoryEntryPoint>()
    val factory = entryPoint.boardFactory()
    return viewModel(factory = BoardViewModel.provideFactory(boardViewModelFactory = factory, gameId = gameId))
}

@Composable
fun BoardScreen(boardViewModel: BoardViewModel, onBack: () -> Unit = {}) {
    val scaffoldState = rememberScaffoldState()
    val uiState by boardViewModel.getUiState().collectAsState()

    BackHandler(true) {
        onBack()
    }
    BoardScreen(
        scaffoldState = scaffoldState,
        uiState = uiState
    ) { event ->
        when (event) {
            BoardEvent.Back -> onBack()
        }
    }
}

@Composable
private fun BoardScreen(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    uiState: BoardUiState = BoardUiState(),
    event: (BoardEvent) -> Unit = {}
) {

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            CenterTextTopBar(
                text = uiState.game.createdAt,
                onBack = { event(BoardEvent.Back) },
                isRed = false,
                onAction = { TODO("준영이한테 이거 왜있냐고 물어보기") }
            )
        }
    ) {
        BoardBackground(
            boxContents = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Text(
                        text = stringResource(id = R.string.income_history),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    RoundedCornerText(text = stringResource(id = R.string.calculate_history))
                }
            },
            contents = {
                Text(text = stringResource(id = R.string.game_history), fontSize = 24.sp, fontWeight = FontWeight.Bold)
            },
            buttonText = stringResource(R.string.start_game)
        )
    }

}


@Composable
@Preview
private fun BoardScreenPreview() {
    BoardScreen(
        uiState = BoardUiState(game = Game(createdAt = "2020.11.26")),

        )
}