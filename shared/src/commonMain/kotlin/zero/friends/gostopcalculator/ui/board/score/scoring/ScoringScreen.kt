package zero.friends.gostopcalculator.ui.board.score.scoring

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.stringResource
import zero.friends.domain.model.Game
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.ScoreOption
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.ic_hero_stars
import zero.friends.gostopcalculator.shared.generated.resources.ic_menu_book
import zero.friends.gostopcalculator.shared.generated.resources.manual
import zero.friends.gostopcalculator.shared.generated.resources.next_step_1
import zero.friends.gostopcalculator.shared.generated.resources.scoring_main_text
import zero.friends.gostopcalculator.shared.generated.resources.scoring_sub_text
import zero.friends.gostopcalculator.ui.board.score.common.DashedGamerCard
import zero.friends.gostopcalculator.ui.board.score.common.GamerListHeader
import zero.friends.gostopcalculator.ui.board.score.common.PlayerIndexBadge
import zero.friends.gostopcalculator.ui.board.score.common.ScoreBorderNeutral
import zero.friends.gostopcalculator.ui.board.score.common.ScoreDisabledBadgeBg
import zero.friends.gostopcalculator.ui.board.score.common.ScoreMutedText
import zero.friends.gostopcalculator.ui.board.score.common.ScorePhaseLayout
import zero.friends.gostopcalculator.ui.board.score.common.ScorePlayerCard
import zero.friends.gostopcalculator.ui.board.score.common.ScorePrimaryRed
import zero.friends.gostopcalculator.ui.board.score.common.ScoreSoftPinkBg
import zero.friends.gostopcalculator.ui.board.score.common.ScoreSubtleGray
import zero.friends.gostopcalculator.ui.board.score.common.ScoreTextDark
import zero.friends.gostopcalculator.ui.board.score.common.StatusPillBadge
import zero.friends.gostopcalculator.util.localizedName

@Composable
fun ScoringScreen(
    game: Game,
    gamers: List<Gamer>,
    seller: Gamer?,
    onNext: () -> Unit,
    onBack: () -> Unit,
    onExit: () -> Unit,
    onSelectScore: (Gamer, ScoreOption) -> Unit,
    onShowManual: () -> Unit
) {
    ScorePhaseLayout(
        gameTitle = game.name,
        mainText = stringResource(Res.string.scoring_main_text),
        subText = stringResource(Res.string.scoring_sub_text),
        heroIcon = Res.drawable.ic_hero_stars,
        buttonText = stringResource(Res.string.next_step_1),
        buttonEnabled = true,
        onBack = onBack,
        onExit = onExit,
        onButtonClick = onNext
    ) {
        ScoringGamerList(
            gamers = gamers,
            seller = seller,
            onSelectScore = onSelectScore,
            onShowManual = onShowManual
        )
    }
}

@Composable
private fun ScoringGamerList(
    gamers: List<Gamer>,
    seller: Gamer?,
    onSelectScore: (Gamer, ScoreOption) -> Unit,
    onShowManual: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        GamerListHeader(
            buttonText = stringResource(Res.string.manual),
            onButtonClick = onShowManual,
            playerCount = gamers.size,
            buttonIcon = Res.drawable.ic_menu_book
        )
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            contentPadding = PaddingValues(bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(
                items = gamers,
                key = { _, item -> item.id }
            ) { index, gamer ->
                if (gamer.id == seller?.id) {
                    DashedGamerCard {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                PlayerIndexBadge(
                                    index = index,
                                    containerColor = ScoreBorderNeutral,
                                    textColor = ScoreSubtleGray,
                                    size = 24.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = seller.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ScoreSubtleGray,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            StatusPillBadge(
                                text = seller.sellerOption?.localizedName().orEmpty(),
                                backgroundColor = ScoreDisabledBadgeBg,
                                textColor = ScoreSubtleGray,
                                fontSize = 12.sp,
                                horizontalPadding = 12.dp,
                                verticalPadding = 4.dp
                            )
                        }
                    }
                } else {
                    ScoringGamerItem(
                        index = index,
                        gamer = gamer,
                        onSelectScore = onSelectScore
                    )
                }
            }
        }
    }
}

@Composable
private fun ScoringGamerItem(
    index: Int,
    gamer: Gamer,
    onSelectScore: (Gamer, ScoreOption) -> Unit
) {
    ScorePlayerCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                PlayerIndexBadge(
                    index = index,
                    containerColor = ScoreSoftPinkBg,
                    textColor = ScorePrimaryRed,
                    size = 24.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = gamer.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = ScoreTextDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            ScoreOptionRow(
                gamer = gamer,
                onSelectScore = onSelectScore
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ScoreOptionRow(
    gamer: Gamer,
    onSelectScore: (Gamer, ScoreOption) -> Unit
) {
    val options = ScoreOption.entries

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            val isSelected = gamer.scoreOption.contains(option)
            val shape = RoundedCornerShape(100.dp)
            Surface(
                modifier = Modifier
                    .clip(shape)
                    .clickable(role = Role.Checkbox) {
                        onSelectScore(gamer, option)
                    },
                shape = shape,
                color = if (isSelected) ScorePrimaryRed else Color.White,
                border = if (isSelected) null else BorderStroke(1.dp, ScoreBorderNeutral),
                elevation = if (isSelected) 2.dp else 0.dp
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 13.dp, vertical = 6.dp),
                    text = option.localizedName(),
                    fontSize = 12.5.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.White else ScoreMutedText,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }
    }
}
