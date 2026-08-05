package zero.friends.gostopcalculator.ui.precondition

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import zero.friends.domain.model.Rule
import zero.friends.domain.util.Const
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.complete
import zero.friends.gostopcalculator.shared.generated.resources.help
import zero.friends.gostopcalculator.shared.generated.resources.help_ddaddak
import zero.friends.gostopcalculator.shared.generated.resources.help_ddaddak_sub
import zero.friends.gostopcalculator.shared.generated.resources.help_fuck
import zero.friends.gostopcalculator.shared.generated.resources.help_score
import zero.friends.gostopcalculator.shared.generated.resources.help_selling
import zero.friends.gostopcalculator.shared.generated.resources.ic_star_red
import zero.friends.gostopcalculator.shared.generated.resources.price_setting
import zero.friends.gostopcalculator.shared.generated.resources.rule_subtitle
import zero.friends.gostopcalculator.shared.generated.resources.rule_title
import zero.friends.gostopcalculator.shared.generated.resources.won
import zero.friends.gostopcalculator.theme.DustyGray
import zero.friends.gostopcalculator.theme.Gray
import zero.friends.gostopcalculator.theme.Gray38
import zero.friends.gostopcalculator.theme.Nero
import zero.friends.gostopcalculator.theme.OrangeyRed
import zero.friends.gostopcalculator.ui.common.BackHandler
import zero.friends.gostopcalculator.ui.common.CenterTextTopBar
import zero.friends.gostopcalculator.ui.common.NumberTextField
import zero.friends.gostopcalculator.ui.common.RoundedCornerText
import zero.friends.gostopcalculator.ui.common.background.AprilBackground
import zero.friends.gostopcalculator.util.TabKeyboardDownModifier

sealed class RuleClickEvent {
    object Back : RuleClickEvent()
    object Complete : RuleClickEvent()
    object Helper : RuleClickEvent()
}

@Composable
fun RuleScreen(
    ruleViewModel: RuleViewModel,
    onNext: (gameId: Long) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(enabled = true) {
        onBack()
    }

    val scaffoldState = rememberScaffoldState()

    val uiState by ruleViewModel.getUiState().collectAsState()
    val scope = rememberCoroutineScope()

    var openDialog by remember {
        mutableStateOf(false)
    }

    if (openDialog) {
        RuleHelpDialog(onClose = { openDialog = false })
    }

    RuleScreen(
        scaffoldState = scaffoldState,
        uiState = uiState,
        clickEvent = { ruleClickEvent ->
            when (ruleClickEvent) {
                RuleClickEvent.Back -> onBack()
                is RuleClickEvent.Complete -> {
                    scope.launch {
                        val gameId = ruleViewModel.startGame()
                        onNext(gameId)
                    }
                }
                RuleClickEvent.Helper -> openDialog = true
            }
        },
        onUpdateRule = { rule, score ->
            ruleViewModel.updateRuleScore(rule, score)
        }
    )
}

@Composable
private fun RuleHelpDialog(
    onClose: () -> Unit = {},
    scrollState: ScrollState = rememberScrollState()
) {
    Dialog(
        onDismissRequest = onClose,
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(Res.string.help), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Nero)
            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 18.dp)
                    .verticalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.padding(10.dp))
                Text(text = stringResource(Res.string.help_score), fontSize = 14.sp, color = Nero)
                Spacer(modifier = Modifier.padding(3.dp))
                Text(text = stringResource(Res.string.help_fuck), fontSize = 14.sp, color = Nero)
                Spacer(modifier = Modifier.padding(3.dp))
                Text(text = stringResource(Res.string.help_ddaddak), fontSize = 14.sp, color = Nero)
                Text(
                    text = stringResource(Res.string.help_ddaddak_sub),
                    fontSize = 10.sp,
                    color = Gray
                )
                Spacer(modifier = Modifier.padding(3.dp))
                Text(text = stringResource(Res.string.help_selling), fontSize = 14.sp, color = Nero)
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                onClick = onClose
            ) {
                Text(
                    text = "확인",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun RuleScreen(
    scaffoldState: ScaffoldState,
    uiState: RuleUiState,
    clickEvent: (RuleClickEvent) -> Unit = {},
    onUpdateRule: (rule: Rule, score: Long) -> Unit = { _, _ -> }
) {
    Scaffold(
        modifier = TabKeyboardDownModifier(),
        scaffoldState = scaffoldState,
        topBar = {
            CenterTextTopBar(
                text = "게임 설정 (2/2)",
                onBack = { clickEvent(RuleClickEvent.Back) },
            )
        }
    ) { innerPadding ->
        AprilBackground(
            modifier = Modifier.padding(innerPadding),
            title = stringResource(Res.string.rule_title),
            subTitle = stringResource(Res.string.rule_subtitle),
            buttonText = stringResource(Res.string.complete),
            buttonEnabled = uiState.enableComplete,
            onClick = { clickEvent(RuleClickEvent.Complete) }
        ) {
            RuleLazyColumn(uiState.rules, clickEvent, onUpdateRule)
        }
    }
}

@Composable
private fun RuleLazyColumn(
    rules: List<Rule>,
    clickEvent: (RuleClickEvent) -> Unit,
    onUpdateRule: (rule: Rule, score: Long) -> Unit = { _, _ -> }
) {
    LazyColumn(contentPadding = PaddingValues(top = 10.dp, bottom = 12.dp)) {
        item {
            AmountSettingBlock { clickEvent(RuleClickEvent.Helper) }
        }
        itemsIndexed(
            items = rules,
            key = { _, item -> item.name }
        ) { index, rule ->
            RuleItem(index = index, rule = rule, onUpdateScore = { score ->
                onUpdateRule(rule, score)
            })
        }
    }
}

@Composable
private fun RuleItem(index: Int, rule: Rule, onUpdateScore: (Long) -> Unit = {}) {
    val isFirstDdadak = rule.name == Const.Rule.FirstDdadak

    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1.2f)
        ) {
            Text(
                text = (index + 1).toString(),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 12.dp, start = 4.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = OrangeyRed
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
                        text = rule.name.displayName,
                        fontSize = 16.sp,
                        color = if (isFirstDdadak) DustyGray else Nero
                    )
                    if (rule.isEssential) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_star_red),
                            contentDescription = "Essential Rule",
                            tint = Color.Unspecified,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
                if (rule.script.isNotEmpty()) {
                    Text(text = rule.script, fontSize = 10.sp, color = Gray38)
                }
            }
        }

        NumberTextField(
            text = rule.score.toString(),
            modifier = Modifier.weight(0.8f),
            endText = stringResource(Res.string.won),
            isEnable = !isFirstDdadak,
            hintColor = if (isFirstDdadak) DustyGray else Nero
        ) {
            onUpdateScore(it)
        }
    }
}

@Composable
private fun AmountSettingBlock(modifier: Modifier = Modifier, onHelperClick: () -> Unit) {
    Row(
        modifier = modifier.fillMaxWidth().padding(bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = stringResource(Res.string.price_setting), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Nero)
        RoundedCornerText(
            stringResource(Res.string.help),
            OrangeyRed,
            fontSize = 14.sp,
            onClick = onHelperClick
        )
    }
}
