package zero.friends.gostopcalculator.ui.board.score

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import zero.friends.domain.model.*
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.usecase.calculate.CalculateGameResultUseCase
import zero.friends.domain.usecase.gamer.GetRoundGamerUseCase
import zero.friends.domain.usecase.option.SellingUseCase
import zero.friends.domain.usecase.option.ToggleLoserOptionUseCase
import zero.friends.domain.usecase.option.ToggleScoreOptionUseCase
import zero.friends.domain.usecase.option.UpdateWinnerUseCase
import zero.friends.domain.usecase.round.DeleteRoundUseCase
import zero.friends.domain.usecase.round.ObserveRoundGamerUseCase
import javax.inject.Inject

data class ScoreUiState(
    val game: Game = Game(),
    val playerResults: List<Gamer> = emptyList(),
    val phase: Phase = Selling(false),
    val seller: Gamer? = null,
    val winner: Gamer? = null,
    val threeFuckGamer: Gamer? = null
)

@HiltViewModel
class ScoreViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val getRoundGamerUseCase: GetRoundGamerUseCase,
    private val toggleScoreOptionUseCase: ToggleScoreOptionUseCase,
    private val toggleLoserOptionUseCase: ToggleLoserOptionUseCase,
    private val observeRoundGamerUseCase: ObserveRoundGamerUseCase,
    private val updateWinnerUseCase: UpdateWinnerUseCase,
    private val calculateGameResultUseCase: CalculateGameResultUseCase,
    private val sellingUseCase: SellingUseCase,
    private val deleteRoundUseCase: DeleteRoundUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ScoreUiState())
    fun uiState() = _uiState.asStateFlow()

    private val _escapeEvent = MutableSharedFlow<Unit>()
    fun escapeEvent() = _escapeEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            val gamers = getRoundGamerUseCase()
            _uiState.update {
                it.copy(
                    game = requireNotNull(gameRepository.getCurrentGame()),
                    playerResults = gamers,
                    phase = if (gamers.count() != 4) Scoring else Selling(false),
                )
            }

            observeRoundGamerUseCase()
                .onEach { roundGamers ->
                    _uiState.update {
                        it.copy(playerResults = roundGamers)
                    }
                }.launchIn(this)

        }

    }

    fun selectScore(gamer: Gamer, option: ScoreOption, checkThreeFuck: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            toggleScoreOptionUseCase(
                gamer = gamer,
                option = option,
                checkThreeFuck = checkThreeFuck.also {
                    _uiState.update { it.copy(threeFuckGamer = gamer.copy(scoreOption = listOf(ScoreOption.ThreeFuck))) }
                }
            )
        }
    }

    fun selectLoser(gamer: Gamer, option: LoserOption) {
        viewModelScope.launch {
            toggleLoserOptionUseCase(gamer, option)
        }
    }

    fun onNextPhase() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    phase = when (it.phase) {
                        is Selling -> Scoring
                        is Scoring -> Winner(false)
                        is Winner -> Loser
                        else -> throw IllegalStateException("없는 페이즈 입니다. ${it.phase}")
                    }
                )
            }
        }
    }


    fun onBackPhase() {
        viewModelScope.launch {
            if (uiState().value.phase is Selling) {
                _escapeEvent.emit(Unit)
            } else if (uiState().value.phase is Scoring && uiState().value.playerResults.size != 4) {
                _escapeEvent.emit(Unit)
            } else {
                _uiState.update {
                    it.copy(
                        phase = when (it.phase) {
                            is Scoring -> Selling(true)
                            is Winner -> Scoring
                            is Loser -> Winner()
                            else -> throw IllegalStateException("없는 페이즈 입니다. ${it.phase}")
                        }
                    )
                }
            }
        }
    }

    fun updateWinner(gamer: Gamer, point: Int) {
        _uiState.update {
            it.copy(
                phase = Winner(point != 0),
                winner = if (point != 0) gamer.copy(score = point, winnerOption = WinnerOption.Winner) else null,
            )
        }
    }

    fun updateSeller(seller: Gamer, count: Int) {
        _uiState.update {
            val hasSeller = count != 0
            val target = if (hasSeller) seller.copy(score = count, sellerOption = SellerOption.Seller) else null
            it.copy(
                phase = Selling(hasSeller),
                seller = target
            )
        }
    }

    fun calculateGameResult() {
        viewModelScope.launch {
            _uiState.value.seller?.let { sellingUseCase.invoke(it) }
            val winner = _uiState.value.winner
            if (winner != null) updateWinnerUseCase.invoke(winner)
            calculateGameResultUseCase.invoke(
                seller = uiState().value.seller,
                winner = uiState().value.winner
            )
        }
    }

    fun deleteRound() {
        viewModelScope.launch {
            deleteRoundUseCase()
        }
    }

}