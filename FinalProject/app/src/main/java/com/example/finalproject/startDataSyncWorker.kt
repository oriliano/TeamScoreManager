import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.finalproject.workers.DataSyncWorker

fun startDataSyncWorker(context: Context) {
    val workRequest: WorkRequest = OneTimeWorkRequestBuilder<DataSyncWorker>().build()
    WorkManager.getInstance(context).enqueue(workRequest)
}
