package zero.friends.gostopcalculator.ui.dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import zero.friends.domain.model.Player
import zero.friends.domain.usecase.player.EditPlayerUseCase
import javax.inject.Inject

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

    fun editPlayer(originalPlayer: Player, name: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            runCatching {
                val editPlayer = if (name.isEmpty()) originalPlayer else originalPlayer.copy(name = name)
                editPlayerUseCase(originalPlayer, editPlayer)
            }
                .onSuccess { onSuccess() }
                .onFailure { t ->
                    _dialogState.update { it.copy(error = t) }
                }
        }
    }

    fun clearState() {
        _dialogState.update {
            it.copy(error = null)
        }
    }

}