package zero.friends.gostopcalculator.ui.history.guide

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import zero.friends.gostopcalculator.R
import javax.inject.Inject

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

@HiltViewModel
class GuideViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(GuideUiState())
    fun uiState() = _uiState.asStateFlow()
}