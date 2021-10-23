package zero.friends.gostopcalculator.ui.dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import zero.friends.domain.model.Player
import zero.friends.domain.usecase.EditPlayerUseCase
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

data class NameEditUiState(
    val modify: Boolean = false,
    val error: Throwable? = null,
)

@HiltViewModel
class NameEditDialogViewModel @Inject constructor(
    private val editPlayerUseCase: EditPlayerUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(NameEditUiState())
    fun getUiState() = _uiState.asStateFlow()

    private val exceptionHandler =
        CoroutineExceptionHandler { _: CoroutineContext, throwable: Throwable ->
            _uiState.update { getUiState().value.copy(modify = false, error = throwable) }
        }

    fun editPlayer(originalPlayer: Player, editPlayer: Player) {
        viewModelScope.launch(exceptionHandler) {
            editPlayerUseCase.invoke(originalPlayer, editPlayer)
            _uiState.update { getUiState().value.copy(modify = true) }
        }
    }

}