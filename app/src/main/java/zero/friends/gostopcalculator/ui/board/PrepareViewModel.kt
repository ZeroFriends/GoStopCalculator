package zero.friends.gostopcalculator.ui.board

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import zero.friends.domain.model.Game
import zero.friends.domain.repository.GameRepository
import zero.friends.gostopcalculator.di.entrypoint.EntryPoint
import zero.friends.gostopcalculator.di.factory.PrePareViewModelFactory
import zero.friends.gostopcalculator.util.getEntryPointFromActivity
import zero.friends.gostopcalculator.util.viewModelFactory

data class PrepareUiState(
    val game: Game = Game()
)

@Composable
fun createPrepareViewModel(gameId: Long): PrepareViewModel {
    val entryPoint = getEntryPointFromActivity<EntryPoint>()
    val factory = entryPoint.prepareFactory()
    return viewModel(factory = PrepareViewModel.provideFactory(prePareViewModelFactory = factory, gameId = gameId))
}

class PrepareViewModel @AssistedInject constructor(
    @Assisted private val gameId: Long,
    private val gameRepository: GameRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(PrepareUiState())
    fun uiState() = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update {
                it.copy(game = gameRepository.getGame(gameId = gameId))
            }
        }
    }

    companion object {
        fun provideFactory(
            prePareViewModelFactory: PrePareViewModelFactory,
            gameId: Long
        ): ViewModelProvider.Factory = viewModelFactory { prePareViewModelFactory.createPrepareViewModel(gameId) }
    }
}
