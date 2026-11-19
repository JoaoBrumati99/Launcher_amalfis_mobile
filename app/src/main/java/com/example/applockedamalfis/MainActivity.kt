package com.example.applockedamalfis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.applockedamalfis.backup.BackupScheduler
import com.example.applockedamalfis.ui.screens.MainScreen
import com.example.applockedamalfis.ui.theme.AppLockedAmalfisTheme
import com.example.applockedamalfis.viewmodel.AmalfisViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Schedule daily background backup at 02:00 AM via WorkManager
        BackupScheduler.scheduleDaily02AMBackup(applicationContext)

        setContent {
            AppLockedAmalfisTheme {
                val viewModel: AmalfisViewModel = viewModel()
                MainScreen(viewModel = viewModel)
            }
        }
    }
}
