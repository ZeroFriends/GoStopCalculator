package zero.friends.gostopcalculator.ui.board.rule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.stringResource
import zero.friends.domain.model.Rule
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.price
import zero.friends.gostopcalculator.shared.generated.resources.rule_title_text
import zero.friends.gostopcalculator.util.localizedName
import zero.friends.gostopcalculator.theme.Nero
import zero.friends.gostopcalculator.theme.OrangeyRed
import zero.friends.gostopcalculator.ui.common.CenterTextTopBar
import zero.friends.gostopcalculator.ui.common.ContentsCard
import zero.friends.gostopcalculator.ui.common.GridItems
import zero.friends.gostopcalculator.ui.common.TitleText

import zero.friends.gostopcalculator.ui.common.BackHandler

@Composable
fun RuleLogScreen(
    ruleLogViewModel: RuleLogViewModel,
    onBack: () -> Unit
) {
    BackHandler(enabled = true) {
        onBack()
    }

    val scaffoldState = rememberScaffoldState()

    val uiState by ruleLogViewModel.uiState().collectAsState()

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
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(horizontal = 16.dp)) {
            TitleText(
                modifier = Modifier.padding(top = 34.dp, start = 18.dp),
                text = stringResource(Res.string.rule_title_text)
            )
            Spacer(modifier = Modifier.padding(8.dp))
            ContentsCard {
                GridItems(
                    modifier = Modifier.padding(vertical = 26.dp, horizontal = 18.dp),
                    data = uiState.rule,
                    nColumns = 2
                ) { index, rule ->
                    RuleLogItem(index = index, rule = rule)
                }
            }
        }
    }
}

@Composable
fun RuleLogItem(index: Int = 0, rule: Rule = Rule()) {
    Row(
        modifier = Modifier.padding(8.dp),
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
                color = OrangeyRed
            )
            Spacer(modifier = Modifier.padding(4.dp))
            Text(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = rule.name.localizedName(),
                fontSize = 16.sp,
                color = Nero,
            )
        }
        Text(
            text = stringResource(Res.string.price, rule.score),
            textAlign = TextAlign.Center,
            color = Nero,
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}
