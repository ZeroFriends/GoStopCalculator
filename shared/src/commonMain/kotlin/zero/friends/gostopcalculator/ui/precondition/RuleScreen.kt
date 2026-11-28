package zero.friends.gostopcalculator.ui.precondition

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import zero.friends.domain.model.Rule
import zero.friends.domain.util.Const
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.close
import zero.friends.gostopcalculator.shared.generated.resources.complete
import zero.friends.gostopcalculator.shared.generated.resources.confirm
import zero.friends.gostopcalculator.shared.generated.resources.game_setting_title
import zero.friends.gostopcalculator.shared.generated.resources.help
import zero.friends.gostopcalculator.shared.generated.resources.ic_help_outline
import zero.friends.gostopcalculator.shared.generated.resources.ic_menu_book
import zero.friends.gostopcalculator.shared.generated.resources.ic_topbar_close
import zero.friends.gostopcalculator.shared.generated.resources.price_setting
import zero.friends.gostopcalculator.shared.generated.resources.rule_first_ddadak
import zero.friends.gostopcalculator.shared.generated.resources.rule_fuck
import zero.friends.gostopcalculator.shared.generated.resources.rule_help_badge_4p_only
import zero.friends.gostopcalculator.shared.generated.resources.rule_help_badge_auto_x3
import zero.friends.gostopcalculator.shared.generated.resources.rule_help_badge_instant
import zero.friends.gostopcalculator.shared.generated.resources.rule_help_badge_required
import zero.friends.gostopcalculator.shared.generated.resources.rule_help_ddadak_desc
import zero.friends.gostopcalculator.shared.generated.resources.rule_help_ddadak_note
import zero.friends.gostopcalculator.shared.generated.resources.rule_help_fuck_chip_1
import zero.friends.gostopcalculator.shared.generated.resources.rule_help_fuck_chip_2
import zero.friends.gostopcalculator.shared.generated.resources.rule_help_fuck_chip_3
import zero.friends.gostopcalculator.shared.generated.resources.rule_help_fuck_desc
import zero.friends.gostopcalculator.shared.generated.resources.rule_help_manual_shortcut
import zero.friends.gostopcalculator.shared.generated.resources.rule_help_score_desc
import zero.friends.gostopcalculator.shared.generated.resources.rule_help_score_example_default_format
import zero.friends.gostopcalculator.shared.generated.resources.rule_help_score_example_live_format
import zero.friends.gostopcalculator.shared.generated.resources.rule_help_selling_desc
import zero.friends.gostopcalculator.shared.generated.resources.rule_help_subtitle
import zero.friends.gostopcalculator.shared.generated.resources.rule_help_title
import zero.friends.gostopcalculator.shared.generated.resources.rule_score
import zero.friends.gostopcalculator.shared.generated.resources.rule_sell
import zero.friends.gostopcalculator.shared.generated.resources.rule_subtitle
import zero.friends.gostopcalculator.shared.generated.resources.rule_title
import zero.friends.gostopcalculator.shared.generated.resources.won
import zero.friends.gostopcalculator.theme.OrangeyRed
import zero.friends.gostopcalculator.ui.board.score.common.PlayerIndexBadge
import zero.friends.gostopcalculator.ui.board.score.common.ScoreBorderNeutral
import zero.friends.gostopcalculator.ui.board.score.common.ScoreCardBorder
import zero.friends.gostopcalculator.ui.board.score.common.ScoreDisabledBadgeBg
import zero.friends.gostopcalculator.ui.board.score.common.ScoreMintBg
import zero.friends.gostopcalculator.ui.board.score.common.ScoreMintText
import zero.friends.gostopcalculator.ui.board.score.common.ScoreMutedText
import zero.friends.gostopcalculator.ui.board.score.common.ScoreNeutralBtnBg
import zero.friends.gostopcalculator.ui.board.score.common.ScorePrimaryRed
import zero.friends.gostopcalculator.ui.board.score.common.ScoreRuleCardBg
import zero.friends.gostopcalculator.ui.board.score.common.ScoreRuleCardBorder
import zero.friends.gostopcalculator.ui.board.score.common.ScoreSoftPinkBg
import zero.friends.gostopcalculator.ui.board.score.common.ScoreSoftPinkBorder
import zero.friends.gostopcalculator.ui.board.score.common.ScoreSubtleGray
import zero.friends.gostopcalculator.ui.board.score.common.ScoreSummaryCardBg
import zero.friends.gostopcalculator.ui.board.score.common.ScoreTextDark
import zero.friends.gostopcalculator.ui.board.score.common.StatusPillBadge
import zero.friends.gostopcalculator.ui.board.score.common.StepperNumberField
import zero.friends.gostopcalculator.ui.common.BackHandler
import zero.friends.gostopcalculator.ui.common.CenterTextTopBar
import zero.friends.gostopcalculator.ui.common.GoStopButton
import zero.friends.gostopcalculator.ui.common.RoundedCornerText
import zero.friends.gostopcalculator.ui.common.background.AprilBackground
import zero.friends.gostopcalculator.ui.common.enableTestTagsAsResourceId
import zero.friends.gostopcalculator.util.TabKeyboardDownModifier
import zero.friends.gostopcalculator.util.localizedName
import zero.friends.gostopcalculator.util.localizedScript
import zero.friends.gostopcalculator.util.separateComma

