package zero.friends.gostopcalculator.ui.precondition

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
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
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import zero.friends.domain.model.Rule
import zero.friends.gostopcalculator.R
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
fun RuleScreen(ruleViewModel: RuleViewModel = hiltViewModel(), onNext: (gameId: Long) -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    val uiState by ruleViewModel.getUiState().collectAsState()
    val scope = rememberCoroutineScope()

    var openDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        ruleViewModel.toast()
            .onEach {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }.launchIn(this)
    }

    BackHandler(true) {
        onBack()
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
            ruleViewModel.checkButtonState()
        })


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
                .background(colorResource(id = R.color.white))

        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(id = R.string.help), fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 18.dp)
                    .scrollable(scrollState, orientation = Orientation.Vertical)
            ) {
                Spacer(modifier = Modifier.padding(10.dp))
                Text(text = stringResource(id = R.string.help_score), fontSize = 14.sp)
                Spacer(modifier = Modifier.padding(3.dp))
                Text(text = stringResource(id = R.string.help_fuck), fontSize = 14.sp)
                Spacer(modifier = Modifier.padding(3.dp))
                Text(text = stringResource(id = R.string.help_ddaddak), fontSize = 14.sp)
                Text(
                    text = stringResource(id = R.string.help_ddaddak_sub), fontSize = 10.sp, color = colorResource(
                        id = R.color.gray
                    )
                )
                Spacer(modifier = Modifier.padding(3.dp))
                Text(text = stringResource(id = R.string.help_selling), fontSize = 14.sp)
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                onClick = onClose
            ) {
                Text(
                    text = stringResource(id = android.R.string.ok),
                    color = colorResource(id = R.color.white),
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
                text = String.format(stringResource(id = R.string.game_setting_title), 2),
                onBack = { clickEvent(RuleClickEvent.Back) },
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
private fun RuleLazyColumn(
    rules: List<Rule>,
    clickEvent: (RuleClickEvent) -> Unit,
    onUpdateRule: (rule: Rule, score: Long) -> Unit = { _, _ -> }
) {
    LazyColumn(contentPadding = PaddingValues(top = 35.dp, bottom = 12.dp)) {
        item {
            AmountSettingBlock { clickEvent(RuleClickEvent.Helper) }
        }
        itemsIndexed(
            items = rules,
            key = { _, item -> item.name }
        ) { index, rule ->
            RuleItem(index = index, rule) {
                onUpdateRule(rule, it)
            }
        }

    }
}

@Composable
private fun RuleItem(index: Int, rule: Rule, onUpdateScore: (Long) -> Unit = {}) {
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

        NumberTextField(
            text = rule.score.toString(),
            modifier = Modifier.weight(1f),
            endText = stringResource(R.string.won)
        ) {
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
            fontSize = 14.sp,
            onClick = onHelperClick
        )
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