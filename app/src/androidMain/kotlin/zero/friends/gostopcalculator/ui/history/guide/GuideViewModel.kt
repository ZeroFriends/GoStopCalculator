package zero.friends.gostopcalculator.ui.history.guide

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import me.tatarka.inject.annotations.Inject
import zero.friends.gostopcalculator.R

data class GuideUiState(
    val pagerList: List<Int> = listOf(
        R.drawable.guide_1,
        R.drawable.guide_2,
        R.drawable.guide_3,
        R.drawable.guide_4,
        R.drawable.guide_5,
        R.drawable.guide_6,
    )
)

@Inject
class GuideViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(GuideUiState())
    fun uiState() = _uiState.asStateFlow()
}