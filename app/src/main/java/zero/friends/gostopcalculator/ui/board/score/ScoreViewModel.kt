package zero.friends.gostopcalculator.ui.board.score

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import zero.friends.domain.model.Game
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.ScoreOption
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.usecase.GetRoundGamerUseCase
import javax.inject.Inject

data class ScoreUiState(
    val game: Game = Game(),
    val playerResults: List<Gamer> = emptyList(),
    val phase: Phase = Scoring()
)

@HiltViewModel
class ScoreViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val gameRepository: GameRepository,
    private val getRoundGamerUseCase: GetRoundGamerUseCase,
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
        }

    }

    fun selectScore(gamer: Gamer, option: ScoreOption) {

    }


}