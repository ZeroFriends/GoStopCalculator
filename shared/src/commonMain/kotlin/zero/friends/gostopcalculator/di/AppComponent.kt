package zero.friends.gostopcalculator.di

import me.tatarka.inject.annotations.Component
import zero.friends.data.di.DataComponent
import zero.friends.data.di.create
import zero.friends.gostopcalculator.ui.history.HistoryViewModel

@Component
abstract class AppComponent(
    @Component val dataComponent: DataComponent = DataComponent::class.create()
) {
    abstract val historyViewModel: HistoryViewModel
}

expect fun createAppComponent(): AppComponent
