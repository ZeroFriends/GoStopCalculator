package zero.friends.gostopcalculator.ui.board.score.manual

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import zero.friends.gostopcalculator.navigation.LocalNativeTopBarHeight
import zero.friends.gostopcalculator.navigation.LocalUseNativeNavigation
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.close
import zero.friends.gostopcalculator.shared.generated.resources.confirm
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_credit
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_credit_tap_hint
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_license_author_label
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_license_author_value
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_license_dialog_title
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_license_mod_label
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_license_mod_value
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_license_source_label
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_license_source_url
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_license_type_label
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_license_type_value
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_license_url
import zero.friends.gostopcalculator.shared.generated.resources.ic_topbar_close
import zero.friends.gostopcalculator.shared.generated.resources.manual
import zero.friends.gostopcalculator.shared.generated.resources.manual_bak_banner
import zero.friends.gostopcalculator.shared.generated.resources.manual_bak_sec_title
import zero.friends.gostopcalculator.shared.generated.resources.manual_bak_x2_badge
import zero.friends.gostopcalculator.shared.generated.resources.manual_filter_all
import zero.friends.gostopcalculator.shared.generated.resources.manual_filter_selling
import zero.friends.gostopcalculator.shared.generated.resources.manual_first_ddadak_badge
import zero.friends.gostopcalculator.shared.generated.resources.manual_first_ddadak_desc
import zero.friends.gostopcalculator.shared.generated.resources.manual_first_ddadak_title
import zero.friends.gostopcalculator.shared.generated.resources.manual_go_badge
import zero.friends.gostopcalculator.shared.generated.resources.manual_go_counter_note
import zero.friends.gostopcalculator.shared.generated.resources.manual_go_desc
import zero.friends.gostopcalculator.shared.generated.resources.manual_go_sec_special
import zero.friends.gostopcalculator.shared.generated.resources.manual_go_sec_winner
import zero.friends.gostopcalculator.shared.generated.resources.manual_go_stack_tip
import zero.friends.gostopcalculator.shared.generated.resources.manual_go_title
import zero.friends.gostopcalculator.shared.generated.resources.manual_gobak_2p_desc
import zero.friends.gostopcalculator.shared.generated.resources.manual_gobak_2p_label
import zero.friends.gostopcalculator.shared.generated.resources.manual_gobak_3p_desc
import zero.friends.gostopcalculator.shared.generated.resources.manual_gobak_3p_label
import zero.friends.gostopcalculator.shared.generated.resources.manual_gobak_badge
import zero.friends.gostopcalculator.shared.generated.resources.manual_gobak_desc
import zero.friends.gostopcalculator.shared.generated.resources.manual_gobak_note
import zero.friends.gostopcalculator.shared.generated.resources.manual_gobak_title
import zero.friends.gostopcalculator.shared.generated.resources.manual_gwangbak_desc
import zero.friends.gostopcalculator.shared.generated.resources.manual_gwangbak_note
import zero.friends.gostopcalculator.shared.generated.resources.manual_meongbak_desc
import zero.friends.gostopcalculator.shared.generated.resources.manual_meongbak_note
import zero.friends.gostopcalculator.shared.generated.resources.manual_meongdda_badge
import zero.friends.gostopcalculator.shared.generated.resources.manual_meongdda_desc
import zero.friends.gostopcalculator.shared.generated.resources.manual_meongdda_title
import zero.friends.gostopcalculator.shared.generated.resources.manual_month_card_count
import zero.friends.gostopcalculator.shared.generated.resources.manual_nagari_badge
import zero.friends.gostopcalculator.shared.generated.resources.manual_nagari_desc
import zero.friends.gostopcalculator.shared.generated.resources.manual_nagari_title
import zero.friends.gostopcalculator.shared.generated.resources.manual_pibak_desc
import zero.friends.gostopcalculator.shared.generated.resources.manual_pibak_note
import zero.friends.gostopcalculator.shared.generated.resources.manual_ppeok_1_desc
import zero.friends.gostopcalculator.shared.generated.resources.manual_ppeok_1_label
import zero.friends.gostopcalculator.shared.generated.resources.manual_ppeok_2_desc
import zero.friends.gostopcalculator.shared.generated.resources.manual_ppeok_2_label
import zero.friends.gostopcalculator.shared.generated.resources.manual_ppeok_3_desc
import zero.friends.gostopcalculator.shared.generated.resources.manual_ppeok_3_label
import zero.friends.gostopcalculator.shared.generated.resources.manual_ppeok_badge
import zero.friends.gostopcalculator.shared.generated.resources.manual_ppeok_title
import zero.friends.gostopcalculator.shared.generated.resources.manual_score_sec_gwang
import zero.friends.gostopcalculator.shared.generated.resources.manual_score_sec_others
import zero.friends.gostopcalculator.shared.generated.resources.manual_selling_banner
import zero.friends.gostopcalculator.shared.generated.resources.manual_setup_2p_floor
import zero.friends.gostopcalculator.shared.generated.resources.manual_setup_2p_hand
import zero.friends.gostopcalculator.shared.generated.resources.manual_setup_2p_target
import zero.friends.gostopcalculator.shared.generated.resources.manual_setup_2p_title
import zero.friends.gostopcalculator.shared.generated.resources.manual_setup_3p_floor
import zero.friends.gostopcalculator.shared.generated.resources.manual_setup_3p_hand
import zero.friends.gostopcalculator.shared.generated.resources.manual_setup_3p_target
import zero.friends.gostopcalculator.shared.generated.resources.manual_setup_3p_title
import zero.friends.gostopcalculator.shared.generated.resources.manual_setup_4p_note
import zero.friends.gostopcalculator.shared.generated.resources.manual_setup_4p_title
import zero.friends.gostopcalculator.shared.generated.resources.manual_setup_badge
import zero.friends.gostopcalculator.shared.generated.resources.manual_setup_deck_tip
import zero.friends.gostopcalculator.shared.generated.resources.manual_setup_label_floor
import zero.friends.gostopcalculator.shared.generated.resources.manual_setup_label_hand
import zero.friends.gostopcalculator.shared.generated.resources.manual_setup_label_target
import zero.friends.gostopcalculator.shared.generated.resources.manual_setup_selling_shortcut
import zero.friends.gostopcalculator.shared.generated.resources.manual_setup_title
import zero.friends.gostopcalculator.shared.generated.resources.manual_shake_bomb_badge
import zero.friends.gostopcalculator.shared.generated.resources.manual_shake_bomb_desc
import zero.friends.gostopcalculator.shared.generated.resources.manual_shake_bomb_title
import zero.friends.gostopcalculator.shared.generated.resources.manual_step_1_desc
import zero.friends.gostopcalculator.shared.generated.resources.manual_step_1_title
import zero.friends.gostopcalculator.shared.generated.resources.manual_step_2_desc
import zero.friends.gostopcalculator.shared.generated.resources.manual_step_2_title
import zero.friends.gostopcalculator.shared.generated.resources.manual_step_3_desc
import zero.friends.gostopcalculator.shared.generated.resources.manual_step_3_title
import zero.friends.gostopcalculator.shared.generated.resources.manual_step_4_desc
import zero.friends.gostopcalculator.shared.generated.resources.manual_step_4_title
import zero.friends.gostopcalculator.shared.generated.resources.manual_steps_title
import zero.friends.gostopcalculator.shared.generated.resources.manual_subtitle
import zero.friends.gostopcalculator.shared.generated.resources.manual_term_ddadak
import zero.friends.gostopcalculator.shared.generated.resources.manual_term_ddadak_desc
import zero.friends.gostopcalculator.shared.generated.resources.manual_term_jjok
import zero.friends.gostopcalculator.shared.generated.resources.manual_term_jjok_desc
import zero.friends.gostopcalculator.shared.generated.resources.manual_term_ppeok
import zero.friends.gostopcalculator.shared.generated.resources.manual_term_ppeok_desc
import zero.friends.gostopcalculator.shared.generated.resources.manual_term_ppeok_tag
import zero.friends.gostopcalculator.shared.generated.resources.manual_term_sseul
import zero.friends.gostopcalculator.shared.generated.resources.manual_term_sseul_desc
import zero.friends.gostopcalculator.shared.generated.resources.manual_term_steal_tag
import zero.friends.gostopcalculator.shared.generated.resources.manual_terms_badge
import zero.friends.gostopcalculator.shared.generated.resources.manual_terms_tip
import zero.friends.gostopcalculator.shared.generated.resources.manual_terms_title
import zero.friends.gostopcalculator.shared.generated.resources.manual_title
import zero.friends.gostopcalculator.shared.generated.resources.rule_light_bak
import zero.friends.gostopcalculator.shared.generated.resources.rule_mong_bak
import zero.friends.gostopcalculator.shared.generated.resources.rule_pea_bak
import zero.friends.gostopcalculator.shared.generated.resources.selling_badge_gwang
import zero.friends.gostopcalculator.shared.generated.resources.selling_badge_ssangpi
import zero.friends.gostopcalculator.shared.generated.resources.selling_disclaimer
import zero.friends.gostopcalculator.shared.generated.resources.selling_section_gwang
import zero.friends.gostopcalculator.shared.generated.resources.selling_section_ssangpi
import zero.friends.gostopcalculator.ui.board.score.common.ScoreBorderNeutral
import zero.friends.gostopcalculator.ui.board.score.common.ScoreCanvasBg
import zero.friends.gostopcalculator.ui.board.score.common.ScoreCardBorder
import zero.friends.gostopcalculator.ui.board.score.common.ScoreDarkBadge
import zero.friends.gostopcalculator.ui.board.score.common.ScoreMintBg
import zero.friends.gostopcalculator.ui.board.score.common.ScoreMintText
import zero.friends.gostopcalculator.ui.board.score.common.ScoreMutedText
import zero.friends.gostopcalculator.ui.board.score.common.ScoreNeutralBtnBg
import zero.friends.gostopcalculator.ui.board.score.common.ScorePrimaryRed
import zero.friends.gostopcalculator.ui.board.score.common.ScoreSoftPinkBg
import zero.friends.gostopcalculator.ui.board.score.common.ScoreSoftPinkBorder
import zero.friends.gostopcalculator.ui.board.score.common.ScoreSubtleGray
import zero.friends.gostopcalculator.ui.board.score.common.ScoreSummaryCardBg
import zero.friends.gostopcalculator.ui.board.score.common.ScoreTextDark
import zero.friends.gostopcalculator.ui.board.score.common.StatusPillBadge
import zero.friends.gostopcalculator.ui.common.BackHandler
import zero.friends.gostopcalculator.ui.common.CenterTextTopBar
import zero.friends.gostopcalculator.ui.common.GoStopButton
import zero.friends.gostopcalculator.ui.common.GoStopDivider
import zero.friends.gostopcalculator.ui.common.enableTestTagsAsResourceId
import zero.friends.gostopcalculator.ui.dialog.FullScreenDialog

