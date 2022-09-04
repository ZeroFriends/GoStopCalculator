package zero.friends.gostopcalculator.ui.board.score

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import zero.friends.domain.model.*
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.GamerRepository
import zero.friends.domain.repository.RoundRepository
import zero.friends.domain.usecase.calculate.CalculateGameResultUseCase
import zero.friends.domain.usecase.option.SellingUseCase
import zero.friends.domain.usecase.option.ToggleLoserOptionUseCase
import zero.friends.domain.usecase.option.ToggleScoreOptionUseCase
import zero.friends.domain.usecase.option.UpdateWinnerUseCase
import zero.friends.domain.util.Const
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.util.separateComma
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
    @ApplicationContext private val context: Context,
    private val gameRepository: GameRepository,
    private val toggleScoreOptionUseCase: ToggleScoreOptionUseCase,
    private val toggleLoserOption: ToggleLoserOptionUseCase,
    private val updateWinnerUseCase: UpdateWinnerUseCase,
    private val calculateGameResultUseCase: CalculateGameResultUseCase,
    private val sellingUseCase: SellingUseCase,
    private val roundRepository: RoundRepository,
    private val gamerRepository: GamerRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val roundId: Long = requireNotNull(savedStateHandle[Const.RoundId])

    private val _uiState = MutableStateFlow(ScoreUiState())
    fun uiState() = _uiState.asStateFlow()

    private val _escapeEvent = MutableSharedFlow<Unit>()
    fun escapeEvent() = _escapeEvent.asSharedFlow()

    private val _toast = MutableSharedFlow<String?>()
    fun toast() = _toast.asSharedFlow()

    init {
        viewModelScope.launch {
            val gamers = gamerRepository.getRoundGamers(roundId)
            _uiState.update {
                it.copy(
                    game = gameRepository.getGame(requireNotNull(savedStateHandle[Const.GameId])),
                    playerResults = gamers,
                    phase = if (gamers.count() != 4) Scoring else Selling(false),
                )
            }

            gamerRepository.observeRoundGamers(roundId)
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
                checkThreeFuck = checkThreeFuck
            )
            _uiState.update { it.copy(threeFuckGamer = gamer.copy(scoreOption = listOf(ScoreOption.ThreeFuck))) }
        }
    }

    fun selectLoser(gamer: Gamer, option: LoserOption) {
        viewModelScope.launch {
            toggleLoserOption(gamer.id, option)
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
                        else -> throw IllegalStateException("${context.getString(R.string.error_msg_phase_not_exist)} ${it.phase}")
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
                _uiState.update { state ->
                    state.copy(
                        phase = when (state.phase) {
                            is Scoring -> Selling(true)
                            is Winner -> Scoring
                            Loser -> Winner(true)
                            else -> throw IllegalStateException("${context.getString(R.string.error_msg_phase_not_exist)} ${state.phase}")
                        },
                        playerResults = when (state.phase) {
                            is Scoring -> state.playerResults.map {
                                if (state.seller?.id == it.id) {
                                    it.copy(score = state.seller.score)
                                } else {
                                    it.copy(score = 0)
                                }

                            }
                            is Winner -> state.playerResults
                            Loser -> state.playerResults.map {
                                if (state.winner?.id == it.id) {
                                    it.copy(score = state.winner.score)
                                } else {
                                    it.copy(score = 0)
                                }
                            }
                            else -> throw IllegalStateException("${context.getString(R.string.error_msg_phase_not_exist)} ${state.phase}")
                        }
                    )
                }
            }
        }
    }


    fun updateSeller(seller: Gamer, count: Long) {
        viewModelScope.launch {
            runCatching {
                require(count <= MAX_SELL_COUNT) {
                    context.getString(R.string.over_page_alert, MAX_SELL_COUNT)
                }
            }.onFailure {
                _toast.emit(it.message)
            }.onSuccess {
                _uiState.update { state ->
                    val newGamers = state.playerResults.map {
                        if (it.name == seller.name) {
                            it.copy(score = count.toInt())
                        } else {
                            it.copy(score = 0)
                        }
                    }
                    state.copy(
                        phase = Selling(newGamers.any { it.score != 0 }),
                        seller = if (count != 0L) seller.copy(
                            score = count.toInt(),
                            sellerOption = SellerOption.Seller
                        ) else null,
                        playerResults = newGamers
                    )
                }
            }
        }
    }

    fun updateWinner(gamer: Gamer, point: Long) {
        viewModelScope.launch {
            runCatching {
                require(point <= MAX_POINT) {
                    context.getString(R.string.over_point_alert, MAX_POINT.separateComma())
                }
            }.onFailure {
                _toast.emit(it.message)
            }.onSuccess {
                _uiState.update { state ->
                    val newGamers = state.playerResults.map {
                        if (it.name == gamer.name) {
                            it.copy(score = point.toInt())
                        } else {
                            it.copy(score = 0)
                        }
                    }
                    state.copy(
                        phase = Winner(newGamers.any { it.score != 0 }),
                        winner = if (point != 0L) gamer.copy(
                            score = point.toInt(),
                            winnerOption = WinnerOption.Winner
                        ) else null,
                        playerResults = newGamers
                    )
                }
            }
        }
    }

    fun calculateGameResult() {
        viewModelScope.launch {
            val seller = _uiState.value.seller
            if (seller != null) sellingUseCase.invoke(seller)

            val winner = _uiState.value.winner
            if (winner != null) updateWinnerUseCase.invoke(winner)

            calculateGameResultUseCase.invoke(
                gameId = uiState().value.game.id,
                roundId = roundId,
                seller = uiState().value.seller,
                winner = uiState().value.winner
            )
        }
    }

    fun deleteRound() {
        viewModelScope.launch {
            roundRepository.deleteRound(roundId)
        }
    }

    companion object {
        private const val MAX_SELL_COUNT = 12
        private const val MAX_POINT = 8_519_680
    }
}