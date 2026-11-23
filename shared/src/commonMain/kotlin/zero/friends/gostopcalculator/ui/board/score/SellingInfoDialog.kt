package zero.friends.gostopcalculator.ui.board.score

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.close
import zero.friends.gostopcalculator.shared.generated.resources.confirm
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_month_1
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_month_11
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_month_12
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_month_3
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_month_8
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_month_9
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_month_bonus
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_name_11_gwang
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_name_11_ssangpi
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_name_12_gwang
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_name_12_ssangpi
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_name_1_gwang
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_name_3_gwang
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_name_8_gwang
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_name_9_ssangpi
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_name_bonus
import zero.friends.gostopcalculator.shared.generated.resources.ic_hero_selling
import zero.friends.gostopcalculator.shared.generated.resources.ic_hwatu_01_gwang
import zero.friends.gostopcalculator.shared.generated.resources.ic_hwatu_03_gwang
import zero.friends.gostopcalculator.shared.generated.resources.ic_hwatu_08_gwang
import zero.friends.gostopcalculator.shared.generated.resources.ic_hwatu_09_ssangpi
import zero.friends.gostopcalculator.shared.generated.resources.ic_hwatu_11_gwang
import zero.friends.gostopcalculator.shared.generated.resources.ic_hwatu_11_ssangpi
import zero.friends.gostopcalculator.shared.generated.resources.ic_hwatu_12_gwang
import zero.friends.gostopcalculator.shared.generated.resources.ic_hwatu_12_ssangpi
import zero.friends.gostopcalculator.shared.generated.resources.ic_hwatu_bonus_pi
import zero.friends.gostopcalculator.shared.generated.resources.ic_topbar_close
import zero.friends.gostopcalculator.shared.generated.resources.selling_badge_gwang
import zero.friends.gostopcalculator.shared.generated.resources.selling_badge_ssangpi
import zero.friends.gostopcalculator.shared.generated.resources.selling_disclaimer
import zero.friends.gostopcalculator.shared.generated.resources.selling_extra_button
import zero.friends.gostopcalculator.shared.generated.resources.selling_section_gwang
import zero.friends.gostopcalculator.shared.generated.resources.selling_section_ssangpi
import zero.friends.gostopcalculator.ui.board.score.common.ScoreMintBg
import zero.friends.gostopcalculator.ui.board.score.common.ScoreMintText
import zero.friends.gostopcalculator.ui.board.score.common.ScoreMutedText
import zero.friends.gostopcalculator.ui.board.score.common.ScoreNeutralBtnBg
import zero.friends.gostopcalculator.ui.board.score.common.ScorePrimaryRed
import zero.friends.gostopcalculator.ui.board.score.common.ScoreSoftPinkBg
import zero.friends.gostopcalculator.ui.board.score.common.ScoreSubtleGray
import zero.friends.gostopcalculator.ui.board.score.common.ScoreTextDark
import zero.friends.gostopcalculator.ui.board.score.common.StatusPillBadge
import zero.friends.gostopcalculator.ui.common.GoStopButton

private data class HwatuCardSpec(
    val drawable: DrawableResource,
    val monthRes: StringResource,
    val nameRes: StringResource,
)

private val GWANG_CARDS = listOf(
    HwatuCardSpec(Res.drawable.ic_hwatu_01_gwang, Res.string.hwatu_month_1, Res.string.hwatu_name_1_gwang),
    HwatuCardSpec(Res.drawable.ic_hwatu_03_gwang, Res.string.hwatu_month_3, Res.string.hwatu_name_3_gwang),
    HwatuCardSpec(Res.drawable.ic_hwatu_08_gwang, Res.string.hwatu_month_8, Res.string.hwatu_name_8_gwang),
    HwatuCardSpec(Res.drawable.ic_hwatu_11_gwang, Res.string.hwatu_month_11, Res.string.hwatu_name_11_gwang),
    HwatuCardSpec(Res.drawable.ic_hwatu_12_gwang, Res.string.hwatu_month_12, Res.string.hwatu_name_12_gwang),
)

private val SSANGPI_CARDS = listOf(
    HwatuCardSpec(Res.drawable.ic_hwatu_09_ssangpi, Res.string.hwatu_month_9, Res.string.hwatu_name_9_ssangpi),
    HwatuCardSpec(Res.drawable.ic_hwatu_11_ssangpi, Res.string.hwatu_month_11, Res.string.hwatu_name_11_ssangpi),
    HwatuCardSpec(Res.drawable.ic_hwatu_12_ssangpi, Res.string.hwatu_month_12, Res.string.hwatu_name_12_ssangpi),
    HwatuCardSpec(Res.drawable.ic_hwatu_bonus_pi, Res.string.hwatu_month_bonus, Res.string.hwatu_name_bonus),
)

@Composable
fun SellingInfoDialog(onClose: () -> Unit = {}) {
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .widthIn(max = 390.dp)
                .heightIn(max = 680.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(ScoreNeutralBtnBg)
                            .clickable(role = Role.Button, onClick = onClose),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_topbar_close),
                            contentDescription = stringResource(Res.string.close),
                            tint = ScoreMutedText,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(ScoreSoftPinkBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_hero_selling),
                                contentDescription = null,
                                tint = ScorePrimaryRed,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = stringResource(Res.string.selling_extra_button),
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = ScoreTextDark,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(Res.string.selling_disclaimer),
                            fontSize = 12.sp,
                            color = ScoreSubtleGray,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                SectionHeader(
                    title = stringResource(Res.string.selling_section_gwang),
                    badgeText = stringResource(Res.string.selling_badge_gwang),
                    badgeBg = ScoreSoftPinkBg,
                    badgeTextColor = ScorePrimaryRed
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    GWANG_CARDS.forEach { card ->
                        HwatuCardTile(
                            spec = card,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                SectionHeader(
                    title = stringResource(Res.string.selling_section_ssangpi),
                    badgeText = stringResource(Res.string.selling_badge_ssangpi),
                    badgeBg = ScoreMintBg,
                    badgeTextColor = ScoreMintText
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SSANGPI_CARDS.forEach { card ->
                        HwatuCardTile(
                            spec = card,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                GoStopButton(
                    text = stringResource(Res.string.confirm),
                    onClick = onClose
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    badgeText: String,
    badgeBg: Color,
    badgeTextColor: Color,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Bold,
            color = ScoreTextDark
        )
        StatusPillBadge(
            text = badgeText,
            backgroundColor = badgeBg,
            textColor = badgeTextColor,
            fontSize = 11.sp,
            horizontalPadding = 8.dp,
            verticalPadding = 3.dp
        )
    }
}

@Composable
private fun HwatuCardTile(
    spec: HwatuCardSpec,
    modifier: Modifier = Modifier,
) {
    val month = stringResource(spec.monthRes)
    val name = stringResource(spec.nameRes)
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(spec.drawable),
            contentDescription = "$month $name",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(53f / 82f)
                .clip(RoundedCornerShape(6.dp))
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = month,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            color = ScoreTextDark,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
        Text(
            text = name,
            fontSize = 10.5.sp,
            color = ScoreMutedText,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}
