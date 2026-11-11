package zero.friends.gostopcalculator.ui.board.score.manual

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import org.jetbrains.compose.resources.DrawableResource
import zero.friends.domain.model.Manual
import zero.friends.domain.repository.ManualRepository
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.animals
import zero.friends.gostopcalculator.shared.generated.resources.bee_shine
import zero.friends.gostopcalculator.shared.generated.resources.cheongdan
import zero.friends.gostopcalculator.shared.generated.resources.chodan
import zero.friends.gostopcalculator.shared.generated.resources.dan
import zero.friends.gostopcalculator.shared.generated.resources.five_shine
import zero.friends.gostopcalculator.shared.generated.resources.four_shine
import zero.friends.gostopcalculator.shared.generated.resources.godori
import zero.friends.gostopcalculator.shared.generated.resources.hongdan
import zero.friends.gostopcalculator.shared.generated.resources.pees
import zero.friends.gostopcalculator.shared.generated.resources.three_shine

data class ManualUiState(
    val manuals: List<Manual> = emptyList(),
    val images: List<DrawableResource> = listOf(
        Res.drawable.five_shine,
        Res.drawable.four_shine,
        Res.drawable.three_shine,
        Res.drawable.bee_shine,
        Res.drawable.animals,
        Res.drawable.dan,
        Res.drawable.hongdan,
        Res.drawable.cheongdan,
        Res.drawable.chodan,
        Res.drawable.godori,
        Res.drawable.pees,
    )
)

@Inject
class ManualViewModel(
    private val manualRepository: ManualRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ManualUiState())
    fun uiState() = _uiState.asStateFlow()

    private var loadJob: Job? = null
    private var loadedFileName: String? = null

    fun loadManuals(fileName: String) {
        if (loadedFileName == fileName && _uiState.value.manuals.isNotEmpty()) return
        loadJob?.cancel()
        loadedFileName = fileName
        loadJob = viewModelScope.launch {
            manualRepository.getManuals(fileName)
                .catch {
                    loadedFileName = null
                    println("Manual error: $it")
                }
                .collectLatest { manuals ->
                    _uiState.update {
                        it.copy(manuals = manuals)
                    }
                }
        }
    }
}
