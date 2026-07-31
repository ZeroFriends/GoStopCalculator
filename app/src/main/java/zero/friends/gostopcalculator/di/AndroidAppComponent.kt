package zero.friends.gostopcalculator.di

import android.content.Context
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import zero.friends.data.di.DataComponent
import zero.friends.gostopcalculator.GoStopApplication
import zero.friends.gostopcalculator.GoogleAdmob
import zero.friends.gostopcalculator.NetWorkChecker
import zero.friends.gostopcalculator.ui.board.result.CalculateViewModel
import zero.friends.gostopcalculator.ui.board.result.DetailViewModel
import zero.friends.gostopcalculator.ui.board.result.ResultViewModel
import zero.friends.gostopcalculator.ui.board.result.ScreenType
import zero.friends.gostopcalculator.ui.board.score.ScoreViewModel
import zero.friends.gostopcalculator.ui.board.score.end.EndViewModel
import zero.friends.gostopcalculator.ui.board.score.manual.ManualViewModel
import zero.friends.gostopcalculator.ui.history.guide.GuideViewModel

@Component
abstract class AndroidAppComponent(
    @Component val dataComponent: DataComponent
) {
    @Provides
    fun provideContext(): Context = GoStopApplication.instance

    abstract val calculateViewModelFactory: (gameId: Long) -> CalculateViewModel
    abstract val detailViewModelFactory: (gameId: Long, roundId: Long) -> DetailViewModel
    abstract val resultViewModelFactory: (gameId: Long, roundId: Long, screenType: ScreenType?) -> ResultViewModel
    abstract val scoreViewModelFactory: (gameId: Long, roundId: Long) -> ScoreViewModel
    abstract val endViewModelFactory: (gameId: Long, roundId: Long) -> EndViewModel
    abstract val manualViewModel: ManualViewModel
    abstract val guideViewModel: GuideViewModel

    abstract val googleAdmob: GoogleAdmob
    abstract val netWorkChecker: NetWorkChecker
}
