package zero.friends.gostopcalculator.ui.board.result

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.content.Intent.EXTRA_STREAM
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.Intent.createChooser
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import zero.friends.gostopcalculator.GoogleAdmob
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.theme.GoStopTheme
import zero.friends.gostopcalculator.util.ScreenCaptureManager
import javax.inject.Inject

@AndroidEntryPoint
class ResultActivity : ComponentActivity() {

    @Inject
    lateinit var roundTraceFormatter: RoundTraceFormatter

    @Inject
    lateinit var screenCaptureManager: ScreenCaptureManager

    @Inject
    lateinit var googleAdmob: GoogleAdmob

    private val viewModel: ResultViewModel by viewModels()

    private val shareResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            googleAdmob.showAd { }
            screenCaptureManager.clearTempFile()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GoStopTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val coroutineScope = rememberCoroutineScope()

                ResultScreen(
                    uiState = uiState,
                    roundTraceFormatter = roundTraceFormatter,
                    onBackClick = { finish() },
                    onShareClick = { graphicsLayer ->
                        coroutineScope.launch {
                            val uri = screenCaptureManager.captureAndGetUri(graphicsLayer, AUTHORITY)
                            if (uri != null) {
                                val shareIntent = Intent(ACTION_SEND).apply {
                                    type = "image/png"
                                    putExtra(EXTRA_STREAM, uri)
                                    addFlags(FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                val intent = createChooser(shareIntent, getString(R.string.share))
                                shareResultLauncher.launch(intent)
                            }
                        }
                    }
                )
            }
        }
    }

    enum class ScreenType {
        CALCULATE,
        DETAIL
    }

    companion object {
        const val EXTRA_GAME_ID = "extra_game_id"
        const val EXTRA_ROUND_ID = "extra_round_id"
        const val EXTRA_SCREEN_TYPE = "extra_screen_type"
        private const val AUTHORITY = "zero.friends.gostopcalculator.fileprovider"

        fun createCalculateIntent(context: Context, gameId: Long): Intent {
            return Intent(context, ResultActivity::class.java).apply {
                putExtra(EXTRA_GAME_ID, gameId)
                putExtra(EXTRA_SCREEN_TYPE, ScreenType.CALCULATE)
            }
        }

        fun createDetailIntent(context: Context, gameId: Long, roundId: Long): Intent {
            return Intent(context, ResultActivity::class.java).apply {
                putExtra(EXTRA_GAME_ID, gameId)
                putExtra(EXTRA_ROUND_ID, roundId)
                putExtra(EXTRA_SCREEN_TYPE, ScreenType.DETAIL)
            }
        }
    }
}
