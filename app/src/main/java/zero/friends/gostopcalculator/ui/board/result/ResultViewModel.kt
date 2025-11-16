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
import zero.friends.domain.model.Target
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.GamerRepository
import zero.friends.gostopcalculator.ui.board.result.ResultActivity.Companion.EXTRA_SCREEN_TYPE
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val gamerRepository: GamerRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResultUiState())
    val uiState: StateFlow<ResultUiState> = _uiState.asStateFlow()

    init {
        val screenType = savedStateHandle.get<ResultActivity.ScreenType>(EXTRA_SCREEN_TYPE)
        if (screenType != null) {
            _uiState.update { it.copy(screenType = screenType) }
        }
    }

    fun loadCalculateData(gameId: Long) {
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
                            gamers = result
                        )
                    }
                }
                .launchIn(this)
        }
    }

    fun loadDetailData(gameId: Long, roundId: Long) {
        viewModelScope.launch {
            val game = gameRepository.getGame(gameId)
            val gamers = gamerRepository.getRoundGamers(roundId)

            _uiState.update {
                it.copy(
                    gameName = game.name,
                    gamers = gamers
                )
            }
        }
    }
}

data class ResultUiState(
    val screenType: ResultActivity.ScreenType = ResultActivity.ScreenType.CALCULATE,
    val gameName: String = "",
    val gamers: List<Gamer> = emptyList(),
)

