// com.example.finalproject.workers.DataSyncWorker.kt
package com.example.finalproject.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.finalproject.data.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DataSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            // API'den takımları çekme
            val response = RetrofitClient.instance.getAllTeams()
            if (response.isSuccessful) {
                val allTeams = response.body()
                if (allTeams != null) {
                    // Veriyi işleme (örneğin, loglama)
                    Log.d("DataSyncWorker", "Takımlar başarıyla senkronize edildi: $allTeams")

                    // Burada veriyi işlemek için istediğiniz başka işlemleri gerçekleştirebilirsiniz
                    // Örneğin, kullanıcıya bildirim gönderme veya başka bir API çağrısı yapma

                    Result.success()
                } else {
                    Log.e("DataSyncWorker", "API'den alınan veri boş.")
                    Result.retry()
                }
            } else {
                Log.e("DataSyncWorker", "API Hatası: ${response.code()}")
                Result.retry()
            }
        } catch (e: Exception) {
            Log.e("DataSyncWorker", "Exception: ${e.message}")
            Result.retry()
        }
    }
}
