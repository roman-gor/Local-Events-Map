package com.gorman.work.workers

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
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

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val mapEventRepository: IMapEventsRepository
) : CoroutineWorker(appContext, workerParams) {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result = withContext(IO) {
        Log.d("SyncWorker", "Worker")
        val result = mapEventRepository.syncWith()
        result.fold(
            onSuccess = {
                Result.success()
            },
            onFailure = { error ->
                Log.e("SyncWorker", "Sync failed: ${error.message}")
                showErrorNotification(appContext, appContext.getString(R.string.errorSync))
                if (runAttemptCount < 3 && error is TimeoutCancellationException) {
                    Result.retry()
                } else {
                    Result.failure()
                }
            }
        )
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showErrorNotification(context: Context, text: String) {
        NotificationHelper.showSyncErrorNotification(context = context, message = text)
    }
}
