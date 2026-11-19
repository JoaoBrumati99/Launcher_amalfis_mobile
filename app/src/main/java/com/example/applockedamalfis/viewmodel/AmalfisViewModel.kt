package com.example.applockedamalfis.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.applockedamalfis.data.SampleData
import com.example.applockedamalfis.model.AppItem
import com.example.applockedamalfis.model.BackupInfo
import com.example.applockedamalfis.model.FileItem
import com.example.applockedamalfis.model.UserProfile
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class ModalScreenType {
    NONE, FILES, SETTINGS_BACKUP, PROFILE
}

class AmalfisViewModel : ViewModel() {
    var searchQuery by mutableStateOf("")
    var selectedAppCategory by mutableStateOf("Tudo")
    
    var userProfile by mutableStateOf(SampleData.userProfile)
    var apps by mutableStateOf(SampleData.apps)
    var files by mutableStateOf(SampleData.files)
    var backupInfo by mutableStateOf(SampleData.backupInfo)
    
    // App Drawer & Desktop Pinned Apps
    var isAppDrawerOpen by mutableStateOf(false)
    var activeModalScreen by mutableStateOf(ModalScreenType.NONE)
    var desktopAppIds by mutableStateOf(setOf("1", "2", "3", "4", "5", "6", "7", "8", "9"))
    var dockAppIds by mutableStateOf(setOf("15", "18", "12", "14", "1", "9", "17")) // Chrome, Contatos, WhatsApp, Câmera, Gerenciador, AppLocked, Configurações

    var isBackupRunning by mutableStateOf(false)
    var statusMessage by mutableStateOf<String?>(null)

    val categories = listOf("Tudo", "Comunicação", "Entretenimento", "Ferramentas", "Trabalho")

    fun desktopApps(): List<AppItem> {
        return apps.filter { desktopAppIds.contains(it.id) }
    }

    fun dockApps(): List<AppItem> {
        return apps.filter { dockAppIds.contains(it.id) }
    }

    fun toggleAppOnDesktop(appId: String) {
        desktopAppIds = if (desktopAppIds.contains(appId)) {
            desktopAppIds - appId
        } else {
            desktopAppIds + appId
        }
    }

    fun toggleAppOnDock(appId: String) {
        dockAppIds = if (dockAppIds.contains(appId)) {
            dockAppIds - appId
        } else {
            if (dockAppIds.size < 8) dockAppIds + appId else dockAppIds
        }
    }

    fun filteredApps(): List<AppItem> {
        return apps
    }

    fun filteredFiles(): List<FileItem> {
        return files.filter { file ->
            searchQuery.isBlank() || file.name.contains(searchQuery, ignoreCase = true) || file.department.contains(searchQuery, ignoreCase = true)
        }
    }

    fun handleAppClick(appName: String) {
        when (appName) {
            "Configurações" -> activeModalScreen = ModalScreenType.SETTINGS_BACKUP
            "Gerenciador de ...", "Files" -> activeModalScreen = ModalScreenType.FILES
            "Amalfis", "AppLocked.Amalfis" -> activeModalScreen = ModalScreenType.PROFILE
            else -> {}
        }
    }

    fun runManualBackup() {
        isBackupRunning = true
        statusMessage = "Iniciando backup síncrono com OneDrive..."
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            isBackupRunning = false
            val time = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
            backupInfo = backupInfo.copy(lastRun = time, status = "Concluído com Sucesso")
            statusMessage = "Backup realizado com sucesso às $time"
        }, 1500)
    }

    fun toggleOfflineFile(fileId: String) {
        files = files.map { file ->
            if (file.id == fileId) file.copy(isOffline = !file.isOffline) else file
        }
    }

    fun logout() {
        userProfile = userProfile.copy(isLoggedIn = false)
    }

    fun login(email: String) {
        userProfile = userProfile.copy(isLoggedIn = true, email = email)
    }
}
