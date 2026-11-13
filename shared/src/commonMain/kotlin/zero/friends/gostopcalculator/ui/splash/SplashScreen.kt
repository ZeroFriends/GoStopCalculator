package zero.friends.gostopcalculator.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.animateLottieCompositionAsState
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.ExperimentalResourceApi
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.ui.common.SystemBarAppearance

@OptIn(ExperimentalResourceApi::class)
@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit = {}
) {
    SystemBarAppearance(
        isLightStatusBar = true,
        isLightNavigationBar = true,
    )
    val currentOnSplashFinished by rememberUpdatedState(onSplashFinished)
    val compositionResult = rememberLottieComposition {
        LottieCompositionSpec.JsonString(
            Res.readBytes("files/gostop_splash.json").decodeToString()
        )
    }
    val progressState = animateLottieCompositionAsState(compositionResult.value)
    var finished by remember { mutableStateOf(false) }

    val finishSplash: () -> Unit = {
        if (!finished) {
            finished = true
            currentOnSplashFinished()
        }
    }

    val isSplashDone by remember {
        derivedStateOf {
            compositionResult.isFailure || progressState.value >= 1f
        }
    }

    LaunchedEffect(isSplashDone) {
        if (isSplashDone) {
            finishSplash()
        }
    }

    LaunchedEffect(Unit) {
        delay(3_000)
        finishSplash()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        Image(
            painter = rememberLottiePainter(
                composition = compositionResult.value,
                progress = { progressState.value }
            ),
            contentDescription = "Splash Lottie Animation",
            modifier = Modifier
                .align(Alignment.Center)
                .size(150.dp, 150.dp)
        )
        Text(
            text = "Zero Friends",
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 20.dp)
        )
    }
}
