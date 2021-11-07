package zero.friends.gostopcalculator.ui.precondition

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import zero.friends.domain.model.Rule
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.ui.common.AprilBackground
import zero.friends.gostopcalculator.ui.common.CenterTextTopBar
import zero.friends.gostopcalculator.ui.common.NumberTextField
import zero.friends.gostopcalculator.ui.common.SubActionOutLineButton


sealed class RuleClickEvent {
    object Back : RuleClickEvent()
    class Complete : RuleClickEvent()
    object Helper : RuleClickEvent()
}

@Composable
fun RuleScreen(ruleViewModel: RuleViewModel = hiltViewModel(), onBack: () -> Unit) {
    val scaffoldState = rememberScaffoldState()
    val uiState by ruleViewModel.getUiState().collectAsState()

    BackHandler(true) {
        onBack()
    }

    RuleScreen(
        scaffoldState = scaffoldState,
        uiState = uiState,
        clickEvent = { ruleClickEvent ->
            when (ruleClickEvent) {
                RuleClickEvent.Back -> onBack()
                is RuleClickEvent.Complete -> TODO()
            }
        },
        onUpdateRule = {
            ruleViewModel.updateRuleScore(it)
        })
}

@Composable
fun RuleScreen(
    scaffoldState: ScaffoldState,
    uiState: RuleUiState,
    clickEvent: (RuleClickEvent) -> Unit = {},
    onUpdateRule: (Rule) -> Unit = {},
) {
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            CenterTextTopBar(
                text = String.format(stringResource(id = R.string.game_setting_title), 2),
                onBack = { clickEvent(RuleClickEvent.Back) },
                onAction = null
            )
        }
    ) {
        val textFieldValue = remember {
            mutableStateOf(TextFieldValue())
        }
        AprilBackground(
            title = "게임규칙 💡",
            subTitle = "게임 플레이 시 적용될 금액입니다.\n과도한 금액이 나오지 않게 주의해 주세요 :)",
            buttonText = "완료",
            onClick = { clickEvent(RuleClickEvent.Complete()) }
        ) {
            Column {
                TitleOutlinedTextField(
                    title = "규칙이름",
                    hint = uiState.currentTime,
                    initialText = ""
                ) { textFieldValue.value = it }

                RuleLazyColumn(uiState.rules, clickEvent, onUpdateRule)
            }
        }
    }
}

@Composable
fun RuleLazyColumn(rules: List<Rule>, clickEvent: (RuleClickEvent) -> Unit, onUpdateRule: (Rule) -> Unit = {}) {
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
fun RuleItem(index: Int, rule: Rule, onUpdateScore: (Int) -> Unit = {}) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Text(text = (index + 1).toString(),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(16.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.orangey_red))
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
                        text = rule.title,
                        fontSize = 16.sp,
                        color = colorResource(id = R.color.nero)
                    )
                    if (rule.isEssential) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_star_red),
                            contentDescription = null,
                            tint = Color.Red,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
                if (rule.script.isNotEmpty()) {
                    Text(text = rule.script, fontSize = 10.sp, color = colorResource(id = R.color.gray38))
                }
            }
        }

        NumberTextField(modifier = Modifier.weight(1f), "원") {
            onUpdateScore(it.text.toInt())
        }
    }
}

@Composable
fun AmountSettingBlock(modifier: Modifier = Modifier, onHelperClick: () -> Unit) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "금액설정", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        SubActionOutLineButton("도움말") { onHelperClick() }
    }
}

@Preview(showBackground = true)
@Composable
fun RuleItemPreview() {
    RuleItem(index = 0, rule = Rule("점당", true, "필수 항목 입니다.", 0))
}

@Preview
@Composable
fun RuleScreenPreview() {
    RuleScreen(rememberScaffoldState(), uiState = RuleUiState())
}