package zero.friends.gostopcalculator.ui.board.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.RoundTrace
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.GamerRepository
import zero.friends.domain.repository.RoundRepository
import zero.friends.domain.repository.RuleRepository
import zero.friends.domain.usecase.calculate.AggregateGamerResultsUseCase
import zero.friends.domain.usecase.calculate.BuildRoundTraceUseCase
import zero.friends.domain.usecase.calculate.audit.AuditPhase
import zero.friends.domain.usecase.calculate.audit.CalculationAuditor
import zero.friends.gostopcalculator.util.mapAsync

@Inject
class ResultViewModel(
    private val gameRepository: GameRepository,
    private val gamerRepository: GamerRepository,
    private val roundRepository: RoundRepository,
    private val ruleRepository: RuleRepository,
    private val aggregateGamerResultsUseCase: AggregateGamerResultsUseCase,
    private val buildRoundTraceUseCase: BuildRoundTraceUseCase,
    private val calculationAuditor: CalculationAuditor,
    @Assisted private val gameId: Long,
    @Assisted private val roundId: Long,
    @Assisted private val initialScreenType: ScreenType?
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResultUiState())
    val uiState: StateFlow<ResultUiState> = _uiState.asStateFlow()

    init {
        val screenType = initialScreenType ?: ScreenType.Calculate
        _uiState.update { it.copy(screenType = screenType) }

        when (screenType) {
            ScreenType.Calculate, ScreenType.CALCULATE -> loadCalculateData()
            ScreenType.Detail, ScreenType.DETAIL, ScreenType.Result, ScreenType.RESULT -> loadDetailData()
        }
    }

    private suspend fun getRulesSafe(targetGameId: Long) = try {
        ruleRepository.getRules(targetGameId)
    } catch (ce: CancellationException) {
        throw ce
    } catch (_: Throwable) {
        emptyList()
    }

    private fun loadCalculateData() {
        viewModelScope.launch {
            val rules = getRulesSafe(gameId)

            combine(
                gameRepository.observeGame(gameId),
                gamerRepository.observeGamers(gameId = gameId)
                    .mapAsync { rawGamers -> rawGamers to aggregateGamerResultsUseCase(rawGamers) },
            ) { game, (rawGamers, result) ->
                Triple(game, rawGamers, result)
            }
                .onEach { (game, rawGamers, result) ->
                    calculationAuditor.auditSettlement(
                        gameId = game.id,
                        rawGamers = rawGamers,
                        aggregatedGamers = result,
                        rules = rules,
                    )
                    _uiState.update {
                        it.copy(
                            gameName = game.name,
                            gamers = result,
                            roundTrace = null
                        )
                    }
                }
                .launchIn(this)
        }
    }

    private fun loadDetailData() {
        viewModelScope.launch {
            val rules = getRulesSafe(gameId)

            combine(
                gameRepository.observeGame(gameId),
                roundRepository.observeRoundMeta(roundId),
                gamerRepository.observeRoundGamers(roundId),
            ) { game, roundMeta, gamers ->
                Triple(game, roundMeta?.name.orEmpty(), gamers)
            }
                .onEach { (game, roundName, gamers) ->
                    val trace = buildRoundTraceUseCase(gameId = gameId, roundId = roundId)
                    calculationAuditor.auditRound(
                        phase = AuditPhase.ROUND_DETAIL,
                        gameId = gameId,
                        roundId = roundId,
                        gamers = gamers,
                        rules = rules,
                        trace = trace,
                    )
                    _uiState.update {
                        it.copy(
                            gameName = game.name,
                            roundName = roundName,
                            gamers = gamers,
                            roundTrace = trace
                        )
                    }
                }
                .launchIn(this)
        }
    }
}

data class ResultUiState(
    val screenType: ScreenType = ScreenType.Calculate,
    val gameName: String = "",
    val roundName: String = "",
    val gamers: List<Gamer> = emptyList(),
    val roundTrace: RoundTrace? = null,
)
