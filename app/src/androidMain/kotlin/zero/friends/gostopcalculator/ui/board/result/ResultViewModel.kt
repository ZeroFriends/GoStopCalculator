package zero.friends.gostopcalculator.ui.board.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
import zero.friends.domain.usecase.calculate.AggregateGamerResultsUseCase
import zero.friends.domain.usecase.calculate.BuildRoundTraceUseCase
import zero.friends.gostopcalculator.util.mapAsync

@Inject
class ResultViewModel(
    private val gameRepository: GameRepository,
    private val gamerRepository: GamerRepository,
    private val aggregateGamerResultsUseCase: AggregateGamerResultsUseCase,
    private val buildRoundTraceUseCase: BuildRoundTraceUseCase,
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
            ScreenType.Calculate -> loadCalculateData()
            ScreenType.Detail -> loadDetailData()
            else -> {}
        }
    }

    private fun loadCalculateData() {
        viewModelScope.launch {
            val game = gameRepository.getGame(gameId)

            gamerRepository.observeGamers(gameId = game.id)
                .mapAsync(aggregateGamerResultsUseCase::invoke)
                .onEach { result ->
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
            val game = gameRepository.getGame(gameId)
            val gamers = gamerRepository.getRoundGamers(roundId)
            val trace = buildRoundTraceUseCase(gameId = gameId, roundId = roundId)

            _uiState.update {
                it.copy(
                    gameName = game.name,
                    gamers = gamers,
                    roundTrace = trace
                )
            }
        }
    }
}

data class ResultUiState(
    val screenType: ScreenType = ScreenType.Calculate,
    val gameName: String = "",
    val gamers: List<Gamer> = emptyList(),
    val roundTrace: RoundTrace? = null,
)
