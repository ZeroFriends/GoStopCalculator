package zero.friends.gostopcalculator.ui.precondition

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import zero.friends.domain.model.Game
import zero.friends.domain.model.Rule
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.ui.common.AprilBackground
import zero.friends.gostopcalculator.ui.common.CenterTextTopBar
import zero.friends.gostopcalculator.ui.common.NumberTextField
import zero.friends.gostopcalculator.ui.common.RoundedCornerText
import zero.friends.gostopcalculator.util.TabKeyboardDownModifier


sealed class RuleClickEvent {
    object Back : RuleClickEvent()
    object Complete : RuleClickEvent()
    object Helper : RuleClickEvent()
}

@Composable
fun RuleScreen(ruleViewModel: RuleViewModel = hiltViewModel(), onNext: (Game) -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    val uiState by ruleViewModel.getUiState().collectAsState()
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        ruleViewModel.updateRule()
    }

    BackHandler(true) {
        onBack()
    }

    RuleScreen(
        scaffoldState = scaffoldState,
        uiState = uiState,
        clickEvent = { ruleClickEvent ->
            when (ruleClickEvent) {
                RuleClickEvent.Back -> onBack()
                is RuleClickEvent.Complete -> {
                    scope.launch {
                        val game = ruleViewModel.startGame()
                        onNext(game)
                    }

                }
                RuleClickEvent.Helper -> Toast.makeText(context, "아직 기능구현 안됨", Toast.LENGTH_SHORT).show()
            }
        },
        onUpdateRule = {
            ruleViewModel.updateRuleScore(it)
            ruleViewModel.checkButtonState()
        })


}

@Composable
private fun RuleScreen(
    scaffoldState: ScaffoldState,
    uiState: RuleUiState,
    clickEvent: (RuleClickEvent) -> Unit = {},
    onUpdateRule: (Rule) -> Unit = {},
) {
    Scaffold(
        modifier = TabKeyboardDownModifier(),
        scaffoldState = scaffoldState,
        topBar = {
            CenterTextTopBar(
                text = String.format(stringResource(id = R.string.game_setting_title), 2),
                onBack = { clickEvent(RuleClickEvent.Back) },
                onAction = null
            )
        }
    ) {
        AprilBackground(
            title = stringResource(R.string.rule_title),
            subTitle = stringResource(id = R.string.rule_subtitle),
            buttonText = stringResource(id = R.string.complete),
            buttonEnabled = uiState.enableComplete,
            onClick = { clickEvent(RuleClickEvent.Complete) }
        ) {
            RuleLazyColumn(uiState.rules, clickEvent, onUpdateRule)
        }
    }
}

@Composable
private fun RuleLazyColumn(rules: List<Rule>, clickEvent: (RuleClickEvent) -> Unit, onUpdateRule: (Rule) -> Unit = {}) {
    LazyColumn(contentPadding = PaddingValues(top = 35.dp, bottom = 12.dp)) {
        item {
            AmountSettingBlock { clickEvent(RuleClickEvent.Helper) }
        }
        itemsIndexed(rules) { index, rule ->
            RuleItem(index = index, rule) {
                onUpdateRule(rule.copy(score = it))
            }
        }

    }
}

@Composable
private fun RuleItem(index: Int, rule: Rule, onUpdateScore: (Int) -> Unit = {}) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = (index + 1).toString(),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(16.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.orangey_red)
            )
            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = rule.name,
                        fontSize = 16.sp,
                        color = colorResource(id = R.color.nero)
                    )
                    if (rule.isEssential) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_star_red),
                            contentDescription = null,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
                if (rule.script.isNotEmpty()) {
                    Text(text = rule.script, fontSize = 10.sp, color = colorResource(id = R.color.gray38))
                }
            }
        }

        NumberTextField(modifier = Modifier.weight(1f), endText = stringResource(R.string.won)) {
            onUpdateScore(it)
        }
    }
}

@Composable
private fun AmountSettingBlock(modifier: Modifier = Modifier, onHelperClick: () -> Unit) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = stringResource(R.string.price_setting), fontSize = 16.sp, fontWeight = FontWeight.Bold)
        RoundedCornerText(
            stringResource(R.string.help),
            colorResource(id = R.color.orangey_red),
            fontSize = 14.sp
        ) { onHelperClick() }
    }
}

@Preview(showBackground = true)
@Composable
private fun RuleItemPreview() {
    RuleItem(index = 0, rule = Rule("점당", true, "필수 항목 입니다.", 0))
}

@Preview
@Composable
private fun RuleScreenPreview() {
    RuleScreen(rememberScaffoldState(), uiState = RuleUiState())
}