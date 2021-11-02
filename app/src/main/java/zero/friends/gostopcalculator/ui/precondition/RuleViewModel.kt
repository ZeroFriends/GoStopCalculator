package zero.friends.gostopcalculator.ui.precondition

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import zero.friends.domain.model.Rule
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class RuleUiState(
    val currentTime: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(System.currentTimeMillis()),
    val rules: Rule = Rule(),
)

@HiltViewModel
class RuleViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(RuleUiState())
    fun getUiState() = _uiState.asStateFlow()

}
