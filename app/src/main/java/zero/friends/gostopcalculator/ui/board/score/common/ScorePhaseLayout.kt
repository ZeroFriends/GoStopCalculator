package zero.friends.gostopcalculator.ui.board.score.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.ui.common.CenterTextTopBar
import zero.friends.gostopcalculator.ui.common.DescriptionBox
import zero.friends.gostopcalculator.ui.common.background.GoStopButtonBackground
import zero.friends.gostopcalculator.util.TabKeyboardDownModifier

/**
 * Score Phase 화면들의 공통 레이아웃
 * Scaffold + TopBar + ButtonBackground + DescriptionBox를 포함
 */
@Composable
fun ScorePhaseLayout(
    gameTitle: String,
    mainText: String,
    subText: String,
    buttonText: String,
    buttonEnabled: Boolean,
    onBack: () -> Unit,
    onExit: () -> Unit,
    onButtonClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        modifier = TabKeyboardDownModifier(),
        scaffoldState = scaffoldState,
        topBar = {
            CenterTextTopBar(
                text = gameTitle,
                onBack = onBack,
                onAction = onExit,
                actionIcon = painterResource(id = R.drawable.ic_topbar_close),
                isRed = false
            )
        }
    ) {
        GoStopButtonBackground(
            buttonString = buttonText,
            buttonEnabled = buttonEnabled,
            onClick = onButtonClick,
            contents = {
                Column {
                    DescriptionBox(
                        mainText = mainText,
                        subText = subText
                    )
                    Spacer(modifier = Modifier.padding(22.dp))
                    content()
                }
            },
        )
    }
}

