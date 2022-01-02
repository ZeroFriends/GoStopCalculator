package zero.friends.gostopcalculator.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import zero.friends.domain.model.Gamer
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.util.getMoneyColor

@Composable
fun GamerItem(modifier: Modifier = Modifier, index: Int, gamer: Gamer) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val moneyColor = remember(gamer.account) {
            derivedStateOf { gamer.account.getMoneyColor() }
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = (index + 1).toString(),
                modifier = Modifier
                    .align(Alignment.CenterVertically),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.orangey_red)
            )
            Spacer(modifier = Modifier.padding(4.dp))
            Column(
                modifier = Modifier.align(Alignment.CenterVertically),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = gamer.winnerOption?.korean ?: "" + " " + gamer.sellerOption?.korean,
                    fontSize = 8.sp,
                    modifier = Modifier.alpha(if (gamer.isWinner()) 1f else 0f)
                )
                Text(
                    text = gamer.name,
                    fontSize = 16.sp,
                    color = colorResource(id = R.color.nero),
                )
                Text(
                    modifier = Modifier.alpha(if (gamer.scoreOption.isNotEmpty() || gamer.loserOption.isNotEmpty()) 1f else 0f),
                    text = gamer.scoreOption.joinToString(" ") { it.korean } + " " + gamer.loserOption.joinToString(" ") { it.korean },
                    fontSize = 8.sp,
                )
            }
        }

        Text(
            text = String.format(stringResource(id = R.string.price), gamer.account),
            textAlign = TextAlign.Center,
            color = colorResource(id = moneyColor.value),
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GamerItemPreview() {
    GamerItem(index = 0, gamer = Gamer())
}