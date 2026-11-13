package zero.friends.gostopcalculator.ui.board.score.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import zero.friends.gostopcalculator.navigation.LocalNativeTopBarHeight
import zero.friends.gostopcalculator.navigation.LocalUseNativeNavigation
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.ic_topbar_close
import zero.friends.gostopcalculator.ui.common.CenterTextTopBar
import zero.friends.gostopcalculator.ui.common.DescriptionBox
import zero.friends.gostopcalculator.ui.common.background.GoStopButtonBackground
import zero.friends.gostopcalculator.util.TabKeyboardDownModifier

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
    content: @Composable () -> Unit,
) {
    val scaffoldState = rememberScaffoldState()
    val topBarPadding = if (LocalUseNativeNavigation.current) LocalNativeTopBarHeight.current else 0.dp

    Scaffold(
        modifier = TabKeyboardDownModifier(),
        scaffoldState = scaffoldState,
        topBar = {
            CenterTextTopBar(
                text = gameTitle,
                onBack = onBack,
                onAction = onExit,
                actionIcon = painterResource(Res.drawable.ic_topbar_close),
                isRed = false,
                swipeBackEnabled = false,
            )
        }
    ) { innerPadding ->
        GoStopButtonBackground(
            modifier = Modifier.padding(innerPadding),
            buttonString = buttonText,
            buttonEnabled = buttonEnabled,
            onClick = onButtonClick,
        ) {
            Column(
                modifier = Modifier
                    .padding(top = topBarPadding)
                    .padding(16.dp)
            ) {
                DescriptionBox(
                    mainText = mainText,
                    subText = subText
                )
                Spacer(modifier = Modifier.padding(22.dp))
                content()
            }
        }
    }
}
