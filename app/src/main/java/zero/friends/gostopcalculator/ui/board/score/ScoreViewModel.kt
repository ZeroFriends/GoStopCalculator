package zero.friends.gostopcalculator.ui.board.score

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import zero.friends.domain.model.Game
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.LoserOption
import zero.friends.domain.model.ScoreOption
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.usecase.gamer.GetRoundGamerUseCase
import zero.friends.domain.usecase.option.ToggleLoserOptionUseCase
import zero.friends.domain.usecase.option.ToggleScoreOptionUseCase
import zero.friends.domain.usecase.option.UpdateWinnerUseCase
import zero.friends.domain.usecase.round.ObserveRoundGamerUseCase
import javax.inject.Inject

data class ScoreUiState(
    val game: Game = Game(),
    val playerResults: List<Gamer> = emptyList(),
    val phase: Phase = Scoring(),
    val seller: Gamer? = null,//todo 판매자, 승자 정보 어떻게 가지고 올건지???
    val winner: Gamer? = null
)

@HiltViewModel
class ScoreViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val getRoundGamerUseCase: GetRoundGamerUseCase,
    private val toggleScoreOptionUseCase: ToggleScoreOptionUseCase,
    private val toggleLoserOptionUseCase: ToggleLoserOptionUseCase,
    private val observeRoundGamerUseCase: ObserveRoundGamerUseCase,
    private val updateWinnerUseCase: UpdateWinnerUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ScoreUiState())
    fun uiState() = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val gamers = getRoundGamerUseCase()
            val seller = gamers.firstOrNull { it.sellerOption != null }
            _uiState.update {
                it.copy(
                    game = requireNotNull(gameRepository.getCurrentGame()),
                    playerResults = gamers,
                    phase = Scoring(),
                    seller = seller
                )
            }

            observeRoundGamerUseCase()
                .onEach { roundGamers ->
                    _uiState.update {
                        it.copy(
                            playerResults = roundGamers
                        )
                    }
                }.launchIn(this)


        }

    }

    fun selectScore(gamer: Gamer, option: ScoreOption) {
        viewModelScope.launch {
            toggleScoreOptionUseCase(gamer, option)
        }
    }

    fun selectLoser(gamer: Gamer, option: LoserOption) {
        viewModelScope.launch {
            toggleLoserOptionUseCase(gamer, option)
        }
    }

    fun onNext() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    phase = when (it.phase) {
                        is Scoring -> Winner(false)
                        is Winner -> {
                            updateWinnerUseCase.invoke(requireNotNull(_uiState.value.winner))
                            Loser()
                        }
                        is Loser -> Loser()
                        else -> throw IllegalStateException("")
                    }
                )
            }
        }
    }

    fun updateWinner(gamer: Gamer, point: Int) {
        _uiState.update {
            it.copy(
                phase = Winner(point != 0),
                winner = if (point != 0) gamer.copy(account = point) else null
            )
        }
    }

}