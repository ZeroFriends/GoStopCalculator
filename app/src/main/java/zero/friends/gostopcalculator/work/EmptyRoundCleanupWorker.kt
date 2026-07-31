package zero.friends.gostopcalculator.work

import android.content.Context
import androidx.room.withTransaction
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import zero.friends.gostopcalculator.GoStopApplication
import zero.friends.gostopcalculator.NonCrashReport

class EmptyRoundCleanupWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private val dataComponent get() = GoStopApplication.instance.appComponent.dataComponent
    private val dataBase get() = dataComponent.dataBase
    private val roundDao get() = dataComponent.provideRoundDao()
    private val gamerDao get() = dataComponent.provideGamerDao()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val rounds = roundDao.getAllRounds()
            var deletedRounds = 0
            val deletedRoundIds = mutableListOf<Long>()

            rounds.forEach { round ->
                dataBase.withTransaction {
                    val gamers = gamerDao.getRoundGamers(round.id)
                    val shouldDeleteRound = gamers.isEmpty() || gamers.all { gamer ->
                        gamer.score == 0 && gamer.account == 0 && gamer.target.isEmpty()
                    }

                    if (shouldDeleteRound) {
                        roundDao.deleteRound(round.id)
                        deletedRounds++
                        deletedRoundIds += round.id
                    }
                }
            }

            if (deletedRoundIds.isNotEmpty()) {
                NonCrashReport.send(
                    message = "EmptyRoundCleanup deleted ${deletedRoundIds.size} rounds",
                    extras = mapOf(
                        "roundIds" to deletedRoundIds.joinToString(","),
                        "reason" to "gamers empty or all score/account zero with empty targets"
                    )
                )
            }

            Timber.i("EmptyRoundCleanupWorker deleted %d rounds", deletedRounds)
            Result.success(workDataOf(KEY_DELETED_ROUNDS to deletedRounds))
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
