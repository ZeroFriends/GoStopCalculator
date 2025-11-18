package zero.friends.gostopcalculator.ui.board.result

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.RoundTrace
import zero.friends.domain.model.Target
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.GamerRepository
import zero.friends.domain.usecase.calculate.BuildRoundTraceUseCase
import zero.friends.gostopcalculator.ui.board.result.ResultActivity.Companion.EXTRA_GAME_ID
import zero.friends.gostopcalculator.ui.board.result.ResultActivity.Companion.EXTRA_ROUND_ID
import zero.friends.gostopcalculator.ui.board.result.ResultActivity.Companion.EXTRA_SCREEN_TYPE
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val gamerRepository: GamerRepository,
    private val buildRoundTraceUseCase: BuildRoundTraceUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val gameId by lazy { savedStateHandle[EXTRA_GAME_ID] ?: -1L }
    private val roundId by lazy { savedStateHandle[EXTRA_ROUND_ID] ?: -1L }

    private val _uiState = MutableStateFlow(ResultUiState())
    val uiState: StateFlow<ResultUiState> = _uiState.asStateFlow()

    init {
        val screenType = savedStateHandle.get<ResultActivity.ScreenType>(EXTRA_SCREEN_TYPE)
        if (screenType != null) {
            _uiState.update { it.copy(screenType = screenType) }
        }
    }

    fun loadCalculateData() {
        viewModelScope.launch {
            val game = gameRepository.getGame(gameId)

            gamerRepository.observeGamers(gameId = game.id)
                .map { gamers ->
                    val sameGamersMap = gamers.groupBy({ it.name }, { it })
                    sameGamersMap.flatMap { (name, sameGamers) ->
                        sameGamers
                            .map { it.calculate }
                            .scan<List<Target>, MutableMap<String, Target>>(mutableMapOf()) { map, targets ->
                                targets.forEach {
                                    val target = map[it.name]
                                    if (target != null) target.account += it.account
                                    else map[it.name] = it
                                }
                                map
                            }
                            .map { it.values.toList() }
                            .distinct()
                            .map { calculate ->
                                Gamer(
                                    name = name,
                                    account = sameGamers.sumOf { it.account },
                                    calculate = calculate
                                )
                            }
                    }
                }
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

    fun loadDetailData() {
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
    val screenType: ResultActivity.ScreenType = ResultActivity.ScreenType.CALCULATE,
    val gameName: String = "",
    val gamers: List<Gamer> = emptyList(),
    val roundTrace: RoundTrace? = null,
)
