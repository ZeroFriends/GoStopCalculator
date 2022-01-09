package zero.friends.gostopcalculator.ui.board.rule

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import zero.friends.domain.model.Game
import zero.friends.domain.model.Rule
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.ui.common.CenterTextTopBar
import zero.friends.gostopcalculator.ui.common.ContentsCard
import zero.friends.gostopcalculator.ui.common.GridItems
import zero.friends.gostopcalculator.ui.common.TitleText

@Composable
fun RuleLogScreen(ruleLogViewModel: RuleLogViewModel = hiltViewModel(), onBack: () -> Unit) {
    val scaffoldState = rememberScaffoldState()
    val uiState by ruleLogViewModel.uiState().collectAsState()
    BackHandler(true) {
        onBack()
    }

    RuleLogScreen(
        scaffoldState = scaffoldState,
        uiState = uiState,
        onBack = onBack
    )

}

@Composable
private fun RuleLogScreen(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    uiState: RuleLogUiState,
    onBack: () -> Unit = {}
) {

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { CenterTextTopBar(text = uiState.game.name, isRed = false, onBack = onBack) },
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            TitleText(
                modifier = Modifier.padding(top = 34.dp, start = 18.dp),
                text = stringResource(id = R.string.rule_title_text)
            )
            Spacer(modifier = Modifier.padding(8.dp))
            ContentsCard {
                Column(modifier = Modifier.padding(vertical = 24.dp, horizontal = 8.dp)) {
                    GridItems(data = uiState.rule, nColumns = 2) { index, rule ->
                        RuleLogItem(index = index, rule = rule)
                    }
                }
            }
        }
    }
}

@Composable
fun RuleLogItem(index: Int = 0, rule: Rule = Rule()) {
    Row(
        modifier = Modifier.padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            Text(
                text = (index + 1).toString(),
                modifier = Modifier
                    .align(Alignment.CenterVertically),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.orangey_red)
            )
            Spacer(modifier = Modifier.padding(4.dp))
            Text(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = rule.name,
                fontSize = 16.sp,
                color = colorResource(id = R.color.nero),
            )
        }
        Text(
            text = String.format(stringResource(id = R.string.price), rule.score),
            textAlign = TextAlign.Center,
            color = colorResource(id = R.color.nero),
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }

}

@Preview
@Composable
private fun Preview() {
    RuleLogScreen(
        uiState = RuleLogUiState(
            Game(name = "hello world"),
            listOf(
                Rule(name = "hello"),
                Rule(name = "world"),
                Rule(name = "world"),
                Rule(name = "world"),
            )
        )
    )
}