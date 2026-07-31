package zero.friends.gostopcalculator.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import zero.friends.domain.model.Gamer
import zero.friends.gostopcalculator.theme.Nero
import zero.friends.gostopcalculator.theme.OrangeyRed
import zero.friends.gostopcalculator.util.getMoneyColor

@Composable
fun GamerItem(modifier: Modifier = Modifier, index: Int, gamer: Gamer) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        val moneyColor by remember(gamer.account) {
            derivedStateOf { gamer.account.getMoneyColor() }
        }
        Text(
            text = (index + 1).toString(),
            modifier = Modifier
                .align(Alignment.CenterVertically),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = OrangeyRed
        )
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.padding(4.dp))
            Column(
                modifier = Modifier.align(Alignment.CenterVertically),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = gamer.winnerOption?.korean ?: (" " + (gamer.sellerOption?.korean ?: "")),
                    fontSize = 8.sp,
                    modifier = Modifier.alpha(if (gamer.isWinner()) 1f else 0f)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = gamer.name,
                        fontSize = 16.sp,
                        color = Nero,
                    )
                    Text(
                        text = "${gamer.account}원",
                        textAlign = TextAlign.Center,
                        color = moneyColor,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .align(Alignment.CenterVertically),
                        maxLines = 1,
                    )
                }
                Text(
                    modifier = Modifier.alpha(if (gamer.scoreOption.isNotEmpty() || gamer.loserOption.isNotEmpty()) 1f else 0f),
                    text = gamer.scoreOption.joinToString(", ") { it.korean } +
                            if (gamer.loserOption.isNotEmpty()) {
                                val separator = if (gamer.scoreOption.isNotEmpty()) ", " else ""
                                separator + gamer.loserOption.joinToString(", ") { it.korean }
                            } else "",
                    fontSize = 8.sp,
                )
            }
        }
    }
}
