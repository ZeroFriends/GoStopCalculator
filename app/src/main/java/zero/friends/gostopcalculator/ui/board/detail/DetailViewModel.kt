package zero.friends.gostopcalculator.ui.board.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import zero.friends.domain.model.Game
import zero.friends.domain.model.Gamer
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.GamerRepository
import zero.friends.gostopcalculator.di.factory.DetailViewModelFactory
import zero.friends.gostopcalculator.util.viewModelFactory

data class DetailUiState(
    val game: Game = Game(),
    val gamers: List<Gamer> = emptyList()
)

class DetailViewModel @AssistedInject constructor(
    @Assisted private val roundId: Long,
    private val gamerRepository: GamerRepository,
    private val gameRepository: GameRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(DetailUiState())
    fun uiState() = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    game = requireNotNull(gameRepository.getCurrentGame()),
                    gamers = gamerRepository.getRoundGamers(roundId = roundId)
                )
            }
        }
    }

    companion object {
        fun provideFactory(
            detailViewModelFactory: DetailViewModelFactory,
            roundId: Long
        ): ViewModelProvider.Factory = viewModelFactory { detailViewModelFactory.createDetailViewModel(roundId) }
    }
}