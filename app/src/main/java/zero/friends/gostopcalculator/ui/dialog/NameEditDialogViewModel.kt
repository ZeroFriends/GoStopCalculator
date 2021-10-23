package zero.friends.gostopcalculator.ui.dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import timber.log.Timber
import zero.friends.domain.model.Player
import zero.friends.domain.usecase.EditPlayerUseCase
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class NameEditDialogViewModel @Inject constructor(
    private val editPlayerUseCase: EditPlayerUseCase,
) : ViewModel() {
    private val exceptionHandler =
        CoroutineExceptionHandler { _: CoroutineContext, throwable: Throwable ->
            Timber.tag("zero1(handler)").e(throwable)
        }

    fun editPlayer(originalPlayer: Player, editPlayer: Player) {
        viewModelScope.launch(exceptionHandler) {
            editPlayerUseCase.invoke(originalPlayer, editPlayer)
        }
    }
}