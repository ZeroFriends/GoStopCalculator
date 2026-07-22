package zero.friends.gostopcalculator.di

import me.tatarka.inject.annotations.Component
import zero.friends.data.di.DataComponent
import zero.friends.gostopcalculator.ui.history.HistoryViewModel
import zero.friends.gostopcalculator.ui.precondition.PlayerViewModel
import zero.friends.gostopcalculator.ui.precondition.RuleViewModel

@Component
abstract class AppComponent(
    @Component val dataComponent: DataComponent
) {
    abstract val historyViewModel: HistoryViewModel
    abstract val playerViewModel: PlayerViewModel
    abstract val ruleViewModel: RuleViewModel
}

expect fun createAppComponent(): AppComponent
