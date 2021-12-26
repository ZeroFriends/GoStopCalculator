package zero.friends.gostopcalculator.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import zero.friends.domain.model.PlayerResult
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.util.TabKeyboardDownModifier
import zero.friends.gostopcalculator.util.getMoneyColor


@Composable
fun CenterTextTopBar(
    text: String,
    isRed: Boolean = true,
    onBack: () -> Unit = {},
    onAction: (() -> Unit)? = null,
    actionIcon: Painter = painterResource(id = R.drawable.ic_topbar_close)
) {
    val modifier = Modifier
        .defaultMinSize(60.dp, 60.dp)//todo title Center 방법이 있다면 변경해보자...
        .background(Color.Transparent)

    val iconColor = if (isRed) R.color.white else R.color.black
    TopAppBar(
        title = {
            Text(
                text = text,
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                modifier = Modifier
                    .wrapContentWidth(align = Alignment.CenterHorizontally)
                    .fillMaxWidth()
            )
        },
        navigationIcon = {
            Box(modifier = modifier, contentAlignment = Alignment.Center) {
                IconButton(onClick = { onBack() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_topbar_back),
                        contentDescription = "Back",
                        tint = colorResource(id = iconColor)
                    )
                }
            }
        },
        actions = {
            Box(modifier = modifier, contentAlignment = Alignment.Center) {
                if (onAction != null) {
                    IconButton(onClick = onAction) {
                        Icon(
                            painter = actionIcon,
                            contentDescription = "Close",
                            tint = colorResource(id = iconColor)
                        )
                    }
                }
            }
        },
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = if (isRed) colorResource(id = R.color.orangey_red) else colorResource(id = R.color.white),
        contentColor = if (isRed) colorResource(id = R.color.white) else colorResource(id = R.color.black),
        elevation = 0.dp
    )
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ContentsCard(onClick: (() -> Unit)? = null, modifier: Modifier = Modifier, boxContents: @Composable () -> Unit) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        backgroundColor = colorResource(id = R.color.white),
        elevation = 6.dp,
        onClick = onClick ?: {},
        enabled = onClick != null
    ) {
        Surface {
            boxContents()
        }
    }
}

@Composable
fun EmptyHistory(
    painter: Painter = painterResource(id = R.drawable.ic_onodofu),
    title: String = stringResource(id = R.string.empty_game),
    subTitle: String = stringResource(id = R.string.info_new_game_start)
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painter,
                contentDescription = null,
                alignment = Alignment.Center,
                contentScale = ContentScale.Crop,
                modifier = Modifier.padding(bottom = 9.dp)
            )
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 2.dp),
                textAlign = TextAlign.Center
            )
            Text(
                text = subTitle,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun DescriptionBox(modifier: Modifier = Modifier, mainText: String, subText: String) {
    ContentsCard(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 36.dp, horizontal = 14.dp)
                .then(TabKeyboardDownModifier()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = mainText,
                fontSize = 20.sp,
                color = colorResource(id = R.color.nero),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.padding(5.dp))
            Text(
                text = subText,
                fontSize = 14.sp,
                color = colorResource(id = R.color.nero),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun PlayerItem(index: Int, playerResult: PlayerResult) {
    val moneyColor by remember(playerResult.account) {
        derivedStateOf { playerResult.account.getMoneyColor() }
    }
    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = (index + 1).toString(),
                modifier = Modifier
                    .align(Alignment.CenterVertically),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.orangey_red)
            )
            Spacer(modifier = Modifier.padding(4.dp))
            Text(
                text = playerResult.name,
                fontSize = 14.sp,
                color = colorResource(id = R.color.nero),
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }

        Text(
            text = String.format(stringResource(id = R.string.price), playerResult.account),
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.CenterVertically),
            color = colorResource(id = moneyColor)
        )
    }
}

@Preview
@Composable
private fun PlayerItemPreview() {
    PlayerItem(index = 0, playerResult = PlayerResult("zero.dev", 1000))
}

@Preview
@Composable
private fun DescriptionBoxPreview() {
    DescriptionBox(
        mainText = stringResource(id = R.string.start),
        subText = stringResource(id = R.string.start_description)
    )
}

@Preview
@Composable
private fun EmptyHistoryPreview() {
    EmptyHistory()
}

@Preview
@Composable
private fun CenterTextTopBarPreview() {
    CenterTextTopBar("hello")
}


@Preview
@Composable
private fun ContentsCardPreview() {
    ContentsCard(boxContents = {})
}
