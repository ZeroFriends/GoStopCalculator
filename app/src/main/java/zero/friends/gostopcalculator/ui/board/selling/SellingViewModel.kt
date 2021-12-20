package zero.friends.gostopcalculator.ui.board.selling

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import zero.friends.domain.model.Game
import zero.friends.domain.model.Gamer
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.usecase.GetRoundGamerUseCase
import zero.friends.domain.usecase.SellingUseCase
import javax.inject.Inject

data class SellingUiState(
    val game: Game = Game(),
    val gamers: List<Gamer> = emptyList(),
    val seller: Gamer? = null
)

@HiltViewModel
class SellingViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val getRoundGamerUseCase: GetRoundGamerUseCase,
    private val sellingUseCase: SellingUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(SellingUiState())
    fun uiState() = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            gameRepository.getCurrentGame()
            _uiState.update {
                val gamers = getRoundGamerUseCase()
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

}
