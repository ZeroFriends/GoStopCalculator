package zero.friends.gostopcalculator.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import zero.friends.domain.model.Gamer
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.util.getMoneyColor

@Composable
fun CalculatedBox(index: Int, gamer: Gamer) {
    ContentsCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(vertical = 12.dp)) {
            GamerItem(modifier = Modifier.fillMaxWidth(), index = index, gamer = gamer)
            Spacer(modifier = Modifier.padding(6.dp))
            for (target in gamer.calculate) {
                Text(
                    modifier = Modifier.padding(horizontal = 18.dp), text = buildAnnotatedString {
                        append("${target.name}에게 ")
                        withStyle(style = SpanStyle(color = colorResource(id = target.account.getMoneyColor()))) {
                            append(String.format(stringResource(id = R.string.price), target.account))
                        }
                        append(" 을 ${if (target.account > 0) "받아야합니다." else "줘야합니다."}")
                    }
                )
                Spacer(modifier = Modifier.padding(3.dp))

            }
        }
    }
}