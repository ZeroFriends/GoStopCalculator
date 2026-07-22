package zero.friends.gostopcalculator.ui.board.score.manual

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
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
        viewModelScope.launch {
            manualRepository.getManuals()
                .catch { Timber.e(it) } //ignore exception
                .collectLatest { manuals ->
                    _uiState.update {
                        it.copy(manuals = manuals)
                    }
                }
        }
    }
}