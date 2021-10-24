package zero.friends.gostopcalculator.ui.precondition

import androidx.activity.compose.BackHandler
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.ui.common.AprilBackground
import zero.friends.gostopcalculator.ui.common.CenterTextTopBar

@Composable
fun RuleScreen(ruleViewModel: RuleViewModel = hiltViewModel(), onBack: () -> Unit) {
    val scaffoldState = rememberScaffoldState()

    BackHandler(true) {
        onBack()
    }

    RuleScreen(scaffoldState)
}

@Composable
fun RuleScreen(
    scaffoldState: ScaffoldState,
) {
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            CenterTextTopBar(
                text = String.format(stringResource(id = R.string.game_setting_title), 2),
                onBack = { /*TODO*/ },
                onAction = null
            )
        }
    ) {
        AprilBackground(
            title = "게임규칙 💡",
            subTitle = "게임 플레이 시 적용될 금액입니다.\n" +
                    "과도한 금액이 나오지 않게 주의해 주세요 :)"
        ) {

        }
    }
}

@Preview
@Composable
fun RuleScreenPreview() {
    RuleScreen {}
}