package com.example.applockedamalfis.model

data class AppItem(
    val id: String,
    val name: String,
    val category: String, // Tudo, Comunicação, Trabalho, Ferramentas
    val packageName: String,
    val isAuthorized: Boolean = true,
    val badge: String? = null,
    val description: String = ""
)

data class FileItem(
    val id: String,
    val name: String,
    val type: String, // PDF, XLSX, DOCX
    val source: String, // SharePoint, OneDrive
    val size: String,
    val updatedAt: String,
    val isOffline: Boolean = false,
    val department: String
)

data class BackupInfo(
    val frequency: String,
    val status: String,
    val lastRun: String,
    val totalItems: Int,
    val totalSize: String
)

data class UserProfile(
    val name: String,
    val email: String,
    val department: String,
    val deviceId: String,
    val managementMode: String,
    val tenantId: String,
    val isLoggedIn: Boolean = true
)
