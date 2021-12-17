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
import zero.friends.domain.usecase.SellingUseCase
import zero.friends.gostopcalculator.di.factory.SellingViewModelFactory
import zero.friends.gostopcalculator.util.viewModelFactory

data class SellingUiState(
    val game: Game = Game(),
    val gamers: List<Gamer> = emptyList(),
    val seller: Gamer? = null
)

class SellingViewModel @AssistedInject constructor(
    @Assisted private val roundId: Long,
    private val gamerRepository: GamerRepository,
    private val sellingUseCase: SellingUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(SellingUiState())
    fun uiState() = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update {
                val gamers = gamerRepository.getRoundGamers(roundId)
                it.copy(gamers = gamers)
            }
        }
    }

    fun onSaveSeller(seller: Gamer, count: Int) {
        _uiState.update {
            val hasSeller = count != 0
            val target = if (hasSeller) seller else null
            it.copy(seller = target)
        }
    }

    fun complete() {
        viewModelScope.launch {
            val seller = uiState().value.seller
            check(seller != null) { "광판 사람은 무조건 존재해야 함" }
            sellingUseCase.invoke(seller)
        }
    }

    companion object {
        fun provideFactory(
            sellingViewModelFactory: SellingViewModelFactory,
            roundId: Long
        ): ViewModelProvider.Factory = viewModelFactory { sellingViewModelFactory.createSellingViewModel(roundId) }
    }
}
