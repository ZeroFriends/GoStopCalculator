package zero.friends.gostopcalculator

import android.app.Application
import androidx.work.Configuration
import com.google.firebase.crashlytics.FirebaseCrashlytics
import zero.friends.data.di.create
import zero.friends.data.source.initAssetContext
import zero.friends.data.source.initDatabaseContext
import zero.friends.data.source.preference.initPreferenceContext
import zero.friends.gostopcalculator.di.AndroidAppComponent
import zero.friends.gostopcalculator.di.AppComponent
import zero.friends.gostopcalculator.di.create
import zero.friends.gostopcalculator.di.createAppComponent
import zero.friends.gostopcalculator.util.ApplicationContextProvider
import zero.friends.gostopcalculator.work.EmptyRoundCleanupWorker

class GoStopApplication : Application(), Configuration.Provider {

    lateinit var appComponent: AppComponent
        private set

    lateinit var androidAppComponent: AndroidAppComponent
        private set

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().build()

    override fun onCreate() {
        super.onCreate()
        instance = this

        initDatabaseContext(this)
        initAssetContext(this)
        initPreferenceContext(this)
        ApplicationContextProvider.init(this)


        appComponent = createAppComponent()
        androidAppComponent = AndroidAppComponent::class.create(appComponent.dataComponent)

        if (BuildConfig.DEBUG) {
            timber.log.Timber.plant(timber.log.Timber.DebugTree())
        }

        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = !BuildConfig.DEBUG

        EmptyRoundCleanupWorker.enqueue(applicationContext)
    }

    companion object {
        lateinit var instance: GoStopApplication
            private set
    }
}
