package zero.friends.gostopcalculator.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import zero.friends.domain.usecase.CleanEmptyRoundsUseCase
import zero.friends.gostopcalculator.GoStopApplication
import zero.friends.gostopcalculator.NonCrashReport

class EmptyRoundCleanupWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private val dataComponent get() = GoStopApplication.instance.appComponent.dataComponent
    private val cleanEmptyRoundsUseCase by lazy {
        CleanEmptyRoundsUseCase(
            gameRepository = dataComponent.provideGameRepository(dataComponent.provideGameDao()),
            roundRepository = dataComponent.provideRoundRepository(dataComponent.provideRoundDao()),
            gamerRepository = dataComponent.provideGamerRepository(dataComponent.provideGamerDao())
        )
    }


    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val deletedCount = cleanEmptyRoundsUseCase()
            if (deletedCount > 0) {
                NonCrashReport.send(
                    message = "EmptyRoundCleanup deleted $deletedCount rounds",
                    extras = mapOf("reason" to "gamers empty or all score/account zero with empty targets")
                )
            }
            Timber.i("EmptyRoundCleanupWorker deleted %d rounds", deletedCount)
            Result.success(workDataOf(KEY_DELETED_ROUNDS to deletedCount))
        } catch (t: Throwable) {
            Timber.e(t, "EmptyRoundCleanupWorker failed")
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "empty_round_cleanup"
        const val KEY_DELETED_ROUNDS = "deleted_rounds"

        fun enqueue(context: Context) {
            val request = OneTimeWorkRequestBuilder<EmptyRoundCleanupWorker>().build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                WORK_NAME,
                ExistingWorkPolicy.KEEP,
                request
            )
        }
    }
}
