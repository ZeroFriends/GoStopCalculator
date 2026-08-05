package zero.friends.gostopcalculator.ui.history.guide

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import me.tatarka.inject.annotations.Inject
import org.jetbrains.compose.resources.DrawableResource
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.guide_1
import zero.friends.gostopcalculator.shared.generated.resources.guide_2
import zero.friends.gostopcalculator.shared.generated.resources.guide_3
import zero.friends.gostopcalculator.shared.generated.resources.guide_4
import zero.friends.gostopcalculator.shared.generated.resources.guide_5
import zero.friends.gostopcalculator.shared.generated.resources.guide_6

data class GuideUiState(
    val pagerList: List<DrawableResource> = listOf(
        Res.drawable.guide_1,
        Res.drawable.guide_2,
        Res.drawable.guide_3,
        Res.drawable.guide_4,
        Res.drawable.guide_5,
        Res.drawable.guide_6
    )
)

@Inject
class GuideViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(GuideUiState())
    fun uiState() = _uiState.asStateFlow()
}