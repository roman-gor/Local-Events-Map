package com.gorman.work.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gorman.data.repository.mapevents.IMapEventsRepository
import com.gorman.work.NotificationHelper
import com.gorman.work.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import java.io.IOException

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val mapEventRepository: IMapEventsRepository
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result = withContext(IO) {
        try {
            Log.d("SyncWorker", "Worker")
            val result = mapEventRepository.syncWith()

            result.fold(
                onSuccess = {
                    showSuccessNotification(appContext, appContext.getString(R.string.successSync))
                    Result.success()
                },
                onFailure = { error ->
                    Log.e("SyncWorker", "Sync failed: ${error.message}")
                    showErrorNotification(appContext, appContext.getString(R.string.errorSync))
                    if (error is TimeoutCancellationException) {
                        Result.retry()
                    } else {
                        Result.failure()
                    }
                }
            )
        } catch (e: IOException) {
            Log.e("SyncWorker", "Error was happened ${e.message}")
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        } catch (e: TimeoutCancellationException) {
            Log.e("SyncWorker", "Error was happened ${e.message}")
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    private fun showErrorNotification(context: Context, text: String) {
        NotificationHelper.showSyncErrorNotification(context = context, message = text)
    }

    private fun showSuccessNotification(context: Context, text: String) {
        NotificationHelper.showSuccessNotification(context, text)
    }
}