private val HelpBlueBg = Color(0xFFDBEAFE)
private val HelpBlueText = Color(0xFF1D4ED8)

internal data class RuleHelpScoreExample(
    val samplePoints: Int = 5,
    val unitAmount: Long,
    val totalAmount: Long,
    val isDefaultFallback: Boolean,
)

internal fun buildRuleHelpScoreExample(scorePerPoint: Int): RuleHelpScoreExample {
    val isDefault = scorePerPoint <= 0
    val unit = if (isDefault) 100L else scorePerPoint.toLong()
    val points = 5
    return RuleHelpScoreExample(
        samplePoints = points,
        unitAmount = unit,
        totalAmount = unit * points,
        isDefaultFallback = isDefault,
    )
}

sealed class RuleClickEvent {
    object Back : RuleClickEvent()
    object Complete : RuleClickEvent()
    object Helper : RuleClickEvent()
}

@Composable
fun RuleScreen(
    ruleViewModel: RuleViewModel,
    onNext: (gameId: Long) -> Unit,
    onBack: () -> Unit,
    onOpenManual: () -> Unit = {},
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

    var openDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var isSubmitting by remember {
        mutableStateOf(false)
    }
    val scorePerPoint = uiState.rules.firstOrNull { it.name == Const.Rule.Score }?.score ?: 0

    if (openDialog) {
        RuleHelpDialog(
            scorePerPoint = scorePerPoint,
            onOpenManual = {
                openDialog = false
                onOpenManual()
            },
            onClose = { openDialog = false },
        )
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
    scorePerPoint: Int = 0,
    onOpenManual: () -> Unit = {},
    onClose: () -> Unit = {},
    scrollState: ScrollState = rememberScrollState()
) {
    val example = remember(scorePerPoint) { buildRuleHelpScoreExample(scorePerPoint) }
    val exampleText = if (example.isDefaultFallback) {
        stringResource(
            Res.string.rule_help_score_example_default_format,
            example.samplePoints,
            example.unitAmount.separateComma(),
            example.totalAmount.separateComma(),
        )
    } else {
        stringResource(
            Res.string.rule_help_score_example_live_format,
            example.samplePoints,
            example.unitAmount.separateComma(),
            example.totalAmount.separateComma(),
        )
    }

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .widthIn(max = 390.dp)
                .heightIn(max = 680.dp)
                .enableTestTagsAsResourceId()
                .testTag("rule_help_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            elevation = 8.dp,
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(scrollState)
                        .padding(horizontal = 20.dp, vertical = 18.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(44.dp)
                                .clip(CircleShape)
                                .clickable(role = Role.Button, onClick = onClose),
                            contentAlignment = Alignment.Center,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(ScoreNeutralBtnBg),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_topbar_close),
                                    contentDescription = stringResource(Res.string.close),
                                    tint = ScoreMutedText,
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(ScoreSoftPinkBg),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_help_outline),
                                    contentDescription = null,
                                    tint = ScorePrimaryRed,
                                    modifier = Modifier.size(24.dp),
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = stringResource(Res.string.rule_help_title),
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = ScoreTextDark,
                                textAlign = TextAlign.Center,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(Res.string.rule_help_subtitle),
                                fontSize = 12.5.sp,
                                color = ScoreSubtleGray,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        RuleHelpCard(
                            index = 0,
                            indexBg = ScoreSoftPinkBg,
                            indexTextColor = ScorePrimaryRed,
                            title = stringResource(Res.string.rule_score),
                            badgeText = stringResource(Res.string.rule_help_badge_required),
                            badgeBg = ScorePrimaryRed,
                            badgeTextColor = Color.White,
                            description = stringResource(Res.string.rule_help_score_desc),
                        ) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, ScoreBorderNeutral),
                            ) {
                                Text(
                                    text = exampleText,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (example.isDefaultFallback) ScoreMutedText else ScorePrimaryRed,
                                    modifier = Modifier
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                        .testTag("rule_help_score_example"),
                                )
                            }
                        }

                        RuleHelpCard(
                            index = 1,
                            indexBg = ScoreSoftPinkBg,
                            indexTextColor = ScorePrimaryRed,
                            title = stringResource(Res.string.rule_fuck),
                            badgeText = stringResource(Res.string.rule_help_badge_instant),
                            badgeBg = ScoreSoftPinkBg,
                            badgeTextColor = ScorePrimaryRed,
                            description = stringResource(Res.string.rule_help_fuck_desc),
                        ) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(IntrinsicSize.Max),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                HelpMultiplierChip(
                                    text = stringResource(Res.string.rule_help_fuck_chip_1),
                                    highlighted = false,
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight(),
                                )
                                HelpMultiplierChip(
                                    text = stringResource(Res.string.rule_help_fuck_chip_2),
                                    highlighted = false,
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight(),
                                )
                                HelpMultiplierChip(
                                    text = stringResource(Res.string.rule_help_fuck_chip_3),
                                    highlighted = true,
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight(),
                                )
                            }
                        }

                        RuleHelpCard(
                            index = 2,
                            indexBg = ScoreDisabledBadgeBg,
                            indexTextColor = ScoreSubtleGray,
                            title = stringResource(Res.string.rule_first_ddadak),
                            badgeText = stringResource(Res.string.rule_help_badge_auto_x3),
                            badgeBg = HelpBlueBg,
                            badgeTextColor = HelpBlueText,
                            description = stringResource(Res.string.rule_help_ddadak_desc),
                        ) {
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                text = stringResource(Res.string.rule_help_ddadak_note),
                                fontSize = 11.sp,
                                color = ScoreSubtleGray,
                                lineHeight = 15.sp,
                            )
                        }

                        RuleHelpCard(
                            index = 3,
                            indexBg = ScoreSoftPinkBg,
                            indexTextColor = ScorePrimaryRed,
                            title = stringResource(Res.string.rule_sell),
                            badgeText = stringResource(Res.string.rule_help_badge_4p_only),
                            badgeBg = ScoreMintBg,
                            badgeTextColor = ScoreMintText,
                            description = stringResource(Res.string.rule_help_selling_desc),
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 18.dp),
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .testTag("rule_help_manual_shortcut")
                            .clickable(role = Role.Button, onClick = onOpenManual),
                        shape = RoundedCornerShape(12.dp),
                        color = ScoreSoftPinkBg,
                        border = BorderStroke(1.dp, ScoreSoftPinkBorder),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_menu_book),
                                contentDescription = null,
                                tint = ScorePrimaryRed,
                                modifier = Modifier.size(15.dp),
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(Res.string.rule_help_manual_shortcut),
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = ScorePrimaryRed,
                                textAlign = TextAlign.Center,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false),
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    GoStopButton(
                        text = stringResource(Res.string.confirm),
                        onClick = onClose,
                    )
                }
            }
        }
    }
}

