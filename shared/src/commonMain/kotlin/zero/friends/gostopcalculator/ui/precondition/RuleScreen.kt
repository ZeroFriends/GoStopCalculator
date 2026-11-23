package zero.friends.gostopcalculator.ui.precondition

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import zero.friends.domain.model.Rule
import zero.friends.domain.util.Const
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.complete
import zero.friends.gostopcalculator.shared.generated.resources.confirm
import zero.friends.gostopcalculator.shared.generated.resources.game_setting_title
import zero.friends.gostopcalculator.shared.generated.resources.help
import zero.friends.gostopcalculator.shared.generated.resources.help_ddaddak
import zero.friends.gostopcalculator.shared.generated.resources.help_ddaddak_sub
import zero.friends.gostopcalculator.shared.generated.resources.help_fuck
import zero.friends.gostopcalculator.shared.generated.resources.help_score
import zero.friends.gostopcalculator.shared.generated.resources.help_selling
import zero.friends.gostopcalculator.shared.generated.resources.price_setting
import zero.friends.gostopcalculator.shared.generated.resources.rule_subtitle
import zero.friends.gostopcalculator.shared.generated.resources.rule_title
import zero.friends.gostopcalculator.shared.generated.resources.won
import zero.friends.gostopcalculator.theme.Gray
import zero.friends.gostopcalculator.theme.Nero
import zero.friends.gostopcalculator.theme.OrangeyRed
import zero.friends.gostopcalculator.ui.board.score.common.PlayerIndexBadge
import zero.friends.gostopcalculator.ui.board.score.common.ScoreDisabledBadgeBg
import zero.friends.gostopcalculator.ui.board.score.common.ScorePrimaryRed
import zero.friends.gostopcalculator.ui.board.score.common.ScoreRuleCardBg
import zero.friends.gostopcalculator.ui.board.score.common.ScoreRuleCardBorder
import zero.friends.gostopcalculator.ui.board.score.common.ScoreSoftPinkBg
import zero.friends.gostopcalculator.ui.board.score.common.ScoreSubtleGray
import zero.friends.gostopcalculator.ui.board.score.common.ScoreTextDark
import zero.friends.gostopcalculator.ui.board.score.common.StepperNumberField
import zero.friends.gostopcalculator.ui.common.BackHandler
import zero.friends.gostopcalculator.ui.common.CenterTextTopBar
import zero.friends.gostopcalculator.ui.common.RoundedCornerText
import zero.friends.gostopcalculator.ui.common.background.AprilBackground
import zero.friends.gostopcalculator.util.TabKeyboardDownModifier
import zero.friends.gostopcalculator.util.localizedName
import zero.friends.gostopcalculator.util.localizedScript

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

    LaunchedEffect(Unit) {
        ruleViewModel.toast().collect { message ->
            if (message != null) {
                scaffoldState.snackbarHostState.showSnackbar(message)
            }
        }
    }

    val uiState by ruleViewModel.getUiState().collectAsState()
    val scope = rememberCoroutineScope()

    var openDialog by remember {
        mutableStateOf(false)
    }
    var isSubmitting by remember {
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
                    if (!isSubmitting) {
                        isSubmitting = true
                        scope.launch {
                            try {
                                val gameId = ruleViewModel.startGame()
                                onNext(gameId)
                            } catch (e: Throwable) {
                                isSubmitting = false
                                throw e
                            }
                        }
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
                    text = stringResource(Res.string.confirm),
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
                text = stringResource(Res.string.game_setting_title, 2),
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
    LazyColumn(
        contentPadding = PaddingValues(top = 6.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
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
    val currentScore = rule.score.toLong()
    val isHighlighted = rule.isEssential && rule.score > 0
    val localizedScript = rule.localizedScript()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = ScoreRuleCardBg,
        border = BorderStroke(1.dp, ScoreRuleCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = if (localizedScript.isNotEmpty()) Alignment.Top else Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                PlayerIndexBadge(
                    index = index,
                    containerColor = if (isFirstDdadak) ScoreDisabledBadgeBg else ScoreSoftPinkBg,
                    textColor = if (isFirstDdadak) ScoreSubtleGray else ScorePrimaryRed,
                    size = 24.dp,
                    shape = RoundedCornerShape(8.dp)
                )
                Column(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = rule.name.localizedName(),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isFirstDdadak) ScoreSubtleGray else ScoreTextDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (rule.isEssential) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(ScorePrimaryRed)
                            )
                        }
                    }
                    if (localizedScript.isNotEmpty()) {
                        Text(
                            text = localizedScript,
                            fontSize = 11.sp,
                            color = ScoreSubtleGray,
                            lineHeight = 14.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            StepperNumberField(
                value = currentScore,
                endText = stringResource(Res.string.won),
                enabled = !isFirstDdadak,
                isHighlighted = isHighlighted,
                filledPlusWhenHighlighted = false,
                outlinedNeutralButtons = true,
                centerWidth = 80.dp,
                centerHeight = 36.dp,
                buttonSize = 28.dp,
                spacing = 5.dp,
                inputTestTag = "rule_score_input_${index}",
                onDecrement = { onUpdateScore(nextRuleStepDown(currentScore)) },
                onIncrement = { onUpdateScore(nextRuleStepUp(currentScore)) },
                onValueChange = { onUpdateScore(it) }
            )
        }
    }
}

@Composable
private fun AmountSettingBlock(modifier: Modifier = Modifier, onHelperClick: () -> Unit) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(Res.string.price_setting),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = ScoreTextDark
        )
        RoundedCornerText(
            stringResource(Res.string.help),
            OrangeyRed,
            fontSize = 13.sp,
            onClick = onHelperClick
        )
    }
}
