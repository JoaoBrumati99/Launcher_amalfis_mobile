package com.example.applockedamalfis.data

import com.example.applockedamalfis.model.AppItem
import com.example.applockedamalfis.model.BackupInfo
import com.example.applockedamalfis.model.FileItem
import com.example.applockedamalfis.model.UserProfile

object SampleData {
    val userProfile = UserProfile(
        name = "Carlos Eduardo Silva",
        email = "carlos.silva@amalfis.com.br",
        department = "Operações / Produção T.I.",
        deviceId = "AMF-MOB-2026-8842",
        managementMode = "Fully Managed (Device Owner)",
        tenantId = "amalfis.onmicrosoft.com",
        isLoggedIn = true
    )

    val apps = listOf(
        // Desktop Apps initially shown
        AppItem("1", "Gerenciador de ...", "Ferramentas", "com.miui.filemanager", true, null, "Gerenciador de arquivos corporativos"),
        AppItem("2", "Agenda", "Ferramentas", "com.google.android.calendar", true, null, "Agenda e compromissos"),
        AppItem("3", "Anotações", "Ferramentas", "com.miui.notes", true, null, "Bloco de notas rápido"),
        AppItem("4", "Mi Canvas", "Ferramentas", "com.miui.canvas", true, null, "Desenho e esquemas"),
        AppItem("5", "Netflix", "Entretenimento", "com.netflix.mediaclient", true, null, "Mídia e vídeos"),
        AppItem("6", "EasyTime", "Trabalho", "com.easytime.app", true, null, "Ponto eletrônico corporativo"),
        AppItem("7", "TKRON Mobile", "Trabalho", "com.tkron.mobile", true, null, "Sistema de chamados e OS"),
        AppItem("8", "Amalfis", "Trabalho", "com.amalfis.main", true, null, "Portal corporativo Amalfis"),
        AppItem("9", "AppLocked.Amalfis", "Trabalho", "com.example.applockedamalfis", true, null, "Launcher e segurança Amalfis"),
        
        // Additional Apps for Drawer
        AppItem("10", "Maps", "Ferramentas", "com.google.android.apps.maps", true, null, "Navegação GPS"),
        AppItem("11", "Relógio", "Ferramentas", "com.miui.deskclock", true, null, "Alarme e cronômetro"),
        AppItem("12", "WhatsApp", "Comunicação", "com.whatsapp", true, null, "Mensagens corporativas"),
        AppItem("13", "Calculadora", "Ferramentas", "com.miui.calculator", true, null, "Calculadora"),
        AppItem("14", "Câmera", "Ferramentas", "com.android.camera2", true, null, "Câmera de fotos"),
        AppItem("15", "Chrome", "Comunicação", "com.android.chrome", true, null, "Navegador web"),
        AppItem("16", "Clima", "Ferramentas", "com.miui.weather2", true, null, "Previsão do tempo"),
        AppItem("17", "Configurações", "Ferramentas", "com.android.settings", true, "1", "Painel do sistema"),
        AppItem("18", "Contatos", "Comunicação", "com.android.contacts", true, null, "Contatos"),
        AppItem("19", "Digitalizador", "Ferramentas", "com.miui.scanner", true, null, "Leitor QR e documentos"),
        AppItem("20", "Downloads", "Ferramentas", "com.android.providers.downloads.ui", true, null, "Gerenciador de downloads"),
        AppItem("21", "Drive", "Trabalho", "com.google.android.apps.docs", true, null, "Google Drive corporativo"),
        AppItem("22", "Emergência", "Ferramentas", "com.android.emergency", true, null, "Contatos de emergência"),
        AppItem("23", "Files", "Ferramentas", "com.google.android.apps.nbu.files", true, null, "Arquivos"),
        AppItem("24", "Fotos", "Entretenimento", "com.google.android.apps.photos", true, null, "Galeria de fotos Google"),
        AppItem("25", "Galeria", "Entretenimento", "com.miui.gallery", true, null, "Galeria local"),
        AppItem("26", "Gmail", "Comunicação", "com.google.android.gm", true, null, "E-mail corporativo"),
        AppItem("27", "Google", "Comunicação", "com.google.android.googlequicksearchbox", true, null, "Busca Google"),
        AppItem("28", "Google One", "Ferramentas", "com.google.android.apps.subscriptions.red", true, null, "Armazenamento"),
        AppItem("29", "Google TV", "Entretenimento", "com.google.android.videos", true, null, "Vídeos e mídias"),
        AppItem("30", "Gravador", "Ferramentas", "com.miui.notes.recorder", true, null, "Gravador de áudio")
    )

    val files = listOf(
        FileItem("f1", "Manual de Procedimentos de Costura 2026.pdf", "PDF", "SharePoint", "4.2 MB", "16/02/2026", true, "Produção"),
        FileItem("f2", "Tabela de Cores e Tecidos Homologados.xlsx", "XLSX", "SharePoint", "1.8 MB", "14/02/2026", true, "Qualidade"),
        FileItem("f3", "Política de Segurança da Informação Amalfis.pdf", "PDF", "SharePoint", "850 KB", "10/01/2026", false, "T.I."),
        FileItem("f4", "Formulário de Relatório de Incidente Operacional.docx", "DOCX", "SharePoint", "520 KB", "01/02/2026", false, "Operações"),
        FileItem("f5", "Notas Pessoais de Turno - OneDrive.docx", "DOCX", "OneDrive", "310 KB", "17/02/2026", true, "Pessoal")
    )

    val backupInfo = BackupInfo(
        frequency = "Diário (02:00 AM)",
        status = "Concluído com Sucesso",
        lastRun = "Hoje, 02:15",
        totalItems = 142,
        totalSize = "24.6 MB"
    )
}
