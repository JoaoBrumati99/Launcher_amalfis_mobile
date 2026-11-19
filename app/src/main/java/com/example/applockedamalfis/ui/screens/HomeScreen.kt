package com.example.applockedamalfis.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.applockedamalfis.model.AppItem
import com.example.applockedamalfis.viewmodel.AmalfisViewModel
import com.example.applockedamalfis.viewmodel.ModalScreenType
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(viewModel: AmalfisViewModel) {
    val currentTime = remember {
        SimpleDateFormat("HH:mm", Locale("pt", "BR")).format(Date())
    }
    val currentDateShort = remember {
        SimpleDateFormat("EEE, d/MMM", Locale("pt", "BR")).format(Date()).replace(".", "").replaceFirstChar { it.uppercase() }
    }
    val currentDayNum = remember {
        SimpleDateFormat("d", Locale("pt", "BR")).format(Date())
    }
    val currentDayName = remember {
        SimpleDateFormat("EEE", Locale("pt", "BR")).format(Date()).replace(".", "").replaceFirstChar { it.uppercase() }
    }

    // Animate home desktop content alpha so desktop icons hide when drawer opens
    val homeDesktopAlpha by animateFloatAsState(
        targetValue = if (viewModel.isAppDrawerOpen) 0f else 1f,
        animationSpec = tween(durationMillis = 200),
        label = "HomeDesktopAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount < -30) {
                        viewModel.isAppDrawerOpen = true
                    } else if (dragAmount > 30) {
                        viewModel.isAppDrawerOpen = false
                    }
                }
            }
    ) {
        // 1. Ultra Fast Cached Silk Wallpaper (Stays rendered in background)
        SilkWallpaperBackground()

        // 2. Home Screen Content (Desktop Icons & Dock fade out when drawer opens to prevent overlap)
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Status Bar (Always visible at top)
            TopStatusBar(timeText = currentTime, dateText = currentDateShort)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .graphicsLayer { alpha = homeDesktopAlpha }, // Smoothly fades out when drawer is open
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                // Desktop Pinned Apps Grid (4 columns)
                val desktopApps = viewModel.desktopApps()
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    items(desktopApps, key = { it.id }) { app ->
                        AppIconItem(
                            app = app,
                            currentDayName = currentDayName,
                            currentDayNum = currentDayNum,
                            isDrawer = false,
                            onClick = {
                                viewModel.handleAppClick(app.name)
                            },
                            onRemoveFromDesktop = {
                                viewModel.toggleAppOnDesktop(app.id)
                            }
                        )
                    }
                }

                // Bottom Area: Upward Arrow & 8-Icon Dock
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { viewModel.isAppDrawerOpen = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.KeyboardArrowUp,
                            contentDescription = "Abrir Gaveta de Apps",
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Translucent Dock Container with 8 icons
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp),
                        shape = RoundedCornerShape(26.dp),
                        color = Color.White.copy(alpha = 0.22f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            DockIcon(type = "Phone")
                            DockIcon(type = "Chrome")
                            DockIcon(type = "Messages")
                            DockIcon(type = "Support")
                            DockIcon(type = "Camera")
                            DockIcon(type = "AppLocked") { viewModel.activeModalScreen = ModalScreenType.PROFILE }
                            DockIcon(type = "Files") { viewModel.activeModalScreen = ModalScreenType.FILES }
                            DockIcon(type = "WhatsApp")
                        }
                    }
                }
            }
        }

        // 3. Fast App Drawer Overlay (Slides up over Wallpaper cleanly without desktop icon overlap)
        AnimatedVisibility(
            visible = viewModel.isAppDrawerOpen,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.50f)) // 50% translucent backdrop over Wallpaper
                    .clickable { viewModel.isAppDrawerOpen = false }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 36.dp, start = 16.dp, end = 16.dp)
                        .clickable(enabled = false) {}
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Gaveta de Aplicativos",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = { viewModel.isAppDrawerOpen = false },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.KeyboardArrowDown,
                                contentDescription = "Fechar Gaveta",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.20f), thickness = 1.dp)

                    val allApps = viewModel.apps
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        items(allApps, key = { it.id }) { app ->
                            val isOnDesktop = viewModel.desktopAppIds.contains(app.id)
                            AppIconItem(
                                app = app,
                                currentDayName = currentDayName,
                                currentDayNum = currentDayNum,
                                isOnDesktop = isOnDesktop,
                                isDrawer = true,
                                onClick = {
                                    viewModel.handleAppClick(app.name)
                                },
                                onAddToDesktop = {
                                    viewModel.toggleAppOnDesktop(app.id)
                                },
                                onRemoveFromDesktop = {
                                    viewModel.toggleAppOnDesktop(app.id)
                                }
                            )
                        }
                    }
                }
            }
        }

        // 4. Modal Screens (Configurações, Files, Perfil)
        if (viewModel.activeModalScreen != ModalScreenType.NONE) {
            Dialog(
                onDismissRequest = { viewModel.activeModalScreen = ModalScreenType.NONE },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 32.dp, bottom = 16.dp, start = 12.dp, end = 12.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(onClick = { viewModel.activeModalScreen = ModalScreenType.NONE }) {
                                Icon(Icons.Default.Close, contentDescription = "Fechar")
                            }
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            when (viewModel.activeModalScreen) {
                                ModalScreenType.FILES -> FilesScreen(viewModel)
                                ModalScreenType.SETTINGS_BACKUP -> SettingsBackupScreen(viewModel)
                                ModalScreenType.PROFILE -> ProfileScreen(viewModel)
                                ModalScreenType.NONE -> {}
                            }
                        }
                    }
                }
            }
        }
    }
}

