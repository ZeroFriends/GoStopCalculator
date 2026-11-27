package zero.friends.gostopcalculator.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import zero.friends.domain.model.Gamer
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.detail
import zero.friends.gostopcalculator.shared.generated.resources.ic_more_black
import zero.friends.gostopcalculator.shared.generated.resources.round_more_cd_format
import zero.friends.gostopcalculator.shared.generated.resources.round_number_format
import zero.friends.gostopcalculator.theme.Gray
import zero.friends.gostopcalculator.ui.board.score.common.ScorePrimaryRed
import zero.friends.gostopcalculator.ui.board.score.common.ScoreSoftPinkBg
import zero.friends.gostopcalculator.ui.board.score.common.ScoreSoftPinkBorder
import zero.friends.gostopcalculator.ui.board.score.common.ScoreTextDark
import zero.friends.gostopcalculator.ui.board.score.common.StatusPillBadge

@Composable
fun RoundBox(
    modifier: Modifier = Modifier,
    index: Int? = null,
    roundName: String = "",
    gamers: List<Gamer> = emptyList(),
    onClickDetail: (() -> Unit)? = null,
    onClickMore: (() -> Unit)? = null
) {
    ContentsCard(modifier = modifier.padding(vertical = 3.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            if (index != null) {
                val defaultTitle = stringResource(Res.string.round_number_format, index + 1)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 8.dp, top = 6.dp, bottom = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (roundName.isBlank()) {
                        Text(
                            text = defaultTitle,
                            modifier = Modifier.weight(1f),
                        )
                    } else {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            StatusPillBadge(
                                text = defaultTitle,
                                backgroundColor = ScoreSoftPinkBg,
                                textColor = ScorePrimaryRed,
                                borderColor = ScoreSoftPinkBorder,
                                fontSize = 11.sp,
                                horizontalPadding = 8.dp,
                                verticalPadding = 3.dp,
                            )
                            Text(
                                text = roundName,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = ScoreTextDark,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false),
                            )
                        }
                    }

                    if (onClickMore != null) {
                        val moreTargetLabel = if (roundName.isNotBlank()) {
                            "$defaultTitle $roundName"
                        } else {
                            defaultTitle
                        }
                        Surface(
                            modifier = Modifier
                                .sizeIn(minWidth = 44.dp, minHeight = 44.dp)
                                .clip(CircleShape)
                                .testTag("round_more_button_${index}")
                                .clickable(role = Role.Button, onClick = onClickMore),
                            shape = CircleShape,
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Image(
                                    painter = painterResource(Res.drawable.ic_more_black),
                                    contentDescription = stringResource(
                                        Res.string.round_more_cd_format,
                                        moreTargetLabel,
                                    )
                                )
                            }
                        }
                    }
                }
            }

            GridItems(data = gamers, nColumns = 2) { idx, gamer ->
                GamerItem(index = idx, gamer = gamer)
            }
            Spacer(modifier = Modifier.padding(4.dp))

            if (onClickDetail != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Gray.copy(alpha = 0.12f))
                        .clickable { onClickDetail.invoke() }
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(Res.string.detail),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}