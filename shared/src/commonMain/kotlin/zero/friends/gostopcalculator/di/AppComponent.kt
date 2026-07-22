package zero.friends.gostopcalculator.di

import me.tatarka.inject.annotations.Component
import zero.friends.data.di.DataComponent
import zero.friends.gostopcalculator.ui.history.HistoryViewModel

@Component
abstract class AppComponent(
    @Component val dataComponent: DataComponent
) {
    abstract val historyViewModel: HistoryViewModel
}

expect fun createAppComponent(): AppComponent
