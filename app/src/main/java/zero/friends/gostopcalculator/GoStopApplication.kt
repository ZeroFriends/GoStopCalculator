package zero.friends.gostopcalculator

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp
import zero.friends.gostopcalculator.work.EmptyRoundCleanupWorker
import javax.inject.Inject

@HiltAndroidApp
class GoStopApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = !BuildConfig.DEBUG

        WorkManager.initialize(this, workManagerConfiguration)
        EmptyRoundCleanupWorker.enqueue(applicationContext)
    }
}