@Composable
private fun RuleHelpCard(
    index: Int,
    indexBg: Color,
    indexTextColor: Color,
    title: String,
    badgeText: String,
    badgeBg: Color,
    badgeTextColor: Color,
    description: String,
    extraContent: @Composable () -> Unit = {},
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = ScoreSummaryCardBg,
        border = BorderStroke(1.dp, ScoreCardBorder),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    PlayerIndexBadge(
                        index = index,
                        containerColor = indexBg,
                        textColor = indexTextColor,
                        size = 22.dp,
                        shape = RoundedCornerShape(7.dp),
                    )
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ScoreTextDark,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                StatusPillBadge(
                    text = badgeText,
                    backgroundColor = badgeBg,
                    textColor = badgeTextColor,
                    fontSize = 10.5.sp,
                    horizontalPadding = 8.dp,
                    verticalPadding = 3.dp,
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = ScoreMutedText,
            )
            extraContent()
        }
    }
}

@Composable
private fun HelpMultiplierChip(
    text: String,
    highlighted: Boolean,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = if (highlighted) ScoreSoftPinkBg else Color.White,
        border = BorderStroke(1.dp, if (highlighted) ScoreSoftPinkBorder else ScoreBorderNeutral),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                fontSize = 10.5.sp,
                fontWeight = if (highlighted) FontWeight.Bold else FontWeight.SemiBold,
                color = if (highlighted) ScorePrimaryRed else ScoreTextDark,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 13.sp,
            )
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
