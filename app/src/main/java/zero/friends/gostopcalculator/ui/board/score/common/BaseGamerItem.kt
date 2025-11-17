package zero.friends.gostopcalculator.ui.board.score.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import zero.friends.domain.model.Gamer
import zero.friends.gostopcalculator.R

/**
 * 게이머 아이템의 기본 레이아웃
 * 번호, 이름, 배지(옵션) 표시하고 오른쪽 content 영역 제공
 */
@Composable
fun BaseGamerItem(
    index: Int,
    gamer: Gamer,
    isEnabled: Boolean = true,
    badge: String? = null,
    content: @Composable (RowScope.() -> Unit)? = null,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = (index + 1).toString(),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(10.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = if (isEnabled) R.color.orangey_red else R.color.gray)
            )
            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = gamer.name,
                        fontSize = 16.sp,
                        color = colorResource(id = if (isEnabled) R.color.nero else R.color.gray)
                    )
                    if (badge != null) {
                        Spacer(modifier = Modifier.padding(3.dp))
                        OptionBox(
                            text = badge,
                            color = colorResource(id = R.color.gray)
                        )
                    }
                }
            }
        }

        content?.invoke(this)
    }
}

