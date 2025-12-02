package zero.friends.gostopcalculator.ui.board.result

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.content.Intent.EXTRA_STREAM
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.Intent.createChooser
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import zero.friends.domain.model.RoundTraceTerm
import zero.friends.gostopcalculator.GoogleAdmob
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.R.string
import zero.friends.gostopcalculator.databinding.ActivityResultBinding
import zero.friends.gostopcalculator.databinding.ViewTraceTableHeaderBinding
import zero.friends.gostopcalculator.databinding.ViewTraceTableRowBinding
import zero.friends.gostopcalculator.databinding.ViewTraceTitleBinding
import zero.friends.gostopcalculator.util.ScreenCaptureManager
import javax.inject.Inject

@AndroidEntryPoint
class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private val adapter = CalculatedGamerAdapter()

    @Inject
    lateinit var roundTraceFormatter: RoundTraceFormatter

    @Inject
    lateinit var screenCaptureManager: ScreenCaptureManager

    @Inject
    lateinit var googleAdmob: GoogleAdmob

    private val viewModel: ResultViewModel by viewModels()

    private val shareResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            // 공유 화면에서 돌아왔을 때 광고 표시 및 임시 파일 정리
            googleAdmob.showAd { }
            screenCaptureManager.clearTempFile()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindow()
        setupViews()
        observeViewModel()
    }

    private fun setupWindow() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.statusBars() or
                    WindowInsetsCompat.Type.navigationBars() or
                    WindowInsetsCompat.Type.displayCutout()
            )

            binding.appBarLayout.updatePadding(
                left = systemBars.left,
                top = systemBars.top,
                right = systemBars.right,
            )
            binding.scrollView.updatePadding(
                left = systemBars.left,
                right = systemBars.right,
                bottom = systemBars.bottom
            )

            insets
        }
    }

    private fun setupViews() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ResultActivity)
            adapter = this@ResultActivity.adapter
            isNestedScrollingEnabled = false
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnShare.setOnClickListener {
            lifecycleScope.launch {
                val uri = screenCaptureManager.captureAndGetUri(binding.contentLayout, AUTHORITY)
                if (uri != null) {
                    val shareIntent = Intent(ACTION_SEND).apply {
                        this.type = "image/png"
                        putExtra(EXTRA_STREAM, uri)
                        addFlags(FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    val intent = createChooser(shareIntent, getString(string.share))
                    shareResultLauncher.launch(intent)
                }
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                binding.tvTitle.text = state.gameName
                binding.tvSubTitle.text = when (state.screenType) {
                    ScreenType.CALCULATE -> getString(R.string.calculate_history_text)
                    ScreenType.DETAIL -> getString(R.string.detail_text)
                }
                adapter.submitList(state.gamers)
                bindFormula(state)
            }
        }
    }

    private fun bindFormula(state: ResultUiState) {
        val trace = state.roundTrace
        val shouldShow = state.screenType == ScreenType.DETAIL && trace != null

        binding.winnerTraceCard.isVisible = shouldShow
        binding.sellerTraceCard.isVisible = shouldShow
        binding.loserTraceCard.isVisible = shouldShow

        if (!shouldShow) return

        // Clear all views before binding
        binding.winnerTraceContainer.removeAllViews()
        binding.sellerTraceContainer.removeAllViews()
        binding.loserTraceContainer.removeAllViews()

        // Separate terms by role
        val winnerTerm = trace.terms.find { it.gamerId == trace.winnerId }
        val sellerTerms = trace.terms.filter { it.factors.isSeller && it.gamerId != trace.winnerId }
        val loserTerms = trace.terms.filter { it.gamerId != trace.winnerId && !it.factors.isSeller }

        // Bind UI for each role
        bindWinnerUi(state, winnerTerm)
        bindSellerUi(sellerTerms)
        bindLoserUi(loserTerms)
    }

    private fun bindWinnerUi(state: ResultUiState, winnerTerm: RoundTraceTerm?) {
        val trace = state.roundTrace ?: return
        if (winnerTerm == null) {
            binding.winnerTraceCard.isVisible = false
            return
        }

        val winnerInfo = state.gamers.find { it.id == trace.winnerId }
        val winnerName = winnerInfo?.name ?: "승자"

        val titleBinding = ViewTraceTitleBinding.inflate(layoutInflater, binding.winnerTraceContainer, false)
        titleBinding.tvTraceTotal.text = "승자 ($winnerName)"
        binding.winnerTraceContainer.addView(titleBinding.root)

        val winnerLines = roundTraceFormatter.toWinnerBreakdown(winnerTerm)
        winnerLines.forEach { line ->
            val itemBinding = ViewTraceTableRowBinding.inflate(layoutInflater, binding.winnerTraceContainer, false)
            itemBinding.tvPlayer.text = line.title
            itemBinding.tvFormula.text = ""
            itemBinding.tvAmount.text = line.amount
            binding.winnerTraceContainer.addView(itemBinding.root)
        }
    }

    private fun bindSellerUi(sellerTerms: List<RoundTraceTerm>) {
        if (sellerTerms.isEmpty()) {
            binding.sellerTraceCard.isVisible = false
            return
        }

        val headerBinding = ViewTraceTableHeaderBinding.inflate(layoutInflater, binding.sellerTraceContainer, false)
        binding.sellerTraceContainer.addView(headerBinding.root)

        val sellerLines = roundTraceFormatter.toLines(sellerTerms)
        sellerLines.forEach { line ->
            val itemBinding = ViewTraceTableRowBinding.inflate(layoutInflater, binding.sellerTraceContainer, false)
            itemBinding.tvPlayer.text = line.title
            itemBinding.tvFormula.text = line.formula
            itemBinding.tvAmount.text = line.amount
            binding.sellerTraceContainer.addView(itemBinding.root)
        }
    }

    private fun bindLoserUi(loserTerms: List<RoundTraceTerm>) {
        if (loserTerms.isEmpty()) {
            binding.loserTraceCard.isVisible = false
            return
        }

        val headerBinding = ViewTraceTableHeaderBinding.inflate(layoutInflater, binding.loserTraceContainer, false)
        binding.loserTraceContainer.addView(headerBinding.root)

        val loserLines = roundTraceFormatter.toLines(loserTerms)
        loserLines.forEach { line ->
            val itemBinding = ViewTraceTableRowBinding.inflate(layoutInflater, binding.loserTraceContainer, false)
            itemBinding.tvPlayer.text = line.title
            itemBinding.tvFormula.text = line.formula
            itemBinding.tvAmount.text = line.amount
            binding.loserTraceContainer.addView(itemBinding.root)
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
