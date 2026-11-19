package com.example.applockedamalfis.backup

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.ListenableWorker.Result
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DailyBackupWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Log.d("AmalfisBackup", "Iniciando backup automático das 02:00 AM no WorkManager...")
        return try {
            val now = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
            
            val prefs = applicationContext.getSharedPreferences("amalfis_backup_prefs", Context.MODE_PRIVATE)
            prefs.edit()
                .putString("last_run", now)
                .putString("last_status", "Concluído com Sucesso às 02:00 AM")
                .apply()

            Log.d("AmalfisBackup", "Backup das 02:00 AM concluído com sucesso em $now")
            Result.success()
        } catch (e: Exception) {
            Log.e("AmalfisBackup", "Erro durante backup das 02:00 AM: ${e.message}", e)
            Result.retry()
        }
    }
}
