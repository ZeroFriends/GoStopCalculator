package zero.friends.gostopcalculator.di

import me.tatarka.inject.annotations.Component
import zero.friends.data.di.DataComponent
import zero.friends.domain.model.Player
import zero.friends.gostopcalculator.ui.board.main.BoardViewModel
import zero.friends.gostopcalculator.ui.board.prepare.PrepareViewModel
import zero.friends.gostopcalculator.ui.board.rule.RuleLogViewModel
import zero.friends.gostopcalculator.ui.board.result.CalculateViewModel
import zero.friends.gostopcalculator.ui.board.result.DetailViewModel
import zero.friends.gostopcalculator.ui.board.result.ResultScreen
import zero.friends.gostopcalculator.ui.board.result.ResultViewModel
import zero.friends.gostopcalculator.ui.board.result.RoundTraceFormatter
import zero.friends.gostopcalculator.ui.board.result.ScreenType
import zero.friends.gostopcalculator.ui.board.score.ScoreViewModel
import zero.friends.gostopcalculator.ui.board.score.end.EndViewModel
import zero.friends.gostopcalculator.ui.board.score.manual.ManualViewModel
import zero.friends.gostopcalculator.ui.history.HistoryViewModel
import zero.friends.gostopcalculator.ui.precondition.PlayerViewModel
import zero.friends.gostopcalculator.ui.precondition.RuleViewModel

import zero.friends.gostopcalculator.ui.history.guide.GuideViewModel
import zero.friends.gostopcalculator.util.ScreenCaptureManager

@Component
abstract class AppComponent(
    @Component val dataComponent: DataComponent
) {
    abstract val historyViewModel: HistoryViewModel
    abstract val playerViewModel: PlayerViewModel
    abstract val manualViewModel: ManualViewModel
    abstract val guideViewModel: GuideViewModel
    abstract val screenCaptureManager: ScreenCaptureManager
    abstract val roundTraceFormatter: RoundTraceFormatter

    abstract val ruleViewModelFactory: (players: List<Player>, gameName: String) -> RuleViewModel
    abstract val boardViewModelFactory: (gameId: Long) -> BoardViewModel
    abstract val prepareViewModelFactory: (gameId: Long) -> PrepareViewModel
    abstract val ruleLogViewModelFactory: (gameId: Long) -> RuleLogViewModel
    abstract val scoreViewModelFactory: (gameId: Long, roundId: Long) -> ScoreViewModel
    abstract val endViewModelFactory: (gameId: Long, roundId: Long) -> EndViewModel
    abstract val resultViewModelFactory: (gameId: Long, roundId: Long, initialScreenType: ScreenType?) -> ResultViewModel
    abstract val calculateViewModelFactory: (gameId: Long) -> CalculateViewModel
    abstract val detailViewModelFactory: (gameId: Long, roundId: Long) -> DetailViewModel
}



expect fun createAppComponent(): AppComponent
