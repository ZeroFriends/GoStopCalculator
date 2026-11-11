package zero.friends.gostopcalculator.ui.board.score

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import org.jetbrains.compose.resources.getString
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.Game
import zero.friends.domain.model.LoserOption
import zero.friends.domain.model.ScoreOption
import zero.friends.domain.model.SellerOption
import zero.friends.domain.model.WinnerOption
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.GamerRepository
import zero.friends.domain.repository.RoundRepository
import zero.friends.domain.usecase.calculate.CalculateGameResultUseCase
import zero.friends.domain.usecase.option.SellingUseCase
import zero.friends.domain.usecase.option.ToggleLoserOptionUseCase
import zero.friends.domain.usecase.option.ToggleScoreOptionUseCase
import zero.friends.domain.usecase.option.UpdateWinnerUseCase
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.max_sell_count_alert
import zero.friends.gostopcalculator.shared.generated.resources.over_point_alert
import zero.friends.gostopcalculator.util.separateComma

data class ScoreUiState(
    val game: Game = Game(),
    val playerResults: List<Gamer> = emptyList(),
    val phase: Phase = Selling(false),
    val seller: Gamer? = null,
    val winner: Gamer? = null,
    val threeFuckGamer: Gamer? = null
)

@Inject
class ScoreViewModel(
    private val gameRepository: GameRepository,
    private val toggleScoreOptionUseCase: ToggleScoreOptionUseCase,
    private val toggleLoserOption: ToggleLoserOptionUseCase,
    private val updateWinnerUseCase: UpdateWinnerUseCase,
    private val calculateGameResultUseCase: CalculateGameResultUseCase,
    private val sellingUseCase: SellingUseCase,
    private val roundRepository: RoundRepository,
    private val gamerRepository: GamerRepository,
    @Assisted val gameId: Long,
    @Assisted val roundId: Long
) : ViewModel() {

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
                    game = gameRepository.getGame(gameId),
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
                        else -> throw IllegalStateException("해당 페이즈가 존재하지 않습니다: ${it.phase}")
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
                            else -> throw IllegalStateException("해당 페이즈가 존재하지 않습니다: ${state.phase}")
                        },
                        playerResults = when (state.phase) {
                            is Scoring -> state.playerResults.map {
                                if (state.seller?.id == it.id) {
                                    it.copy(score = state.seller.score, go = 0)
                                } else {
                                    it.copy(score = 0, go = 0)
                                }
                            }
                            is Winner -> state.playerResults
                            Loser -> state.playerResults.map {
                                if (state.winner?.id == it.id) {
                                    it.copy(score = state.winner.score, go = state.winner.go)
                                } else {
                                    it.copy(score = 0, go = 0)
                                }
                            }
                            else -> throw IllegalStateException("해당 페이즈가 존재하지 않습니다: ${state.phase}")
                        }
                    )
                }
            }
        }
    }

    fun updateSeller(seller: Gamer, count: Long) {
        viewModelScope.launch {
            if (count > MAX_SELL_COUNT) {
                _toast.emit(getString(Res.string.max_sell_count_alert, MAX_SELL_COUNT.toInt()))
                return@launch
            }
            _uiState.update { state ->
                val newGamers = state.playerResults.map {
                    if (it.id == seller.id) {
                        it.copy(score = count.toInt(), go = 0)
                    } else {
                        it.copy(score = 0, go = 0)
                    }
                }
                state.copy(
                    phase = Selling(newGamers.any { it.score != 0 }),
                    seller = if (count != 0L) seller.copy(
                        score = count.toInt(),
                        go = 0,
                        sellerOption = SellerOption.Seller
                    ) else null,
                    playerResults = newGamers
                )
            }
        }
    }

    fun updateWinner(gamer: Gamer, point: Long, go: Long) {
        viewModelScope.launch {
            if (point > MAX_POINT) {
                _toast.emit(getString(Res.string.over_point_alert, MAX_POINT.separateComma()))
                return@launch
            }
            _uiState.update { state ->
                val goValue = go.toInt()
                    val newGamers = state.playerResults.map {
                        if (it.id == gamer.id) {
                            it.copy(score = point.toInt(), go = goValue)
                        } else {
                            it.copy(score = 0, go = 0)
                        }
                    }
                    val hasWinnerInput = newGamers.any { it.score != 0 || it.go != 0 }
                    state.copy(
                        phase = Winner(hasWinnerInput),
                        winner = if (hasWinnerInput) gamer.copy(
                            score = point.toInt(),
                            go = goValue,
                            winnerOption = WinnerOption.Winner
                        ) else null,
                        playerResults = newGamers
                    )
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