private val AmberBg = Color(0xFFFFFBEB)
private val AmberText = Color(0xFFB45309)
private val BlueBg = Color(0xFFDBEAFE)
private val BlueText = Color(0xFF1D4ED8)
private val GreenBg = Color(0xFFDCFCE7)
private val GreenText = Color(0xFF15803D)

@Composable
fun ManualFullScreenDialog(
    initialTab: Int = 0,
    onDismiss: () -> Unit = {}
) {
    FullScreenDialog(onDismiss = onDismiss) {
        ManualScreen(
            initialTab = initialTab,
            onBack = onDismiss,
        )
    }
}

@Composable
fun ManualScreen(
    initialTab: Int = 0,
    onBack: () -> Unit = {}
) {
    BackHandler(enabled = true) {
        onBack()
    }

    val safeInitialTab = ManualTab.fromIndex(initialTab).index
    val pagerState = rememberPagerState(
        initialPage = safeInitialTab,
        pageCount = { ManualTab.entries.size },
    )
    var showSellingOnly by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val topBarPadding = if (LocalUseNativeNavigation.current) LocalNativeTopBarHeight.current else 0.dp

    Scaffold(
        scaffoldState = rememberScaffoldState(),
        topBar = {
            CenterTextTopBar(
                text = stringResource(Res.string.manual_title),
                onBack = onBack,
                isRed = false
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .padding(top = topBarPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = stringResource(Res.string.manual),
                    fontSize = 22.sp,
                    color = ScoreTextDark,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = stringResource(Res.string.manual_subtitle),
                    fontSize = 12.5.sp,
                    color = ScoreMutedText
                )
                Spacer(modifier = Modifier.height(12.dp))
                ManualTabBar(
                    selectedIndex = pagerState.currentPage,
                    onSelectTab = { index ->
                        scope.launch { pagerState.animateScrollToPage(index) }
                    }
                )
            }
            GoStopDivider()
            HorizontalPager(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(color = ScoreCanvasBg),
                state = pagerState,
                beyondViewportPageCount = 1,
            ) { page ->
                when (ManualTab.fromIndex(page)) {
                    ManualTab.GameFlow -> GameFlowTab(
                        onOpenSellingCards = {
                            showSellingOnly = true
                            scope.launch { pagerState.animateScrollToPage(ManualTab.MonthlyCards.index) }
                        }
                    )
                    ManualTab.CardScores -> CardScoresTab()
                    ManualTab.GoAndBonus -> GoAndBonusTab()
                    ManualTab.Penalties -> PenaltiesTab()
                    ManualTab.MonthlyCards -> MonthlyCardsTab(
                        showSellingOnly = showSellingOnly,
                        onToggleFilter = { showSellingOnly = it }
                    )
                }
            }
        }
    }
}