// Top System Status Bar matching the screenshot
@Composable
fun TopStatusBar(timeText: String, dateText: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "$timeText $dateText",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(Icons.Default.Wifi, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFF4CAF50),
                modifier = Modifier.padding(start = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text("58", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppIconItem(
    app: AppItem,
    currentDayName: String,
    currentDayNum: String,
    isOnDesktop: Boolean = false,
    isDrawer: Boolean = false,
    onClick: () -> Unit,
    onRemoveFromDesktop: (() -> Unit)? = null,
    onAddToDesktop: (() -> Unit)? = null
) {
    var showMenu by remember { mutableStateOf(false) }

    Box(contentAlignment = Alignment.TopCenter) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = { showMenu = true }
                )
                .padding(2.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = getAppBackgroundColor(app.name),
                    shadowElevation = 4.dp,
                    modifier = Modifier.size(58.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        RenderAppGraphic(appName = app.name, currentDayName = currentDayName, currentDayNum = currentDayNum)
                    }
                }

                if (app.badge != null) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFE53935),
                        modifier = Modifier
                            .size(18.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = 4.dp, y = (-4).dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(app.badge, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = app.name,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Long-Press Context Menu
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            if (isDrawer) {
                if (isOnDesktop) {
                    DropdownMenuItem(
                        text = { Text("Remover da Área de Trabalho", color = Color(0xFFE53935)) },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFE53935)) },
                        onClick = {
                            showMenu = false
                            onRemoveFromDesktop?.invoke()
                        }
                    )
                } else {
                    DropdownMenuItem(
                        text = { Text("Adicionar à Área de Trabalho") },
                        leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) },
                        onClick = {
                            showMenu = false
                            onAddToDesktop?.invoke()
                        }
                    )
                }
            } else {
                DropdownMenuItem(
                    text = { Text("Remover da Área de Trabalho", color = Color(0xFFE53935)) },
                    leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFE53935)) },
                    onClick = {
                        showMenu = false
                        onRemoveFromDesktop?.invoke()
                    }
                )
            }
        }
    }
}

