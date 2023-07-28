package zero.friends.gostopcalculator.ui.board.result

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.LoserOption
import zero.friends.domain.model.ScoreOption
import zero.friends.domain.model.Target
import zero.friends.domain.model.WinnerOption
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.ui.common.CalculatedBox
import zero.friends.gostopcalculator.ui.common.CenterTextTopBar

private const val IMAGE_NAME = "image.png"
private const val AUTHORITY = "zerofriends"

@Composable
fun ResultScreen(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    gameName: String = "",
    gamers: List<Gamer> = emptyList(),
    onBack: () -> Unit = {},
    title: String = ""
) {
    val view = LocalView.current
    val context = LocalContext.current

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            CenterTextTopBar(
                text = gameName,
                onBack = onBack,
                isRed = false,
//                onAction = {
//                    //Note : 화면 및 기능이 바뀔일이 없어보여 여기다 때려넣음
//                    runCatching {
//                        val bitmap = Bitmap.createBitmap(
//                            view.width,
//                            view.height,
//                            Bitmap.Config.ARGB_8888
//                        ).applyCanvas { view.draw(this) }
//
//                        val file = File(context.cacheDir, IMAGE_NAME).apply {
//                            outputStream().use { out ->
//                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
//                                out.flush()
//                            }
//                        }
//                        FileProvider.getUriForFile(context, AUTHORITY, file)
//                    }.onSuccess { uri ->
//                        ShareCompat.IntentBuilder(context)
//                            .setType("image/*")
//                            .setStream(uri)
//                            .createChooserIntent()
//                            .also(context::startActivity)
//                    }.onFailure {
//                        Toast.makeText(context, "공유에 실패하였습니다.", Toast.LENGTH_SHORT).show()
//                    }
//                },
                actionText = stringResource(R.string.share)
            )
        }
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(18.dp)
        ) {
            item {
                Text(
                    text = title,
                    fontSize = 24.sp,
                    color = colorResource(id = R.color.nero),
                    fontWeight = FontWeight.Bold
                )
            }
            itemsIndexed(gamers) { index, gamer ->
                CalculatedBox(index, gamer)
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    ResultScreen(
        gameName = "helloWorld",
        gamers = listOf(
            Gamer(
                name = "송준영", calculate = listOf(
                    Target(name = "조재영", account = 1000),
                    Target(name = "김경민", account = -1000),
                ),
                account = 2000,
                winnerOption = WinnerOption.Winner
            ),
            Gamer(
                name = "김경민", calculate = listOf(
                    Target(name = "송준영", account = -1000),
                    Target(name = "조재영", account = -500),
                ),
                account = -2000,
                scoreOption = listOf(ScoreOption.SecondFuck),
                loserOption = listOf(LoserOption.LightBak)
            )
        ),
        title = stringResource(id = R.string.calculate_history_text)
    )
}
