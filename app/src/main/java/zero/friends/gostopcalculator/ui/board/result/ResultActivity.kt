package zero.friends.gostopcalculator.ui.board.result

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.graphics.createBitmap
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import zero.friends.domain.model.RoundTraceTerm
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.databinding.ActivityResultBinding
import zero.friends.gostopcalculator.databinding.ViewTraceTableHeaderBinding
import zero.friends.gostopcalculator.databinding.ViewTraceTableRowBinding
import zero.friends.gostopcalculator.databinding.ViewTraceTitleBinding
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private val adapter = CalculatedGamerAdapter()

    @Inject
    lateinit var roundTraceFormatter: RoundTraceFormatter

    private val viewModel: ResultViewModel by viewModels()

    private var lastSharedFile: File? = null

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
        WindowCompat.getInsetsController(window, window.decorView)?.apply {
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

    override fun onResume() {
        super.onResume()
        // 공유 후 돌아왔을 때 파일 삭제
        lastSharedFile?.delete()
        lastSharedFile = null
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
            captureAndShare()
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
        
        binding.winnerTraceContainer.isVisible = shouldShow
        binding.sellerTraceContainer.isVisible = shouldShow
        binding.loserTraceContainer.isVisible = shouldShow

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
            binding.winnerTraceContainer.isVisible = false
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
            binding.sellerTraceContainer.isVisible = false
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
            binding.loserTraceContainer.isVisible = false
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

    private fun captureAndShare() {
        lifecycleScope.launch {
            try {
                val bitmap = captureScrollView()
                val (file, uri) = saveBitmapToCache(bitmap)
                
                lastSharedFile = file

                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/png"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                startActivity(Intent.createChooser(shareIntent, getString(R.string.share)))
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Toast.makeText(this@ResultActivity, "공유에 실패하였습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun captureScrollView(): Bitmap = withContext(Dispatchers.Main) {
        val contentLayout = binding.contentLayout
        val scrollView = binding.scrollView

        val scrollX = scrollView.scrollX
        val scrollY = scrollView.scrollY
        scrollView.scrollTo(0, 0)

        binding.recyclerView.requestLayout()

        val widthSpec = View.MeasureSpec.makeMeasureSpec(scrollView.width, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        contentLayout.measure(widthSpec, heightSpec)

        contentLayout.layout(0, 0, contentLayout.measuredWidth, contentLayout.measuredHeight)

        val bitmap = createBitmap(contentLayout.measuredWidth, contentLayout.measuredHeight)
        val canvas = Canvas(bitmap)
        
        canvas.drawColor(getColor(R.color.light_gray))
        
        contentLayout.draw(canvas)

        scrollView.scrollTo(scrollX, scrollY)

        return@withContext bitmap
    }

    private suspend fun saveBitmapToCache(bitmap: Bitmap) = withContext(Dispatchers.IO) {
        val file = File(cacheDir, "result_share.png")

        file.outputStream().use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
        }

        val uri = FileProvider.getUriForFile(this@ResultActivity, AUTHORITY, file)
        
        return@withContext Pair(file, uri)
    }

    enum class ScreenType {
        CALCULATE,
        DETAIL
    }

    companion object {
        const val EXTRA_GAME_ID = "extra_game_id"
        const val EXTRA_ROUND_ID = "extra_round_id"
        const val EXTRA_SCREEN_TYPE = "extra_screen_type"
        private const val AUTHORITY = "zerofriends"

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
