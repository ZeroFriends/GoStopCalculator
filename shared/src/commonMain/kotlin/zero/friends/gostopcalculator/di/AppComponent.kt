package zero.friends.gostopcalculator.di

import me.tatarka.inject.annotations.Component
import zero.friends.data.di.DataComponent
import zero.friends.domain.model.Player
import zero.friends.gostopcalculator.ui.board.main.BoardViewModel
import zero.friends.gostopcalculator.ui.board.prepare.PrepareViewModel
import zero.friends.gostopcalculator.ui.board.rule.RuleLogViewModel
import zero.friends.gostopcalculator.ui.board.score.ScoreViewModel
import zero.friends.gostopcalculator.ui.board.score.end.EndViewModel
import zero.friends.gostopcalculator.ui.board.score.manual.ManualViewModel
import zero.friends.gostopcalculator.ui.history.HistoryViewModel
import zero.friends.gostopcalculator.ui.precondition.PlayerViewModel
import zero.friends.gostopcalculator.ui.precondition.RuleViewModel

@Component
abstract class AppComponent(
    @Component val dataComponent: DataComponent
) {
    abstract val historyViewModel: HistoryViewModel
    abstract val playerViewModel: PlayerViewModel
    abstract val manualViewModel: ManualViewModel
    abstract val ruleViewModelFactory: (players: List<Player>, gameName: String) -> RuleViewModel
    abstract val boardViewModelFactory: (gameId: Long) -> BoardViewModel
    abstract val prepareViewModelFactory: (gameId: Long) -> PrepareViewModel
    abstract val ruleLogViewModelFactory: (gameId: Long) -> RuleLogViewModel
    abstract val scoreViewModelFactory: (gameId: Long, roundId: Long) -> ScoreViewModel
    abstract val endViewModelFactory: (gameId: Long, roundId: Long) -> EndViewModel
}


expect fun createAppComponent(): AppComponent
