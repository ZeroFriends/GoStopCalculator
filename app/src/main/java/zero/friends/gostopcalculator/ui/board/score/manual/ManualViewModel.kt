package zero.friends.gostopcalculator.ui.board.score.manual

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import zero.friends.domain.model.Manual
import zero.friends.domain.repository.ManualRepository
import zero.friends.gostopcalculator.R
import javax.inject.Inject

data class ManualUiState(
    val manuals: List<Manual> = emptyList(),
    val images: List<Int> = listOf(
        R.drawable.five_shine,
        R.drawable.four_shine,
        R.drawable.three_shine,
        R.drawable.bee_shine,
        R.drawable.animals,
        R.drawable.dan,
        R.drawable.hongdan,
        R.drawable.cheongdan,
        R.drawable.chodan,
        R.drawable.godori,
        R.drawable.pees,
    )
)

@HiltViewModel
class ManualViewModel @Inject constructor(
    private val manualRepository: ManualRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ManualUiState())
    fun uiState() = _uiState.asStateFlow()


    init {
        _uiState.update {
            val manuals = manualRepository.getManuals()
            it.copy(manuals = manuals)
        }
    }
}