@Composable
private fun ManualTabBar(
    selectedIndex: Int,
    onSelectTab: (Int) -> Unit,
) {
    val listState = rememberLazyListState()
    LaunchedEffect(selectedIndex) {
        listState.animateScrollToItem(selectedIndex)
    }
    LazyRow(
        state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items(
            items = ManualTab.entries,
            key = { it.index },
        ) { tab ->
            val selected = selectedIndex == tab.index
            Surface(
                modifier = Modifier
                    .defaultMinSize(minHeight = 40.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .testTag("manual_tab_${tab.index}")
                    .selectable(
                        selected = selected,
                        role = Role.Tab,
                        onClick = { onSelectTab(tab.index) },
                    ),
                shape = RoundedCornerShape(100.dp),
                color = if (selected) ScorePrimaryRed else ScoreNeutralBtnBg,
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(tab.labelRes),
                        fontSize = 12.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                        color = if (selected) Color.White else ScoreMutedText,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun ManualPageList(
    modifier: Modifier = Modifier.fillMaxSize(),
    topPadding: Dp = 14.dp,
    content: androidx.compose.foundation.lazy.LazyListScope.() -> Unit,
) {
    val navBarBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = topPadding,
            bottom = 20.dp + navBarBottom,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = content,
    )
}

@Composable
private fun ManualCard(
    modifier: Modifier = Modifier,
    borderColor: Color = ScoreCardBorder,
    borderWidth: Dp = 1.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(borderWidth, borderColor),
        elevation = 1.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            content = content,
        )
    }
}

@Composable
private fun ManualSectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = ScoreMutedText,
        modifier = Modifier.padding(horizontal = 2.dp, vertical = 2.dp)
    )
}

@Composable
private fun ManualInfoBanner(text: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = ScoreSoftPinkBg,
        border = BorderStroke(1.dp, ScoreSoftPinkBorder),
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.Medium,
            color = ScoreTextDark,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
        )
    }
}

