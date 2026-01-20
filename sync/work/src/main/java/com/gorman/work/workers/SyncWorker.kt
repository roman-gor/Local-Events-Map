package com.gorman.work.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gorman.data.repository.IMapEventsRepository
import com.gorman.work.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val mapEventRepository: IMapEventsRepository
): CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result = withContext(IO) {
        try {
            Log.d("SyncWorker", "Worker")
            val result = mapEventRepository.syncWith()
            showNotification(appContext, "Worker in work")
            result.fold(
                onSuccess = {
                    Result.success()
                },
                onFailure = { error ->
                    Log.e("SyncWorker", "Sync failed: ${error.message}")
                    val text = when (error) {
                        is TimeoutCancellationException -> appContext.getString(com.gorman.work.R.string.NetworkError)
                        else -> appContext.getString(com.gorman.work.R.string.DefaultError)
                    }
                    showNotification(appContext, text)
                    if (error is TimeoutCancellationException) {
                        Result.retry()
                    } else {
                        Result.failure()
                    }
                }
            )
        } catch (e: Exception) {
            Log.e("SyncWorker", "Error was happened ${e.message}")
            if (runAttemptCount < 3)
                Result.retry()
            else
                Result.failure()
        }
    }

    private fun showNotification(context: Context, text: String) {
        NotificationHelper.showSyncErrorNotification(context = context, message = text)
    }
}
