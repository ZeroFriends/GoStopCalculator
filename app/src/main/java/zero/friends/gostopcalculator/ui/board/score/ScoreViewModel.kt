package zero.friends.gostopcalculator.ui.board.score

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import zero.friends.domain.model.Game
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.ScoreOption
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.usecase.gamer.GetRoundGamerUseCase
import zero.friends.domain.usecase.option.ToggleScoreOptionUseCase
import zero.friends.domain.usecase.round.ObserveRoundGamerUseCase
import javax.inject.Inject

data class ScoreUiState(
    val game: Game = Game(),
    val playerResults: List<Gamer> = emptyList(),
    val phase: Phase = Scoring()
)

@HiltViewModel
class ScoreViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val getRoundGamerUseCase: GetRoundGamerUseCase,
    private val toggleScoreOptionUseCase: ToggleScoreOptionUseCase,
    private val observeRoundGamerUseCase: ObserveRoundGamerUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ScoreUiState())
    fun uiState() = _uiState.asStateFlow()
    init {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    game = requireNotNull(gameRepository.getCurrentGame()),
                    playerResults = getRoundGamerUseCase(),
                    phase = Scoring()
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


}