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
import zero.friends.domain.usecase.player.EditPlayerUseCase
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

data class DialogState(
    val originalPlayer: Player = Player(),
    val editPlayer: Player? = null,
    val error: Throwable? = null,
)

@HiltViewModel
class NameEditDialogViewModel @Inject constructor(
    private val editPlayerUseCase: EditPlayerUseCase
) : ViewModel() {
    private val _dialogState = MutableStateFlow(DialogState())
    fun getDialogState() = _dialogState.asStateFlow()

    private val editNameExceptionHandler =
        CoroutineExceptionHandler { _: CoroutineContext, throwable: Throwable ->
            _dialogState.update { it.copy(error = throwable) }
        }

    fun editPlayer(originalPlayer: Player, name: String) {
        viewModelScope.launch(editNameExceptionHandler) {
            val editPlayer = if (name.isEmpty()) originalPlayer else originalPlayer.copy(name = name)
            editPlayerUseCase(originalPlayer, editPlayer)
        }
    }

}