@Composable
private fun GameFlowTab(onOpenSellingCards: () -> Unit) {
    ManualPageList {
        item(key = "setup_card", contentType = "manual_card") {
            ManualCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(Res.string.manual_setup_title),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ScoreTextDark,
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    StatusPillBadge(
                        text = stringResource(Res.string.manual_setup_badge),
                        backgroundColor = ScoreNeutralBtnBg,
                        textColor = ScoreMutedText,
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    SetupColumn(
                        title = stringResource(Res.string.manual_setup_2p_title),
                        target = stringResource(Res.string.manual_setup_2p_target),
                        floor = stringResource(Res.string.manual_setup_2p_floor),
                        hand = stringResource(Res.string.manual_setup_2p_hand),
                        isHighlighted = false,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                    )
                    SetupColumn(
                        title = stringResource(Res.string.manual_setup_3p_title),
                        target = stringResource(Res.string.manual_setup_3p_target),
                        floor = stringResource(Res.string.manual_setup_3p_floor),
                        hand = stringResource(Res.string.manual_setup_3p_hand),
                        isHighlighted = true,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                    )
                    SetupColumn(
                        title = stringResource(Res.string.manual_setup_4p_title),
                        target = stringResource(Res.string.manual_setup_3p_target),
                        floor = stringResource(Res.string.manual_setup_3p_floor),
                        hand = stringResource(Res.string.manual_setup_3p_hand),
                        footnote = stringResource(Res.string.manual_setup_4p_note),
                        isHighlighted = false,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = ScoreSummaryCardBg,
                ) {
                    Text(
                        text = stringResource(Res.string.manual_setup_deck_tip),
                        fontSize = 11.5.sp,
                        lineHeight = 16.sp,
                        color = ScoreMutedText,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .testTag("manual_selling_shortcut")
                        .clickable(role = Role.Button, onClick = onOpenSellingCards),
                    shape = RoundedCornerShape(10.dp),
                    color = ScoreSoftPinkBg,
                    border = BorderStroke(1.dp, ScoreSoftPinkBorder),
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(Res.string.manual_setup_selling_shortcut),
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = ScorePrimaryRed,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }

        item(key = "steps_card", contentType = "manual_card") {
            ManualCard {
                Text(
                    text = stringResource(Res.string.manual_steps_title),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ScoreTextDark,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    FlowStepRow(1, stringResource(Res.string.manual_step_1_title), stringResource(Res.string.manual_step_1_desc))
                    FlowStepRow(2, stringResource(Res.string.manual_step_2_title), stringResource(Res.string.manual_step_2_desc))
                    FlowStepRow(3, stringResource(Res.string.manual_step_3_title), stringResource(Res.string.manual_step_3_desc))
                    FlowStepRow(4, stringResource(Res.string.manual_step_4_title), stringResource(Res.string.manual_step_4_desc), emphasized = true)
                }
            }
        }

        item(key = "terms_card", contentType = "manual_card") {
            ManualCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(Res.string.manual_terms_title),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ScoreTextDark,
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    StatusPillBadge(
                        text = stringResource(Res.string.manual_terms_badge),
                        backgroundColor = ScoreNeutralBtnBg,
                        textColor = ScoreMutedText,
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    TermTile(
                        title = stringResource(Res.string.manual_term_jjok),
                        tag = stringResource(Res.string.manual_term_steal_tag),
                        desc = stringResource(Res.string.manual_term_jjok_desc),
                        isPenalty = false,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                    )
                    TermTile(
                        title = stringResource(Res.string.manual_term_ddadak),
                        tag = stringResource(Res.string.manual_term_steal_tag),
                        desc = stringResource(Res.string.manual_term_ddadak_desc),
                        isPenalty = false,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    TermTile(
                        title = stringResource(Res.string.manual_term_sseul),
                        tag = stringResource(Res.string.manual_term_steal_tag),
                        desc = stringResource(Res.string.manual_term_sseul_desc),
                        isPenalty = false,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                    )
                    TermTile(
                        title = stringResource(Res.string.manual_term_ppeok),
                        tag = stringResource(Res.string.manual_term_ppeok_tag),
                        desc = stringResource(Res.string.manual_term_ppeok_desc),
                        isPenalty = true,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(Res.string.manual_terms_tip),
                    fontSize = 11.5.sp,
                    lineHeight = 16.sp,
                    color = ScoreMutedText,
                )
            }
        }
    }
}

@Composable
private fun SetupColumn(
    title: String,
    target: String,
    floor: String,
    hand: String,
    isHighlighted: Boolean,
    modifier: Modifier = Modifier,
    footnote: String? = null,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = if (isHighlighted) ScoreSoftPinkBg else ScoreSummaryCardBg,
        border = BorderStroke(1.dp, if (isHighlighted) ScorePrimaryRed else ScoreCardBorder),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isHighlighted) ScorePrimaryRed else ScoreTextDark,
                maxLines = 2,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(8.dp))
            SetupStatItem(stringResource(Res.string.manual_setup_label_target), target, highlighted = true)
            Spacer(modifier = Modifier.height(6.dp))
            SetupStatItem(stringResource(Res.string.manual_setup_label_floor), floor)
            Spacer(modifier = Modifier.height(6.dp))
            SetupStatItem(stringResource(Res.string.manual_setup_label_hand), hand)
            if (footnote != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = footnote,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = ScorePrimaryRed,
                    textAlign = TextAlign.Center,
                    lineHeight = 13.sp,
                )
            }
        }
    }
}

@Composable
private fun SetupStatItem(label: String, value: String, highlighted: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 10.sp, color = ScoreSubtleGray)
        Text(
            text = value,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            color = if (highlighted) ScorePrimaryRed else ScoreTextDark,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun FlowStepRow(
    step: Int,
    title: String,
    desc: String,
    emphasized: Boolean = false,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = if (emphasized) ScoreSoftPinkBg else ScoreSummaryCardBg,
        border = if (emphasized) BorderStroke(1.dp, ScoreSoftPinkBorder) else null,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 1.dp)
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(ScorePrimaryRed),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = step.toString(),
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = ScoreTextDark,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = desc,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = ScoreMutedText,
                )
            }
        }
    }
}

