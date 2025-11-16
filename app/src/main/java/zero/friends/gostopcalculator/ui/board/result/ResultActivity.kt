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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.databinding.ActivityResultBinding
import java.io.File

@AndroidEntryPoint
class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private val adapter = CalculatedGamerAdapter()

    private val viewModel: ResultViewModel by viewModels()

    private val gameId: Long by lazy { intent.getLongExtra(EXTRA_GAME_ID, -1L) }
    private val roundId: Long by lazy { intent.getLongExtra(EXTRA_ROUND_ID, -1L) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        observeViewModel()
        loadData()
    }

    private fun setupViews() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ResultActivity)
            adapter = this@ResultActivity.adapter
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
            }
        }
    }

    private fun loadData() {
        when (viewModel.uiState.value.screenType) {
            ScreenType.CALCULATE -> viewModel.loadCalculateData(gameId)
            ScreenType.DETAIL -> viewModel.loadDetailData(gameId, roundId)
        }
    }

    private fun captureAndShare() {
        lifecycleScope.launch {
            try {
                val bitmap = captureScrollView()
                val uri = saveBitmapToCache(bitmap)

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

    private suspend fun captureScrollView(): Bitmap = withContext(Dispatchers.Default) {
        val scrollView = binding.scrollView
        val contentLayout = binding.contentLayout

        // 스크롤뷰의 전체 컨텐츠 크기 측정
        contentLayout.measure(
            View.MeasureSpec.makeMeasureSpec(scrollView.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )

        val bitmap = createBitmap(contentLayout.measuredWidth, contentLayout.measuredHeight)

        val canvas = Canvas(bitmap)

        // 현재 스크롤 위치 저장
        val scrollX = scrollView.scrollX
        val scrollY = scrollView.scrollY

        // 스크롤을 맨 위로
        withContext(Dispatchers.Main) {
            scrollView.scrollTo(0, 0)
        }

        // 레이아웃 그리기
        contentLayout.layout(
            0,
            0,
            contentLayout.measuredWidth,
            contentLayout.measuredHeight
        )
        contentLayout.draw(canvas)

        // 원래 스크롤 위치로 복원
        withContext(Dispatchers.Main) {
            scrollView.scrollTo(scrollX, scrollY)
        }

        bitmap
    }

    private suspend fun saveBitmapToCache(bitmap: Bitmap) = withContext(Dispatchers.IO) {
        val cacheDir = File(cacheDir, "images").apply { mkdirs() }
        val file = File(cacheDir, "result_${System.currentTimeMillis()}.png")

        file.outputStream().use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
        }

        FileProvider.getUriForFile(
            this@ResultActivity,
            AUTHORITY,
            file
        )
    }

    enum class ScreenType {
        CALCULATE,
        DETAIL
    }

    companion object {
        private const val EXTRA_GAME_ID = "extra_game_id"
        private const val EXTRA_ROUND_ID = "extra_round_id"
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

