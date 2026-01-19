package com.gorman.work.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gorman.data.repository.IMapEventsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers.IO
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
            mapEventRepository.syncWith()
            Result.success()
        } catch (e: Exception) {
            Log.e("SyncWorker", "Error was happened ${e.message}")
            if (runAttemptCount < 3)
                Result.retry()
            else
                Result.failure()
        }
    }
}