@Composable
private fun TermTile(
    title: String,
    tag: String,
    desc: String,
    isPenalty: Boolean,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = ScoreSummaryCardBg,
        border = BorderStroke(1.dp, ScoreCardBorder),
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = ScoreTextDark,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.width(4.dp))
                StatusPillBadge(
                    text = tag,
                    backgroundColor = if (isPenalty) AmberBg else ScoreSoftPinkBg,
                    textColor = if (isPenalty) AmberText else ScorePrimaryRed,
                    fontSize = 10.sp,
                    horizontalPadding = 6.dp,
                    verticalPadding = 2.dp,
                )
            }
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = desc,
                fontSize = 11.5.sp,
                lineHeight = 16.sp,
                color = ScoreMutedText,
            )
        }
    }
}

@Composable
private fun CardScoresTab() {
    ManualPageList {
        item(key = "sec_gwang", contentType = "section_title") {
            ManualSectionTitle(stringResource(Res.string.manual_score_sec_gwang))
        }
        itemsIndexed(
            items = HwatuDeck.gwangScoreComboPairs,
            key = { index, _ -> "gwang_row_$index" },
            contentType = { _, _ -> "score_combo_row" },
        ) { _, pair ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                pair.forEach { combo ->
                    ScoreComboCard(
                        spec = combo,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                    )
                }
                if (pair.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }
        item(key = "sec_others", contentType = "section_title") {
            Spacer(modifier = Modifier.height(2.dp))
            ManualSectionTitle(stringResource(Res.string.manual_score_sec_others))
        }
        itemsIndexed(
            items = HwatuDeck.otherScoreComboPairs,
            key = { index, _ -> "other_row_$index" },
            contentType = { _, _ -> "score_combo_row" },
        ) { _, pair ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                pair.forEach { combo ->
                    ScoreComboCard(
                        spec = combo,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                    )
                }
                if (pair.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }
        item(key = "pi_full", contentType = "score_combo_full") {
            ScoreComboCard(
                spec = HwatuDeck.piScoreCombo,
                modifier = Modifier.fillMaxWidth(),
                compactCards = false,
            )
        }
        item(key = "scores_credit", contentType = "credit_footer") {
            HwatuCreditFooter()
        }
    }
}

@Composable
private fun ScoreComboCard(
    spec: ScoreComboCardSpec,
    modifier: Modifier = Modifier,
    compactCards: Boolean = spec.cards.size >= 5,
) {
    ManualCard(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(ScoreSummaryCardBg)
                .padding(vertical = 8.dp, horizontal = 6.dp),
            contentAlignment = Alignment.Center,
        ) {
            val cardWidth = if (compactCards) 28.dp else 34.dp
            val cardHeight = if (compactCards) 46.dp else 56.dp
            val effectiveOverlap = if (compactCards) maxOf(spec.overlapDp, 13) else spec.overlapDp
            val step = cardWidth - effectiveOverlap.dp
            Box(
                modifier = Modifier.size(
                    width = cardWidth + step * (spec.cards.size - 1),
                    height = cardHeight,
                ),
                contentAlignment = Alignment.CenterStart,
            ) {
                spec.cards.forEachIndexed { index, drawableRes ->
                    Image(
                        painter = painterResource(drawableRes),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .offset(x = step * index)
                            .size(width = cardWidth, height = cardHeight)
                            .clip(RoundedCornerShape(4.dp))
                            .border(0.5.dp, ScoreBorderNeutral, RoundedCornerShape(4.dp))
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(spec.titleRes),
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = ScoreTextDark,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            Spacer(modifier = Modifier.width(4.dp))
            StatusPillBadge(
                text = stringResource(spec.badgeRes),
                backgroundColor = ScorePrimaryRed,
                textColor = Color.White,
                fontSize = 11.sp,
                horizontalPadding = 7.dp,
                verticalPadding = 2.dp,
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(spec.descRes),
            fontSize = 11.5.sp,
            lineHeight = 16.sp,
            color = ScoreMutedText,
        )
    }
}

@Composable
private fun GoAndBonusTab() {
    ManualPageList {
        item(key = "sec_winner_bonus", contentType = "section_title") {
            ManualSectionTitle(stringResource(Res.string.manual_go_sec_winner))
        }
        item(key = "go_card", contentType = "rule_detail_card") {
            RuleDetailCard(
                title = stringResource(Res.string.manual_go_title),
                badgeText = stringResource(Res.string.manual_go_badge),
                badgeBg = ScoreSoftPinkBg,
                badgeTextColor = ScorePrimaryRed,
                body = stringResource(Res.string.manual_go_desc),
                footerNote = stringResource(Res.string.manual_go_counter_note),
            )
        }
        item(key = "shake_bomb_card", contentType = "rule_detail_card") {
            RuleDetailCard(
                title = stringResource(Res.string.manual_shake_bomb_title),
                badgeText = stringResource(Res.string.manual_shake_bomb_badge),
                badgeBg = ScoreSoftPinkBg,
                badgeTextColor = ScorePrimaryRed,
                body = stringResource(Res.string.manual_shake_bomb_desc),
            )
        }
        item(key = "meongdda_card", contentType = "rule_detail_card") {
            RuleDetailCard(
                title = stringResource(Res.string.manual_meongdda_title),
                badgeText = stringResource(Res.string.manual_meongdda_badge),
                badgeBg = AmberBg,
                badgeTextColor = AmberText,
                body = stringResource(Res.string.manual_meongdda_desc),
            )
        }
        item(key = "sec_special", contentType = "section_title") {
            Spacer(modifier = Modifier.height(2.dp))
            ManualSectionTitle(stringResource(Res.string.manual_go_sec_special))
        }
        item(key = "ppeok_card", contentType = "manual_card") {
            ManualCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(Res.string.manual_ppeok_title),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = ScoreTextDark,
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    StatusPillBadge(
                        text = stringResource(Res.string.manual_ppeok_badge),
                        backgroundColor = ScoreNeutralBtnBg,
                        textColor = ScoreMutedText,
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    CompactSubRow(
                        label = stringResource(Res.string.manual_ppeok_1_label),
                        desc = stringResource(Res.string.manual_ppeok_1_desc),
                        highlighted = false,
                    )
                    CompactSubRow(
                        label = stringResource(Res.string.manual_ppeok_2_label),
                        desc = stringResource(Res.string.manual_ppeok_2_desc),
                        highlighted = false,
                    )
                    CompactSubRow(
                        label = stringResource(Res.string.manual_ppeok_3_label),
                        desc = stringResource(Res.string.manual_ppeok_3_desc),
                        highlighted = true,
                    )
                }
            }
        }
        item(key = "first_ddadak_card", contentType = "rule_detail_card") {
            RuleDetailCard(
                title = stringResource(Res.string.manual_first_ddadak_title),
                badgeText = stringResource(Res.string.manual_first_ddadak_badge),
                badgeBg = BlueBg,
                badgeTextColor = BlueText,
                body = stringResource(Res.string.manual_first_ddadak_desc),
            )
        }
        item(key = "nagari_card", contentType = "rule_detail_card") {
            RuleDetailCard(
                title = stringResource(Res.string.manual_nagari_title),
                badgeText = stringResource(Res.string.manual_nagari_badge),
                badgeBg = ScoreDarkBadge,
                badgeTextColor = Color.White,
                body = stringResource(Res.string.manual_nagari_desc),
            )
        }
        item(key = "stack_tip", contentType = "banner") {
            ManualInfoBanner(stringResource(Res.string.manual_go_stack_tip))
        }
    }
}

@Composable
private fun PenaltiesTab() {
    val x2Badge = stringResource(Res.string.manual_bak_x2_badge)
    ManualPageList {
        item(key = "bak_banner", contentType = "banner") {
            ManualInfoBanner(stringResource(Res.string.manual_bak_banner))
        }
        item(key = "bak_sec", contentType = "section_title") {
            ManualSectionTitle(stringResource(Res.string.manual_bak_sec_title))
        }
        item(key = "pibak", contentType = "rule_detail_card") {
            RuleDetailCard(
                title = stringResource(Res.string.rule_pea_bak),
                badgeText = x2Badge,
                badgeBg = ScoreSoftPinkBg,
                badgeTextColor = ScorePrimaryRed,
                body = stringResource(Res.string.manual_pibak_desc),
                footerNote = stringResource(Res.string.manual_pibak_note),
            )
        }
        item(key = "gwangbak", contentType = "rule_detail_card") {
            RuleDetailCard(
                title = stringResource(Res.string.rule_light_bak),
                badgeText = x2Badge,
                badgeBg = ScoreSoftPinkBg,
                badgeTextColor = ScorePrimaryRed,
                body = stringResource(Res.string.manual_gwangbak_desc),
                footerNote = stringResource(Res.string.manual_gwangbak_note),
            )
        }
        item(key = "meongbak", contentType = "rule_detail_card") {
            RuleDetailCard(
                title = stringResource(Res.string.rule_mong_bak),
                badgeText = x2Badge,
                badgeBg = ScoreSoftPinkBg,
                badgeTextColor = ScorePrimaryRed,
                body = stringResource(Res.string.manual_meongbak_desc),
                footerNote = stringResource(Res.string.manual_meongbak_note),
            )
        }
        item(key = "gobak", contentType = "manual_card") {
            ManualCard(borderColor = ScorePrimaryRed, borderWidth = 1.5.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(Res.string.manual_gobak_title),
                        fontSize = 15.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = ScoreTextDark,
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    StatusPillBadge(
                        text = stringResource(Res.string.manual_gobak_badge),
                        backgroundColor = ScorePrimaryRed,
                        textColor = Color.White,
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(Res.string.manual_gobak_desc),
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = ScoreMutedText,
                )
                Spacer(modifier = Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    CompactSubRow(
                        label = stringResource(Res.string.manual_gobak_2p_label),
                        desc = stringResource(Res.string.manual_gobak_2p_desc),
                        highlighted = true,
                    )
                    CompactSubRow(
                        label = stringResource(Res.string.manual_gobak_3p_label),
                        desc = stringResource(Res.string.manual_gobak_3p_desc),
                        highlighted = true,
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(Res.string.manual_gobak_note),
                    fontSize = 11.5.sp,
                    lineHeight = 16.sp,
                    color = ScoreMutedText,
                )
            }
        }
    }
}

@Composable
private fun RuleDetailCard(
    title: String,
    badgeText: String,
    badgeBg: Color,
    badgeTextColor: Color,
    body: String,
    footerNote: String? = null,
) {
    ManualCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = ScoreTextDark,
                modifier = Modifier.weight(1f),
            )
            Spacer(modifier = Modifier.width(8.dp))
            StatusPillBadge(
                text = badgeText,
                backgroundColor = badgeBg,
                textColor = badgeTextColor,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = body,
            fontSize = 13.sp,
            lineHeight = 19.sp,
            color = ScoreMutedText,
        )
        if (footerNote != null) {
            Spacer(modifier = Modifier.height(8.dp))
            GoStopDivider()
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = footerNote,
                fontSize = 11.5.sp,
                lineHeight = 16.sp,
                color = ScoreSubtleGray,
            )
        }
    }
}

@Composable
private fun CompactSubRow(
    label: String,
    desc: String,
    highlighted: Boolean,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = if (highlighted) ScoreSoftPinkBg else ScoreSummaryCardBg,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (highlighted) ScorePrimaryRed else ScoreTextDark,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = desc,
                fontSize = 12.sp,
                fontWeight = if (highlighted) FontWeight.Bold else FontWeight.Medium,
                color = if (highlighted) ScorePrimaryRed else ScoreMutedText,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun MonthlyCardsTab(
    showSellingOnly: Boolean,
    onToggleFilter: (Boolean) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 6.dp)
                .selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterToggleButton(
                text = stringResource(Res.string.manual_filter_all),
                selected = !showSellingOnly,
                selectedColor = ScoreDarkBadge,
                testTagName = "manual_filter_all",
                onClick = { onToggleFilter(false) },
                modifier = Modifier.weight(1f),
            )
            FilterToggleButton(
                text = stringResource(Res.string.manual_filter_selling),
                selected = showSellingOnly,
                selectedColor = ScorePrimaryRed,
                testTagName = "manual_filter_selling",
                onClick = { onToggleFilter(true) },
                modifier = Modifier.weight(1f),
            )
        }
        ManualPageList(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            topPadding = 6.dp,
        ) {
            if (showSellingOnly) {
                item(key = "selling_banner", contentType = "banner") {
                    ManualInfoBanner(stringResource(Res.string.manual_selling_banner))
                }
                item(key = "selling_gwang_card", contentType = "manual_card") {
                    ManualCard {
                        SellingGroupHeader(
                            title = stringResource(Res.string.selling_section_gwang),
                            badgeText = stringResource(Res.string.selling_badge_gwang),
                            badgeBg = ScoreSoftPinkBg,
                            badgeTextColor = ScorePrimaryRed,
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            HwatuDeck.sellingGwangEntries.forEach { entry ->
                                SellingCardTile(entry = entry, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
                item(key = "selling_ssangpi_card", contentType = "manual_card") {
                    ManualCard {
                        SellingGroupHeader(
                            title = stringResource(Res.string.selling_section_ssangpi),
                            badgeText = stringResource(Res.string.selling_badge_ssangpi),
                            badgeBg = ScoreMintBg,
                            badgeTextColor = ScoreMintText,
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            HwatuDeck.sellingSsangpiEntries.forEach { entry ->
                                SellingCardTile(entry = entry, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
                item(key = "selling_disclaimer", contentType = "disclaimer") {
                    Text(
                        text = stringResource(Res.string.selling_disclaimer),
                        fontSize = 11.5.sp,
                        color = ScoreSubtleGray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            } else {
                items(
                    items = HwatuDeck.monthlySections,
                    key = { it.month },
                    contentType = { "monthly_section" },
                ) { section ->
                    ManualCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = stringResource(section.nameRes),
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = ScoreTextDark,
                                modifier = Modifier.weight(1f),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(Res.string.manual_month_card_count),
                                fontSize = 11.5.sp,
                                color = ScoreSubtleGray,
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            section.cards.forEach { card ->
                                MonthlyCardTile(card = card, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            item(key = "hwatu_credit", contentType = "credit_footer") {
                HwatuCreditFooter()
            }
        }
    }
}

@Composable
private fun FilterToggleButton(
    text: String,
    selected: Boolean,
    selectedColor: Color,
    testTagName: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .defaultMinSize(minHeight = 44.dp)
            .clip(RoundedCornerShape(10.dp))
            .testTag(testTagName)
            .selectable(selected = selected, role = Role.Tab, onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        color = if (selected) selectedColor else Color.White,
        border = if (selected) null else BorderStroke(1.dp, ScoreBorderNeutral),
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (selected) Color.White else ScoreMutedText,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun MonthlyCardTile(
    card: HwatuCardEntry,
    modifier: Modifier = Modifier,
) {
    val roleLabel = stringResource(card.role.labelRes)
    val (badgeBg, badgeText) = roleColors(card.role)
    Column(
        modifier = modifier.semantics(mergeDescendants = true) {},
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(card.drawable),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(103f / 168f)
                .clip(RoundedCornerShape(5.dp))
                .border(0.5.dp, ScoreBorderNeutral, RoundedCornerShape(5.dp))
        )
        Spacer(modifier = Modifier.height(5.dp))
        StatusPillBadge(
            text = roleLabel,
            backgroundColor = badgeBg,
            textColor = badgeText,
            fontSize = 9.5.sp,
            horizontalPadding = 6.dp,
            verticalPadding = 2.dp,
            maxLines = 2,
        )
    }
}

@Composable
private fun SellingGroupHeader(
    title: String,
    badgeText: String,
    badgeBg: Color,
    badgeTextColor: Color,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = ScoreTextDark,
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.width(8.dp))
        StatusPillBadge(
            text = badgeText,
            backgroundColor = badgeBg,
            textColor = badgeTextColor,
            fontSize = 11.sp,
        )
    }
}

@Composable
private fun SellingCardTile(
    entry: SellingCardEntry,
    modifier: Modifier = Modifier,
) {
    val month = stringResource(entry.monthRes)
    val name = stringResource(entry.nameRes)
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(entry.drawable),
            contentDescription = "$month $name",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(103f / 168f)
                .clip(RoundedCornerShape(5.dp))
                .border(0.5.dp, ScoreBorderNeutral, RoundedCornerShape(5.dp))
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = month,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = ScoreTextDark,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = name,
            fontSize = 10.sp,
            color = if (entry.isBiGwang) ScorePrimaryRed else ScoreMutedText,
            fontWeight = if (entry.isBiGwang) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
internal fun HwatuCreditFooter() {
    var showLicenseDialog by rememberSaveable { mutableStateOf(false) }

    if (showLicenseDialog) {
        HwatuLicenseDialog(onDismiss = { showLicenseDialog = false })
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 44.dp)
            .clip(RoundedCornerShape(8.dp))
            .testTag("hwatu_credit_footer")
            .clickable(role = Role.Button, onClick = { showLicenseDialog = true })
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(Res.string.hwatu_credit),
            fontSize = 10.5.sp,
            color = ScoreSubtleGray,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = stringResource(Res.string.hwatu_credit_tap_hint),
            fontSize = 10.5.sp,
            color = ScorePrimaryRed,
            fontWeight = FontWeight.SemiBold,
            textDecoration = TextDecoration.Underline,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
internal fun HwatuLicenseDialog(onDismiss: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    val sourceUrl = stringResource(Res.string.hwatu_license_source_url)
    val licenseUrl = stringResource(Res.string.hwatu_license_url)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .widthIn(max = 390.dp)
                .heightIn(max = 640.dp)
                .enableTestTagsAsResourceId()
                .testTag("hwatu_license_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            elevation = 8.dp,
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(Res.string.hwatu_license_dialog_title),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = ScoreTextDark,
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(ScoreNeutralBtnBg)
                            .clickable(role = Role.Button, onClick = onDismiss),
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

                Spacer(modifier = Modifier.height(14.dp))
                LicenseInfoBlock(
                    label = stringResource(Res.string.hwatu_license_author_label),
                    value = stringResource(Res.string.hwatu_license_author_value),
                )
                Spacer(modifier = Modifier.height(10.dp))
                LicenseInfoBlock(
                    label = stringResource(Res.string.hwatu_license_source_label),
                    value = sourceUrl,
                    isLink = true,
                    onClick = { runCatching { uriHandler.openUri(sourceUrl) } },
                )
                Spacer(modifier = Modifier.height(10.dp))
                LicenseInfoBlock(
                    label = stringResource(Res.string.hwatu_license_type_label),
                    value = stringResource(Res.string.hwatu_license_type_value),
                    secondaryLink = licenseUrl,
                    onClick = { runCatching { uriHandler.openUri(licenseUrl) } },
                )
                Spacer(modifier = Modifier.height(10.dp))
                LicenseInfoBlock(
                    label = stringResource(Res.string.hwatu_license_mod_label),
                    value = stringResource(Res.string.hwatu_license_mod_value),
                )
                Spacer(modifier = Modifier.height(18.dp))
                GoStopButton(
                    text = stringResource(Res.string.confirm),
                    onClick = onDismiss,
                )
            }
        }
    }
}

@Composable
private fun LicenseInfoBlock(
    label: String,
    value: String,
    isLink: Boolean = false,
    secondaryLink: String? = null,
    onClick: (() -> Unit)? = null,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(role = Role.Button, onClick = onClick)
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(12.dp),
        color = ScoreSummaryCardBg,
        border = BorderStroke(1.dp, ScoreCardBorder),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ScoreSubtleGray,
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = value,
                fontSize = 12.5.sp,
                lineHeight = 18.sp,
                color = if (isLink) ScorePrimaryRed else ScoreTextDark,
                fontWeight = if (isLink) FontWeight.SemiBold else FontWeight.Medium,
                textDecoration = if (isLink) TextDecoration.Underline else TextDecoration.None,
            )
            if (secondaryLink != null) {
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = secondaryLink,
                    fontSize = 12.sp,
                    color = ScorePrimaryRed,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = TextDecoration.Underline,
                )
            }
        }
    }
}

private fun roleColors(role: HwatuCardRole): Pair<Color, Color> = when (role) {
    HwatuCardRole.GWANG, HwatuCardRole.BI_GWANG, HwatuCardRole.HONGDAN -> ScoreSoftPinkBg to ScorePrimaryRed
    HwatuCardRole.GODORI -> AmberBg to AmberText
    HwatuCardRole.YEOL, HwatuCardRole.TTI -> AmberBg to AmberText
    HwatuCardRole.YEOL_SSANGPI, HwatuCardRole.SSANGPI -> ScoreMintBg to ScoreMintText
    HwatuCardRole.CHEONGDAN -> BlueBg to BlueText
    HwatuCardRole.CHODAN -> GreenBg to GreenText
    HwatuCardRole.PI -> ScoreNeutralBtnBg to ScoreMutedText
}
