package zero.friends.gostopcalculator.di

import android.content.Context
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import zero.friends.data.di.DataComponent
import zero.friends.gostopcalculator.GoStopApplication
import zero.friends.gostopcalculator.GoogleAdmob
import zero.friends.gostopcalculator.NetWorkChecker

@Component
abstract class AndroidAppComponent(
    @Component val dataComponent: DataComponent
) {
    @Provides
    fun provideContext(): Context = GoStopApplication.instance

    val netWorkChecker: NetWorkChecker by lazy { NetWorkChecker(provideContext()) }
    val googleAdmob: GoogleAdmob by lazy { GoogleAdmob(provideContext(), netWorkChecker) }
}
