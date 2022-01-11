package zero.friends.gostopcalculator

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GoStopApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}