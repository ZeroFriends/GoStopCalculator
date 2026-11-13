package zero.friends.gostopcalculator.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import zero.friends.domain.model.PlayerResult
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.empty_playing_game
import zero.friends.gostopcalculator.shared.generated.resources.ic_topbar_back
import zero.friends.gostopcalculator.shared.generated.resources.start_new_game_prompt
import zero.friends.gostopcalculator.shared.generated.resources.won
import zero.friends.gostopcalculator.theme.Gray
import zero.friends.gostopcalculator.theme.Nero
import zero.friends.gostopcalculator.theme.OrangeyRed
import zero.friends.gostopcalculator.util.separateComma

@Composable
fun CenterTextTopBar(
    text: String,
    isRed: Boolean = true,
    onBack: (() -> Unit)? = null,
    onAction: (() -> Unit)? = null,
    actionIcon: Painter? = null,
    actionText: String? = null
) {
    val iconModifier = Modifier
        .defaultMinSize(60.dp, 60.dp)

    val iconColor = if (isRed) Color.White else Color.Black
    val topBarBackgroundColor = if (isRed) OrangeyRed else Color.White

    SystemBarAppearance(
        isLightStatusBar = !isRed,
        isLightNavigationBar = true,
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = topBarBackgroundColor,
        elevation = 1.dp
    ) {
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
                Box(modifier = iconModifier, contentAlignment = Alignment.Center) {
                    if (onBack != null) {
                        IconButton(onClick = { onBack() }) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_topbar_back),
                                contentDescription = "Back",
                                tint = iconColor
                            )
                        }
                    }
                }
            },
            actions = {
                Box(modifier = iconModifier, contentAlignment = Alignment.Center) {
                    if (onAction != null) {
                        if (actionIcon != null) {
                            IconButton(onClick = onAction) {
                                Icon(
                                    painter = actionIcon,
                                    contentDescription = "Close",
                                    tint = iconColor
                                )
                            }
                        } else if (actionText != null) {
                            val actionTextColor = if (isRed) Color.White else OrangeyRed
                            Surface(
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .clickable(onClick = onAction),
                                color = Color.Transparent
                            ) {
                                Text(
                                    text = actionText,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = actionTextColor
                                )
                            }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            windowInsets = WindowInsets.statusBars,
            backgroundColor = Color.Transparent,
            contentColor = if (isRed) Color.White else Color.Black,
            elevation = 0.dp
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ContentsCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    boxContents: @Composable (BoxScope.() -> Unit)
) {
    if (onClick != null) {
        Card(
            modifier = modifier.padding(4.dp),
            shape = RoundedCornerShape(18.dp),
            backgroundColor = Color.White,
            elevation = 6.dp,
            onClick = onClick,
        ) {
            Box(contentAlignment = Alignment.Center) {
                boxContents()
            }
        }
    } else {
        Card(
            modifier = modifier.padding(4.dp),
            shape = RoundedCornerShape(18.dp),
            backgroundColor = Color.White,
            elevation = 6.dp,
        ) {
            Box(contentAlignment = Alignment.Center) {
                boxContents()
            }
        }
    }
}

@Composable
fun EmptyHistory(
    painter: Painter? = null,
    title: String = stringResource(Res.string.empty_playing_game),
    subTitle: String = stringResource(Res.string.start_new_game_prompt)
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (painter != null) {
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier.padding(bottom = 9.dp)
                )
            }
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
                textAlign = TextAlign.Center,
                color = Gray
            )
        }
    }
}

@Composable
fun DescriptionBox(modifier: Modifier = Modifier, mainText: String, subText: String) {
    ContentsCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(vertical = 36.dp, horizontal = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = mainText,
                fontSize = 20.sp,
                color = Nero,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.padding(5.dp))
            Text(
                text = subText,
                fontSize = 14.sp,
                color = Nero,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun PlayerItem(index: Int, playerResult: PlayerResult) {
    val moneyColor by remember(playerResult.account) {
        derivedStateOf {
            if (playerResult.account > 0) Color(0xFFFF5252)
            else if (playerResult.account < 0) Color(0xFF1E88E5)
            else Color.Gray
        }
    }
    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = (index + 1).toString(),
                modifier = Modifier.align(Alignment.CenterVertically),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = OrangeyRed
            )
            Spacer(modifier = Modifier.padding(4.dp))
            Text(
                text = playerResult.name,
                fontSize = 14.sp,
                color = Nero,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }

        val won = stringResource(Res.string.won)
        val accountText = when {
            playerResult.account > 0 -> "+${playerResult.account.separateComma()}$won"
            else -> "${playerResult.account.separateComma()}$won"
        }
        Text(
            text = accountText,
            color = moneyColor,
            fontSize = 14.sp
        )
    }
}
