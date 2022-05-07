package zero.friends.gostopcalculator.ui.board.result

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import zero.friends.domain.model.Game
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.Target
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.GamerRepository
import zero.friends.domain.util.Const
import javax.inject.Inject

data class CalculateUiState(
    val game: Game = Game(),
    val gamer: Gamer = Gamer(),
    val gamers: List<Gamer> = emptyList()
)

@HiltViewModel
class CalculateViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val gamerRepository: GamerRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalculateUiState())
    fun uiState() = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            //ㅋㅋㅋ 고칠수 있으면 고쳐봐 to 미래의 재영이
            val game = gameRepository.getGame(requireNotNull(savedStateHandle[Const.GameId]))
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
                    _uiState.update { it.copy(gamers = result) }
                }
                .launchIn(this)

            _uiState.update {
                it.copy(game = game)
            }
        }
    }
}