package zero.friends.gostopcalculator.ui.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.ScoreOption
import zero.friends.domain.model.SellerOption
import zero.friends.domain.model.WinnerOption
import zero.friends.gostopcalculator.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RoundBox(
    modifier: Modifier = Modifier,
    index: Int? = null,
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = String.format(stringResource(id = R.string.round, (index + 1))))
                    Surface(modifier = Modifier.clickable { onClickMore?.invoke() }) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_more_black),
                            contentDescription = null
                        )
                    }
                }
            }

            GridItems(data = gamers, nColumns = 2) { index, gamer ->
                GamerItem(index = index, gamer = gamer)
            }
            Spacer(modifier = Modifier.padding(4.dp))

            if (onClickDetail != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colorResource(id = R.color.light_gray))
                        .clickable { onClickDetail.invoke() }
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.detail),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RoundBoxPreview() {
    RoundBox(
        index = 1,
        gamers = listOf(
            Gamer(name = "조재영", account = 10, winnerOption = WinnerOption.Winner),
            Gamer(
                name = "송준영", account = -10, sellerOption = SellerOption.Seller
            ),
            Gamer(
                name = "송준영",
                account = -100000,
                scoreOption = listOf(
                    ScoreOption.FirstDdadak,
                    ScoreOption.FirstFuck,
                )
            ),
            Gamer(
                name = "김경민",
                account = -1000,
                scoreOption = listOf(
                    ScoreOption.FirstDdadak,
                    ScoreOption.FirstFuck,
                )
            )
        ),
//        onClickMore = {},
//        onClickDetail = {}
    )
}