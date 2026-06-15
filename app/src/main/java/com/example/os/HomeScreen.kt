package com.example.os

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

// Color scheme specific to our Pixel Max OS with Frosted Glass Theme
val CyberTeal = Color(0xFF00F5D4)
val CyberPink = Color(0xFFC77DFF) // Softened pink for frosted contrast
val NeonBlue = Color(0xFF3A86FF)  // Vibrant sapphire blue
val FrostWhite = Color(0xE6FFFFFF)
val DarkGreyBg = Color(0xFF030306) // Deep space base
val CardBg = Color(0x1BFFFFFF)     // Translucent primary glass base (approx 11% white overlay)
val GlassBorder = Color(0x38FFFFFF) // Premium white frost outline
val AlertRed = Color(0xFFFF4D4D)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: OSViewModel,
    modifier: Modifier = Modifier
) {
    val osStatus by viewModel.osStatus.collectAsState()
    val systemLogs by viewModel.systemLogs.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val cpuCoreLoads by viewModel.cpuCoreLoads.collectAsState()
    val installedApps by viewModel.installedApps.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val focusManager = LocalFocusManager.current

    var userMessageText by remember { mutableStateOf("") }
    var showApiKeyInput by remember { mutableStateOf(false) }
    var showAddAppDialog by remember { mutableStateOf(false) }

    var newAppNameInput by remember { mutableStateOf("") }

    // Scroll to latest message on size change
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            lazyListState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(DarkGreyBg),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeveloperMode,
                            contentDescription = "OS Icon",
                            tint = CyberTeal,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "PIXEL MAX AI-OS v17.0",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.45f),
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFF030306))
                .drawBehind {
                    // Top-Left Blue Ambient Light
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0x3D3A86FF), Color.Transparent),
                            radius = size.width * 1.0f,
                            center = androidx.compose.ui.geometry.Offset(-size.width * 0.1f, -size.height * 0.05f)
                        ),
                        radius = size.width * 1.0f,
                        center = androidx.compose.ui.geometry.Offset(-size.width * 0.1f, -size.height * 0.05f)
                    )
                    // Bottom-Right Purple Ambient Light
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0x33C77DFF), Color.Transparent),
                            radius = size.width * 1.1f,
                            center = androidx.compose.ui.geometry.Offset(size.width * 1.1f, size.height * 0.98f)
                        ),
                        radius = size.width * 1.1f,
                        center = androidx.compose.ui.geometry.Offset(size.width * 1.1f, size.height * 0.98f)
                    )
                    // Mid-Right Cyan Ambient Light
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0x2600F5D4), Color.Transparent),
                            radius = size.width * 0.8f,
                            center = androidx.compose.ui.geometry.Offset(size.width * 0.95f, size.height * 0.35f)
                        ),
                        radius = size.width * 0.8f,
                        center = androidx.compose.ui.geometry.Offset(size.width * 0.95f, size.height * 0.35f)
                    )
                }
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Dynamic Grand Display Clock (Theme: Frosted Glass Clock & Date)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var currentTimeString by remember { mutableStateOf("") }
                var currentDateString by remember { mutableStateOf("") }

                LaunchedEffect(Unit) {
                    while (true) {
                        currentTimeString = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
                        currentDateString = java.text.SimpleDateFormat("EEEE, d MMMM yyyy", java.util.Locale.getDefault()).format(java.util.Date())
                        kotlinx.coroutines.delay(1000)
                    }
                }

                Text(
                    text = currentTimeString.ifEmpty { "09:41" },
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 72.sp,
                        fontWeight = FontWeight.ExtraLight,
                        letterSpacing = (-2).sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                )
                Text(
                    text = currentDateString.ifEmpty { "Tuesday, October 24" }.uppercase(),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                )
            }

            // SECTION 1: SYSTEM QUICK STATS (OS, Chip, Performance)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quick_stats_card"),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, GlassBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "[STATUS: ONLINE - ${osStatus.efficiencyPercent}]",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                color = CyberTeal,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh rate",
                                tint = NeonBlue,
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(end = 4.dp)
                            )
                            Text(
                                text = "${osStatus.refreshRateHz}Hz Dynamic Screen",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    color = NeonBlue,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Simulated Dynamic System Tickers
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 240Hz vs 60Hz selector buttons
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Screen Rate Toggle",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Button(
                                    onClick = { viewModel.setRefreshRate(240) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(36.dp)
                                        .testTag("rate_240_btn"),
                                    contentPadding = PaddingValues(0.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (osStatus.refreshRateHz == 240) CyberTeal else Color.DarkGray
                                    )
                                ) {
                                    Text("240Hz", fontSize = 11.sp, color = if (osStatus.refreshRateHz == 240) Color.Black else Color.White)
                                }
                                Button(
                                    onClick = { viewModel.setRefreshRate(60) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(36.dp)
                                        .testTag("rate_60_btn"),
                                    contentPadding = PaddingValues(0.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (osStatus.refreshRateHz == 60) NeonBlue else Color.DarkGray
                                    )
                                ) {
                                    Text("60Hz", fontSize = 11.sp, color = Color.White)
                                }
                            }
                        }

                        // Performance Boost Action button
                        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                            Text(
                                text = "OS Optimizer Tool",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Button(
                                onClick = { viewModel.runBenchmark() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(36.dp)
                                    .testTag("benchmark_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberPink)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Bolt, contentDescription = "Benchmark", modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("AnTuTu Test", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            // SECTION 2: HARDWARE WIDGETS (ROW 1: SNAPDRAGON CHIP & THERMALS)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Qualcomm Snapdragon 8 Gen 6 Max widget
                Card(
                    modifier = Modifier
                        .weight(1.1f)
                        .height(180.dp)
                        .testTag("snapdragon_card"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = BorderStroke(1.dp, GlassBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Bolt, contentDescription = "CPU", tint = CyberTeal, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Snapdragon 8 Gen 6 Max",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp),
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Clock Speed: ${osStatus.clockSpeedGhz} GHz",
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                            color = Color.LightGray
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Render 8 core utilization indicators
                        Text("Real-Time Octa-Core Workload:", fontSize = 10.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            cpuCoreLoads.forEach { load ->
                                val barColor = when {
                                    load > 45 -> AlertRed
                                    load > 15 -> NeonBlue
                                    else -> CyberTeal
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(Color.DarkGray)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight(load.toFloat() / 100f)
                                            .align(Alignment.BottomCenter)
                                            .background(barColor)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Core 1-4 (Idle)", fontSize = 8.sp, color = Color.DarkGray)
                            Text("Core 5-8 (Boost)", fontSize = 8.sp, color = CyberTeal)
                        }
                    }
                }

                // Sub-Zero Liquid Nitrogen Temperature Widget
                Card(
                    modifier = Modifier
                        .weight(0.9f)
                        .height(180.dp)
                        .testTag("thermals_card"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = BorderStroke(1.dp, GlassBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Thermostat, contentDescription = "Thermometer", tint = NeonBlue, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Liquid Nitrogen",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp),
                                color = Color.White
                            )
                        }

                        // Giant real-time temperature visual
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            val pulseTemp = osStatus.cpuTempCelsius
                            Text(
                                text = "${pulseTemp}°C",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = if (osStatus.maxFrostActive) CyberTeal else NeonBlue
                            )
                            Text(
                                text = if (osStatus.maxFrostActive) "FROST MAX (Active)" else "SUB-ZERO DINGIN",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (osStatus.maxFrostActive) CyberTeal else Color.LightGray
                            )
                        }

                        // Interactive Frost Coolant trigger
                        Button(
                            onClick = { viewModel.triggerLiquidNitrogenPulse() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                                .testTag("frost_pulse_btn"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (osStatus.maxFrostActive) CyberTeal else NeonBlue
                            ),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Bolt, contentDescription = "Pulse", modifier = Modifier.size(12.dp), tint = Color.Black)
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = if (osStatus.maxFrostActive) "Normal Nitrogen" else "Nitrogen Pulse",
                                    fontSize = 10.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // SECTION 3: REVENUE STORAGE WIDGET (4 TB UFS 5.5) & LPDDR6 RAM WIDGET
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("storage_ram_card"),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, GlassBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // RAM part
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Memory, contentDescription = "RAM LPDDR6", tint = CyberTeal)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Ultra-RAM LPDDR6: 128 GB",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                        Button(
                            onClick = { viewModel.optimizeRam() },
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("boost_ram_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberTeal),
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) {
                            Text("Boost RAM", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Terpakai: ${String.format("%.2f", osStatus.usedRamGb)} GB / 128.00 GB (Efisiensi 100% No Bottleneck)",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0x22FFFFFF))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(((osStatus.usedRamGb / 128.0).coerceIn(0.0, 1.0)).toFloat())
                                .fillMaxHeight()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(CyberPink, NeonBlue, CyberTeal)
                                    )
                                )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color.DarkGray, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Storage 4 TB part
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Storage, contentDescription = "Storage", tint = NeonBlue)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Extreme-Speed Storage UFS 5.5: 4 TB",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                        Button(
                            onClick = { showAddAppDialog = true },
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("install_app_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonBlue),
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) {
                            Text("Download App", fontSize = 11.sp, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    val freeSt = 4.0 - osStatus.usedStorageTb
                    Text(
                        text = "Terpakai: ${String.format("%.3f", osStatus.usedStorageTb)} TB / 4.00 TB (Sisa: ${String.format("%.3f", freeSt)} TB)",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0x22FFFFFF))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(((osStatus.usedStorageTb / 4.0).coerceIn(0.0, 1.0)).toFloat())
                                .fillMaxHeight()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(NeonBlue, CyberTeal, Color.White)
                                    )
                                )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Terinstal di UFS Storage:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(6.dp))

                    // App installations chips
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(installedApps) { app ->
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color.Black)
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                    .clickable { viewModel.deleteApp(app) },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(app, fontSize = 10.sp, color = Color.White)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.Delete, contentDescription = "Uninstall", tint = AlertRed, modifier = Modifier.size(10.dp))
                            }
                        }
                    }
                }
            }

            // SECTION 4: ZEISS/LEICA CAMERA MATRIX SIMULATOR
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("camera_matrix_card"),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, GlassBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CameraAlt, contentDescription = "Camera Co-Eng", tint = CyberPink)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ZEISS & Leica Camera Optics",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Sensor Dual-Engine sum-lens (50.0 MP, f/1.4 T* anti-reflection matrix layer)",
                        fontSize = 11.sp,
                        color = Color.LightGray
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.capturePhoto("ZEISS") },
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .testTag("capture_zeiss_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberPink)
                        ) {
                            Text("ZEISS T* Bokeh", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.capturePhoto("LEICA") },
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .testTag("capture_leica_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                        ) {
                            Text("Leica Monochrome", fontSize = 11.sp)
                        }
                    }
                }
            }

            // SECTION 5: LIVE SYSTEM JOURNAL (REAL-TIME ACTIVITY LOGS)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .testTag("system_logs_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0x33000000)),
                border = BorderStroke(1.dp, GlassBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "🧾 LIVE SYSTEM KERNEL JOURNAL (REAL-TIME):",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = CyberTeal
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(modifier = Modifier.fillMaxSize()) {
                        val scrollState = rememberScrollState()
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                        ) {
                            systemLogs.forEach { log ->
                                Text(
                                    text = log,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    color = Color.LightGray,
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            // SECTION 6: PIXEL NEURAL INTELIGENCE (GEMINI CHAT CORE)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ai_console_card"),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, GlassBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header neural interface
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DeveloperMode, contentDescription = "Neural Core", tint = CyberTeal)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Pixel Neural Core AI",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = "Model: gemini-3.1-pro-preview (Thinking HIGH)",
                                    fontSize = 9.sp,
                                    color = CyberTeal
                                )
                            }
                        }

                        // API Key Configurations Toggle
                        IconButton(
                            onClick = { showApiKeyInput = !showApiKeyInput },
                            modifier = Modifier.testTag("key_toggle_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = "API Keys Configuration",
                                tint = if (osStatus.customApiKey.isNotEmpty()) CyberTeal else Color.Gray
                            )
                        }
                    }

                    // Collapsible API Key Config Form securely handled
                    AnimatedVisibility(visible = showApiKeyInput) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp)
                        ) {
                            Text(
                                text = "Ganti API Key Gemini (Opsional jika BuildConfig kosong):",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                var tempKey by remember { mutableStateOf(osStatus.customApiKey) }
                                OutlinedTextField(
                                    value = tempKey,
                                    onValueChange = { tempKey = it },
                                    placeholder = { Text("Masukkan AI Studio API Key", fontSize = 12.sp) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(50.dp)
                                        .testTag("api_key_input"),
                                    textStyle = TextStyle(color = Color.White, fontSize = 12.sp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = CyberTeal,
                                        unfocusedBorderColor = Color.DarkGray
                                    )
                                )
                                Button(
                                    onClick = {
                                        viewModel.updateApiKey(tempKey)
                                        showApiKeyInput = false
                                    },
                                    modifier = Modifier.testTag("save_key_btn"),
                                    colors = ButtonDefaults.buttonColors(containerColor = CyberTeal)
                                ) {
                                    Text("Simpan", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text(
                                text = "⚠️ Peringatan: Jangan pernah membagikan kunci API Anda kepada siapa pun.",
                                fontSize = 9.sp,
                                color = AlertRed,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color.DarkGray, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Active chat box area (Frosted Inner Console)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .background(Color.Black.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                            .border(1.dp, GlassBorder.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                            .padding(8.dp)
                    ) {
                        LazyColumn(
                            state = lazyListState,
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(chatMessages) { message ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 4.dp)
                                ) {
                                    val bubbleBg = when (message.sender) {
                                        "User" -> Brush.horizontalGradient(listOf(CyberPink.copy(alpha = 0.25f), Color(0x11FFFFFF)))
                                        "System" -> Brush.horizontalGradient(listOf(AlertRed.copy(alpha = 0.25f), Color(0x11FFFFFF)))
                                        else -> Brush.horizontalGradient(listOf(NeonBlue.copy(alpha = 0.25f), Color(0x11FFFFFF)))
                                    }
                                    val labelColor = when (message.sender) {
                                        "User" -> CyberPink
                                        "System" -> AlertRed
                                        else -> CyberTeal
                                    }

                                    Box(
                                        modifier = Modifier
                                            .align(if (message.sender == "User") Alignment.End else Alignment.Start)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(bubbleBg)
                                            .border(
                                                1.dp,
                                                if (message.isError) AlertRed.copy(alpha = 0.5f) else labelColor.copy(alpha = 0.2f),
                                                RoundedCornerShape(12.dp)
                                            )
                                            .padding(12.dp)
                                    ) {
                                        Column {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = if (message.sender == "User") "ADMINISTRATOR" else message.sender.uppercase(),
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = labelColor,
                                                    fontFamily = FontFamily.Monospace
                                                )
                                                Text(
                                                    text = message.timestamp,
                                                    fontSize = 8.sp,
                                                    color = Color.Gray,
                                                    fontFamily = FontFamily.Monospace
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = message.text,
                                                fontSize = 12.sp,
                                                color = Color.White,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }

                            if (isGenerating) {
                                item {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        horizontalArrangement = Arrangement.Start,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            color = CyberTeal,
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = "Snapragon AI sedang memproses pemikiran (Thinking Level: HIGH)...",
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = CyberTeal
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Input console fields
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = userMessageText,
                            onValueChange = { userMessageText = it },
                            placeholder = { Text("Ketik perintah sistem OS di sini...", fontSize = 12.sp, color = Color.Gray) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("console_msg_input"),
                            textStyle = TextStyle(color = Color.White, fontSize = 13.sp),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberTeal,
                                unfocusedBorderColor = Color.DarkGray
                            )
                        )

                        IconButton(
                            onClick = {
                                if (userMessageText.isNotBlank()) {
                                    viewModel.sendUserMessage(userMessageText)
                                    userMessageText = ""
                                    focusManager.clearFocus()
                                }
                            },
                            enabled = !isGenerating && userMessageText.isNotBlank(),
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (userMessageText.isNotBlank()) CyberTeal else Color.DarkGray)
                                .size(48.dp)
                                .testTag("console_send_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send Command",
                                tint = if (userMessageText.isNotBlank()) Color.Black else Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Interactive add app dialog handles installer simulation safely
    if (showAddAppDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddAppDialog = false
                newAppNameInput = ""
            },
            title = {
                Text("Google Play Store: Snapdragon Max Downloader", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            },
            text = {
                Column {
                    Text("Pilih atau masukkan aplikasi performa tinggi untuk diinstal pada penyimpanan UFS 5.5 4 TB.", fontSize = 12.sp, color = Color.LightGray)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = newAppNameInput,
                        onValueChange = { newAppNameInput = it },
                        label = { Text("Nama Aplikasi") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_app_name_input"),
                        textStyle = TextStyle(color = Color.White)
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Rekomendasi Cepat:", fontSize = 11.sp, color = CyberTeal)
                    Spacer(modifier = Modifier.height(6.dp))

                    val recApps = listOf("Genshin Impact Max v9", "Leica Cinema Pro Cam", "240Hz Unreal Renderer", "AnTuTu Extreme Benchmark")
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        recApps.forEach { rec ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.DarkGray)
                                    .clickable { newAppNameInput = rec }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(rec, fontSize = 10.sp, color = Color.White)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newAppNameInput.isNotBlank()) {
                            viewModel.simulateInstallApp(newAppNameInput)
                            showAddAppDialog = false
                            newAppNameInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberTeal)
                ) {
                    Text("Instal", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddAppDialog = false
                    newAppNameInput = ""
                }) {
                    Text("Batal", color = Color.DarkGray)
                }
            },
            containerColor = CardBg
        )
    }
}