@Composable
fun DockIcon(type: String, onClick: (() -> Unit)? = null) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = when (type) {
            "Phone" -> Color(0xFF4CAF50)
            "WhatsApp" -> Color(0xFF25D366)
            "AppLocked" -> Color(0xFF4CAF50)
            "Files" -> Color(0xFFFFB300)
            "Messages" -> Color(0xFF1E88E5)
            "Support" -> Color(0xFF0288D1)
            "Camera" -> Color(0xFF37474F)
            else -> Color.White
        },
        shadowElevation = 2.dp,
        modifier = Modifier
            .size(46.dp)
            .clickable(enabled = onClick != null) { onClick?.invoke() }
    ) {
        Box(contentAlignment = Alignment.Center) {
            when (type) {
                "Phone" -> Icon(Icons.Default.Call, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                "WhatsApp" -> Icon(Icons.Default.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                "AppLocked" -> Icon(Icons.Default.Android, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
                "Files" -> Icon(Icons.Default.Folder, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                "Messages" -> Icon(Icons.AutoMirrored.Filled.Message, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                "Support" -> Icon(Icons.AutoMirrored.Filled.Help, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                "Camera" -> Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                "Chrome" -> ChromeGraphic()
            }
        }
    }
}

fun getAppBackgroundColor(appName: String): Color {
    return when (appName) {
        "Gerenciador de ...", "Files" -> Color(0xFFFFB300)
        "Agenda" -> Color.White
        "Anotações" -> Color(0xFFFFB300)
        "Mi Canvas" -> Color.White
        "Netflix", "Relógio", "Câmera", "Digitalizador", "Gravador" -> Color(0xFF1E1E1E)
        "EasyTime" -> Color.Black
        "TKRON Mobile" -> Color.White
        "Amalfis" -> Color(0xFFE65100)
        "AppLocked.Amalfis" -> Color(0xFF4CAF50)
        "WhatsApp", "Downloads" -> Color(0xFF25D366)
        "Calculadora", "Drive", "Fotos", "Galeria", "Gmail", "Google", "Google One", "Google TV", "Maps" -> Color.White
        "Clima" -> Color(0xFF29B6F6)
        "Configurações" -> Color(0xFFE0E0E0)
        "Contatos" -> Color(0xFF1976D2)
        "Emergência" -> Color.White
        else -> Color.White
    }
}

@Composable
fun RenderAppGraphic(appName: String, currentDayName: String, currentDayNum: String) {
    when (appName) {
        "Agenda" -> {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(currentDayName, color = Color(0xFF1976D2), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(currentDayNum, color = Color.Black, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
        "Gerenciador de ...", "Files" -> Icon(Icons.Default.Folder, contentDescription = null, tint = Color.White, modifier = Modifier.size(30.dp))
        "Anotações" -> Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
        "Mi Canvas" -> Icon(Icons.Default.Brush, contentDescription = null, tint = Color(0xFFE91E63), modifier = Modifier.size(28.dp))
        "Netflix" -> Text("N", color = Color(0xFFE50914), fontSize = 32.sp, fontWeight = FontWeight.Black)
        "EasyTime" -> Icon(Icons.Default.Timer, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
        "TKRON Mobile" -> Text("TK", color = Color(0xFF0D47A1), fontSize = 22.sp, fontWeight = FontWeight.Black)
        "Amalfis" -> Icon(Icons.Default.Navigation, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
        "AppLocked.Amalfis" -> Icon(Icons.Default.Android, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
        "WhatsApp" -> Icon(Icons.Default.Call, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
        "Calculadora" -> Icon(Icons.Default.Calculate, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(28.dp))
        "Câmera" -> Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
        "Chrome" -> ChromeGraphic()
        "Clima" -> Icon(Icons.Default.WbSunny, contentDescription = null, tint = Color(0xFFFFD54F), modifier = Modifier.size(28.dp))
        "Configurações" -> Icon(Icons.Default.Settings, contentDescription = null, tint = Color(0xFF424242), modifier = Modifier.size(28.dp))
        "Contatos" -> Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
        "Digitalizador" -> Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
        "Downloads" -> Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
        "Drive" -> Icon(Icons.Default.AddToDrive, contentDescription = null, tint = Color(0xFF0F9D58), modifier = Modifier.size(28.dp))
        "Emergência" -> Icon(Icons.Default.LocalHospital, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(28.dp))
        "Fotos" -> Icon(Icons.Default.FilterVintage, contentDescription = null, tint = Color(0xFFF4B400), modifier = Modifier.size(28.dp))
        "Galeria" -> Icon(Icons.Default.Collections, contentDescription = null, tint = Color(0xFFE91E63), modifier = Modifier.size(28.dp))
        "Gmail" -> Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFFEA4335), modifier = Modifier.size(28.dp))
        "Google" -> Text("G", color = Color(0xFF4285F4), fontSize = 28.sp, fontWeight = FontWeight.Bold)
        "Google One" -> Text("1", color = Color(0xFF4285F4), fontSize = 28.sp, fontWeight = FontWeight.Bold)
        "Google TV" -> Icon(Icons.Default.Tv, contentDescription = null, tint = Color(0xFF4285F4), modifier = Modifier.size(28.dp))
        "Gravador" -> Icon(Icons.Default.GraphicEq, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
        "Maps" -> Icon(Icons.Default.Place, contentDescription = null, tint = Color(0xFFEA4335), modifier = Modifier.size(28.dp))
        "Relógio" -> Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
        else -> Icon(Icons.Default.Apps, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(28.dp))
    }
}

@Composable
fun ChromeGraphic() {
    Surface(
        shape = CircleShape,
        color = Color(0xFF4285F4),
        modifier = Modifier.size(26.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Surface(shape = CircleShape, color = Color.White, modifier = Modifier.size(10.dp)) {}
        }
    }
}

// Ultra Fast Cached Silk Wallpaper
@Composable
fun SilkWallpaperBackground() {
    Spacer(
        modifier = Modifier
            .fillMaxSize()
            .drawWithCache {
                val width = size.width
                val height = size.height

                val bgBrush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF091428),
                        Color(0xFF0D1F3C),
                        Color(0xFF0F172A)
                    )
                )

                val path1 = Path().apply {
                    moveTo(0f, height * 0.15f)
                    cubicTo(
                        width * 0.6f, height * 0.1f,
                        width * 0.7f, height * 0.6f,
                        0f, height * 0.9f
                    )
                    close()
                }
                val brush1 = Brush.linearGradient(
                    colors = listOf(Color(0xFF4A90E2), Color(0xFF1D5299), Color(0xFF0D284B)),
                    start = Offset(0f, 0f),
                    end = Offset(width * 0.7f, height * 0.6f)
                )

                val path2 = Path().apply {
                    moveTo(width * 0.3f, height)
                    cubicTo(
                        width * 0.45f, height * 0.45f,
                        width * 0.85f, height * 0.35f,
                        width, height * 0.1f
                    )
                    lineTo(width, height)
                    close()
                }
                val brush2 = Brush.linearGradient(
                    colors = listOf(Color(0xFF9C27B0), Color(0xFF673AB7), Color(0xFF1A237E)),
                    start = Offset(width * 0.3f, height),
                    end = Offset(width, height * 0.2f)
                )

                val path3 = Path().apply {
                    moveTo(width * 0.4f, height)
                    cubicTo(
                        width * 0.55f, height * 0.6f,
                        width * 0.9f, height * 0.5f,
                        width, height * 0.65f
                    )
                    lineTo(width, height)
                    close()
                }
                val brush3 = Brush.linearGradient(
                    colors = listOf(Color(0xFFE1BEE7), Color(0xFFB388FF), Color(0xFF3F51B5)),
                    start = Offset(width * 0.4f, height),
                    end = Offset(width, height * 0.65f)
                )

                onDrawBehind {
                    drawRect(brush = bgBrush)
                    drawPath(path = path1, brush = brush1)
                    drawPath(path = path2, brush = brush2)
                    drawPath(path = path3, brush = brush3, alpha = 0.85f)
                }
            }
    )
}
