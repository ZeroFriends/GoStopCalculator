package zero.friends.gostopcalculator.ui.board.selling

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
import zero.friends.domain.repository.GamerRepository
import zero.friends.gostopcalculator.di.factory.SellingViewModelFactory
import zero.friends.gostopcalculator.util.viewModelFactory

data class SellingUiState(
    val game: Game = Game(),
    val gamers: List<Gamer> = emptyList()
)

class SellingViewModel @AssistedInject constructor(
    @Assisted private val roundId: Long,
    private val gamerRepository: GamerRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SellingUiState())
    fun uiState() = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update {
                it.copy(gamers = gamerRepository.getRoundGamers(roundId))
            }
        }
    }

    companion object {
        fun provideFactory(
            sellingViewModelFactory: SellingViewModelFactory,
            roundId: Long
        ): ViewModelProvider.Factory = viewModelFactory { sellingViewModelFactory.createSellingViewModel(roundId) }
    }
}
