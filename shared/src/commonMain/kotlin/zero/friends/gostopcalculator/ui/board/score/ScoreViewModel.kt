package zero.friends.gostopcalculator.ui.board.score

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
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
import zero.friends.domain.usecase.calculate.MAX_WINNER_SPECIAL_MULTIPLIER_COUNT
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

    private val _escapeEvent = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    fun escapeEvent() = _escapeEvent.asSharedFlow()

    private val _toast = MutableSharedFlow<String?>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    fun toast() = _toast.asSharedFlow()

    private val actionMutex = Mutex()
    private var isSettling = false
    private var isClosed = false

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
                    _uiState.update { state ->
                        val mergedGamers = roundGamers.map { dbGamer ->
                            when {
                                state.winner?.id == dbGamer.id -> dbGamer.copy(
                                    score = state.winner.score,
                                    go = state.winner.go,
                                    shakeCount = state.winner.shakeCount,
                                    bombCount = state.winner.bombCount,
                                    winnerOption = WinnerOption.Winner,
                                    loserOption = emptyList(),
                                )
                                state.seller?.id == dbGamer.id -> dbGamer.copy(
                                    score = state.seller.score,
                                    go = 0,
                                    shakeCount = 0,
                                    bombCount = 0,
                                    sellerOption = SellerOption.Seller,
                                    scoreOption = emptyList(),
                                    winnerOption = null,
                                    loserOption = emptyList(),
                                )
                                else -> dbGamer
                            }
                        }
                        state.copy(playerResults = mergedGamers)
                    }
                }.launchIn(this)
        }
    }

    fun selectScore(gamer: Gamer, option: ScoreOption, checkThreeFuck: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            actionMutex.withLock {
                if (isSettling || isClosed) return@withLock
                val latestGamer = _uiState.value.playerResults.firstOrNull { it.id == gamer.id } ?: gamer
                var shouldShowThreeFuckDialog = false
                toggleScoreOptionUseCase(
                    gamer = latestGamer,
                    option = option,
                    checkThreeFuck = { shouldShow -> shouldShowThreeFuckDialog = shouldShow }
                )
                if (shouldShowThreeFuckDialog) {
                    _uiState.update {
                        it.copy(threeFuckGamer = latestGamer.copy(scoreOption = listOf(ScoreOption.ThreeFuck)))
                    }
                    checkThreeFuck(true)
                }
            }
        }
    }

    fun selectLoser(gamer: Gamer, option: LoserOption) {
        viewModelScope.launch {
            actionMutex.withLock {
                if (isSettling || isClosed) return@withLock
                toggleLoserOption(gamer.id, option)
            }
        }
    }

    fun onNextPhase(expectedPhase: Phase? = null) {
        viewModelScope.launch {
            actionMutex.withLock {
                if (isSettling || isClosed) return@withLock
                _uiState.update { state ->
                    if (expectedPhase != null && state.phase != expectedPhase) return@update state
                    if (!state.phase.getEnableNext()) return@update state
                    val nextPhase = when (state.phase) {
                        is Selling -> Scoring
                        is Scoring -> Winner(false)
                        is Winner -> Loser
                        Loser -> return@update state
                    }
                    state.copy(phase = nextPhase)
                }
            }
        }
    }

    fun onBackPhase() {
        viewModelScope.launch {
            val shouldEscape = actionMutex.withLock {
                if (isSettling || isClosed) return@withLock false
                val snapshot = _uiState.value
                val previousPhase = snapshot.phase
                if (previousPhase is Selling) {
                    return@withLock true
                }
                if (previousPhase is Scoring && snapshot.playerResults.size != 4) {
                    return@withLock true
                }

                val previousWinner = snapshot.winner
                _uiState.update { state ->
                    if (state.phase != previousPhase) return@update state
                    state.copy(
                        phase = when (state.phase) {
                            is Scoring -> if (state.playerResults.size == 4) Selling(state.seller != null) else state.phase
                            is Winner -> Scoring
                            Loser -> Winner(state.winner != null)
                            else -> throw IllegalStateException("해당 페이즈가 존재하지 않습니다: ${state.phase}")
                        },
                        winner = when (state.phase) {
                            is Scoring, is Winner -> null
                            else -> state.winner
                        },
                        playerResults = when (state.phase) {
                            is Scoring, is Winner -> state.playerResults.map {
                                if (state.seller?.id == it.id) {
                                    it.copy(score = state.seller.score, go = 0, shakeCount = 0, bombCount = 0)
                                } else {
                                    it.copy(score = 0, go = 0, shakeCount = 0, bombCount = 0, winnerOption = null)
                                }
                            }
                            Loser -> state.playerResults.map {
                                when (it.id) {
                                    state.winner?.id -> it.copy(
                                        score = state.winner.score,
                                        go = state.winner.go,
                                        shakeCount = state.winner.shakeCount,
                                        bombCount = state.winner.bombCount,
                                    )
                                    state.seller?.id -> it.copy(score = state.seller.score, go = 0, shakeCount = 0, bombCount = 0)
                                    else -> it.copy(score = 0, go = 0, shakeCount = 0, bombCount = 0)
                                }
                            }
                            else -> throw IllegalStateException("해당 페이즈가 존재하지 않습니다: ${state.phase}")
                        }
                    )
                }

                if (previousPhase is Winner && previousWinner != null) {
                    gamerRepository.clearOption(previousWinner.id, WinnerOption::class)
                    gamerRepository.updateScore(
                        previousWinner.copy(score = 0, go = 0, shakeCount = 0, bombCount = 0),
                        0,
                        0,
                        0,
                        0,
                    )
                }
                false
            }
            if (shouldEscape) {
                _escapeEvent.emit(Unit)
            }
        }
    }

    fun updateSeller(seller: Gamer, count: Long) {
        viewModelScope.launch {
            if (count > MAX_SELL_COUNT) {
                _toast.emit(getString(Res.string.max_sell_count_alert, MAX_SELL_COUNT))
                return@launch
            }
            actionMutex.withLock {
                if (isSettling || isClosed || _uiState.value.phase !is Selling) return@withLock
                var shouldClearOptions = false
                _uiState.update { state ->
                    val currentSeller = state.playerResults.firstOrNull { it.id == seller.id } ?: seller
                    shouldClearOptions = count != 0L && (
                        currentSeller.scoreOption.isNotEmpty() ||
                            currentSeller.winnerOption != null ||
                            currentSeller.loserOption.isNotEmpty() ||
                            state.winner?.id == seller.id
                        )
                    val newGamers = state.playerResults.map {
                        if (it.id == seller.id) {
                            val targetSellerOption = if (count != 0L) SellerOption.Seller else null
                            val targetScoreOption = if (count != 0L) emptyList() else it.scoreOption
                            val targetLoserOption = if (count != 0L) emptyList() else it.loserOption
                            if (it.score == count.toInt() &&
                                it.go == 0 &&
                                it.shakeCount == 0 &&
                                it.bombCount == 0 &&
                                it.sellerOption == targetSellerOption &&
                                it.scoreOption == targetScoreOption &&
                                it.winnerOption == null &&
                                it.loserOption == targetLoserOption
                            ) {
                                it
                            } else {
                                it.copy(
                                    score = count.toInt(),
                                    go = 0,
                                    shakeCount = 0,
                                    bombCount = 0,
                                    sellerOption = targetSellerOption,
                                    scoreOption = targetScoreOption,
                                    winnerOption = null,
                                    loserOption = targetLoserOption,
                                )
                            }
                        } else if (
                            it.score == 0 &&
                            it.go == 0 &&
                            it.shakeCount == 0 &&
                            it.bombCount == 0 &&
                            it.sellerOption == null
                        ) {
                            it
                        } else {
                            it.copy(score = 0, go = 0, shakeCount = 0, bombCount = 0, sellerOption = null)
                        }
                    }
                    val updatedPhase = if (state.phase is Selling) {
                        Selling(newGamers.any { it.score != 0 })
                    } else {
                        state.phase
                    }
                    state.copy(
                        phase = updatedPhase,
                        seller = if (count != 0L) currentSeller.copy(
                            score = count.toInt(),
                            go = 0,
                            shakeCount = 0,
                            bombCount = 0,
                            sellerOption = SellerOption.Seller,
                            scoreOption = emptyList(),
                            winnerOption = null,
                            loserOption = emptyList(),
                        ) else null,
                        winner = if (state.winner?.id == seller.id && count != 0L) null else state.winner,
                        playerResults = newGamers
                    )
                }
                if (shouldClearOptions) {
                    gamerRepository.clearOption(seller.id, ScoreOption::class)
                    gamerRepository.clearOption(seller.id, WinnerOption::class)
                    gamerRepository.clearOption(seller.id, LoserOption::class)
                }
            }
        }
    }

    fun updateWinner(
        gamer: Gamer,
        point: Long,
        go: Long,
        shakeCount: Int = gamer.shakeCount,
        bombCount: Int = gamer.bombCount,
    ) {
        viewModelScope.launch {
            applyWinnerUpdate(gamer.id) {
                WinnerInput(
                    point = point,
                    go = go,
                    shakeCount = shakeCount,
                    bombCount = bombCount,
                )
            }
        }
    }

    fun adjustWinnerPoint(gamerId: Long, delta: Long) {
        viewModelScope.launch {
            applyWinnerUpdate(gamerId) { current ->
                WinnerInput(
                    point = (current.score.toLong() + delta).coerceAtLeast(0L),
                    go = current.go.toLong(),
                    shakeCount = current.shakeCount,
                    bombCount = current.bombCount,
                )
            }
        }
    }

    fun updateWinnerPoint(gamerId: Long, point: Long) {
        viewModelScope.launch {
            applyWinnerUpdate(gamerId) { current ->
                WinnerInput(
                    point = point.coerceAtLeast(0L),
                    go = current.go.toLong(),
                    shakeCount = current.shakeCount,
                    bombCount = current.bombCount,
                )
            }
        }
    }

    fun updateWinnerGo(gamerId: Long, go: Long) {
        viewModelScope.launch {
            applyWinnerUpdate(gamerId) { current ->
                WinnerInput(
                    point = current.score.toLong(),
                    go = go,
                    shakeCount = current.shakeCount,
                    bombCount = current.bombCount,
                )
            }
        }
    }

    fun cycleWinnerShake(gamerId: Long) {
        viewModelScope.launch {
            applyWinnerUpdate(gamerId) { current ->
                val nextShake = (current.shakeCount + 1) % (MAX_WINNER_SPECIAL_MULTIPLIER_COUNT + 1)
                WinnerInput(
                    point = current.score.toLong(),
                    go = current.go.toLong(),
                    shakeCount = nextShake,
                    bombCount = current.bombCount,
                )
            }
        }
    }

    fun cycleWinnerBomb(gamerId: Long) {
        viewModelScope.launch {
            applyWinnerUpdate(gamerId) { current ->
                val nextBomb = (current.bombCount + 1) % (MAX_WINNER_SPECIAL_MULTIPLIER_COUNT + 1)
                WinnerInput(
                    point = current.score.toLong(),
                    go = current.go.toLong(),
                    shakeCount = current.shakeCount,
                    bombCount = nextBomb,
                )
            }
        }
    }

    private data class WinnerInput(
        val point: Long,
        val go: Long,
        val shakeCount: Int,
        val bombCount: Int,
    )

    private suspend fun applyWinnerUpdate(
        gamerId: Long,
        compute: (Gamer) -> WinnerInput,
    ) {
        var overPointLimit = false
        actionMutex.withLock {
            if (isSettling || isClosed || _uiState.value.phase !is Winner) return@withLock
            val snapshot = _uiState.value
            val currentGamer = snapshot.playerResults.firstOrNull { it.id == gamerId } ?: return@withLock
            val input = compute(currentGamer)
            if (input.point > MAX_POINT) {
                overPointLimit = true
                return@withLock
            }

            val boundedPoint = input.point.coerceAtLeast(0L).toInt()
            val boundedGo = input.go.coerceIn(0L, MAX_GO_COUNT).toInt()
            val willBeWinner = boundedPoint != 0 || boundedGo != 0
            val isCurrentWinner = snapshot.winner?.id == gamerId

            // 비승자 카드에서 (0점, 0고) 입력이 들어와도 기존 승자를 지우지 않는다.
            if (!willBeWinner && !isCurrentWinner) {
                if (currentGamer.score == 0 &&
                    currentGamer.go == 0 &&
                    currentGamer.shakeCount == 0 &&
                    currentGamer.bombCount == 0 &&
                    currentGamer.winnerOption == null
                ) {
                    return@withLock
                }
                _uiState.update { state ->
                    if (state.phase !is Winner) return@update state
                    state.copy(
                        playerResults = state.playerResults.map {
                            if (it.id == gamerId) {
                                it.copy(score = 0, go = 0, shakeCount = 0, bombCount = 0, winnerOption = null)
                            } else {
                                it
                            }
                        }
                    )
                }
                return@withLock
            }

            val boundedShake = if (willBeWinner) {
                input.shakeCount.coerceIn(0, MAX_WINNER_SPECIAL_MULTIPLIER_COUNT)
            } else {
                0
            }
            val boundedBomb = if (willBeWinner) {
                input.bombCount.coerceIn(0, MAX_WINNER_SPECIAL_MULTIPLIER_COUNT)
            } else {
                0
            }

            var shouldClearLoser = false
            _uiState.update { state ->
                if (state.phase !is Winner) return@update state
                val latestGamer = state.playerResults.firstOrNull { it.id == gamerId } ?: currentGamer
                val sellerId = state.seller?.id
                shouldClearLoser = willBeWinner && latestGamer.loserOption.isNotEmpty()
                val newGamers = state.playerResults.map {
                    when (it.id) {
                        gamerId -> {
                            val targetWinnerOption = if (willBeWinner) WinnerOption.Winner else null
                            val targetLoserOption = if (willBeWinner) emptyList() else it.loserOption
                            if (it.score == boundedPoint &&
                                it.go == boundedGo &&
                                it.shakeCount == boundedShake &&
                                it.bombCount == boundedBomb &&
                                it.winnerOption == targetWinnerOption &&
                                it.loserOption == targetLoserOption
                            ) {
                                it
                            } else {
                                it.copy(
                                    score = boundedPoint,
                                    go = boundedGo,
                                    shakeCount = boundedShake,
                                    bombCount = boundedBomb,
                                    winnerOption = targetWinnerOption,
                                    loserOption = targetLoserOption,
                                )
                            }
                        }
                        sellerId -> {
                            val sellerScore = state.seller.score
                            if (it.score == sellerScore &&
                                it.go == 0 &&
                                it.shakeCount == 0 &&
                                it.bombCount == 0
                            ) {
                                it
                            } else {
                                it.copy(score = sellerScore, go = 0, shakeCount = 0, bombCount = 0)
                            }
                        }
                        else -> {
                            if (it.score == 0 &&
                                it.go == 0 &&
                                it.shakeCount == 0 &&
                                it.bombCount == 0 &&
                                it.winnerOption == null
                            ) {
                                it
                            } else {
                                it.copy(
                                    score = 0,
                                    go = 0,
                                    shakeCount = 0,
                                    bombCount = 0,
                                    winnerOption = null,
                                )
                            }
                        }
                    }
                }
                val updatedWinner = newGamers.firstOrNull { it.id == gamerId && willBeWinner }
                val updatedPhase = Winner(updatedWinner != null)
                state.copy(
                    phase = updatedPhase,
                    winner = updatedWinner,
                    playerResults = newGamers
                )
            }
            if (shouldClearLoser) {
                gamerRepository.clearOption(gamerId, LoserOption::class)
            }
        }
        if (overPointLimit) {
            _toast.emit(getString(Res.string.over_point_alert, MAX_POINT.separateComma()))
        }
    }

    suspend fun calculateGameResult() {
        withContext(NonCancellable) {
            actionMutex.withLock {
                if (isSettling || isClosed) return@withLock
                isSettling = true
                try {
                    val state = _uiState.value
                    val seller = state.seller
                    if (seller != null) {
                        gamerRepository.clearOption(seller.id, ScoreOption::class)
                        gamerRepository.clearOption(seller.id, WinnerOption::class)
                        gamerRepository.clearOption(seller.id, LoserOption::class)
                        sellingUseCase.invoke(seller)
                    }

                    val winner = state.winner
                    if (winner != null) {
                        gamerRepository.clearOption(winner.id, LoserOption::class)
                        updateWinnerUseCase.invoke(winner)
                    } else {
                        state.playerResults.forEach { gamer ->
                            gamerRepository.clearOption(gamer.id, LoserOption::class)
                            gamerRepository.clearOption(gamer.id, WinnerOption::class)
                        }
                    }

                    calculateGameResultUseCase.invoke(
                        gameId = gameId,
                        roundId = roundId,
                        seller = seller,
                        winner = winner
                    )
                } catch (t: Throwable) {
                    isSettling = false
                    throw t
                }
            }
        }
    }

    suspend fun deleteRound() {
        withContext(NonCancellable) {
            actionMutex.withLock {
                if (isClosed) return@withLock
                isClosed = true
                roundRepository.deleteRound(roundId)
            }
        }
    }

    companion object {
        private const val MAX_SELL_COUNT = 12
        private const val MAX_POINT = 8_519_680
        private const val MAX_GO_COUNT = 20L
    }
}
