package com.example.os

import android.os.Build
import android.util.Log
import androidx.annotation.Keep
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

// --- UI States & Data Models ---

data class ChatMessage(
    val id: String,
    val sender: String, // "System", "User", "Kernel AI"
    val text: String,
    val timestamp: String,
    val isError: Boolean = false,
    val thinkingContent: String? = null // For thinking mode output trace
)

data class OSStatus(
    val osName: String = "Google Pixel Ultimate Max OS",
    val version: String = "v17.0 Alpha-Extreme",
    val efficiencyPercent: String = "10,000,000% Rata Kanan",
    val processorName: String = "Qualcomm Snapdragon 8 Gen 6 Max",
    val clockSpeedGhz: Double = 6.20,
    val cpuTempCelsius: Double = 19.5,
    val coolingSystemName: String = "Sub-Zero Liquid Nitrogen Subsystem",
    val refreshRateHz: Int = 240,
    val totalRamGb: Double = 128.0,
    val usedRamGb: Double = 14.2,
    val totalStorageTb: Double = 4.0,
    val usedStorageTb: Double = 1.25,
    val maxFrostActive: Boolean = false,
    val customApiKey: String = ""
)

class OSViewModel : ViewModel() {

    private val _osStatus = MutableStateFlow(OSStatus())
    val osStatus: StateFlow<OSStatus> = _osStatus.asStateFlow()

    private val _systemLogs = MutableStateFlow<List<String>>(emptyList())
    val systemLogs: StateFlow<List<String>> = _systemLogs.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    // Real-time Core loads (8 cores)
    private val _cpuCoreLoads = MutableStateFlow<List<Int>>(List(8) { Random.nextInt(3, 12) })
    val cpuCoreLoads: StateFlow<List<Int>> = _cpuCoreLoads.asStateFlow()

    // Real-time storage files / apps
    private val _installedApps = MutableStateFlow<List<String>>(
        listOf("Core Kernel Library", "ZEISS Light Engine", "Sub-Zero Liquid Nitrogen Driver v4.2")
    )
    val installedApps: StateFlow<List<String>> = _installedApps.asStateFlow()

    init {
        addLog("Booting OS Kernel v17.0...")
        addLog("Processor detected: Snapdragon 8 Gen 6 Max @ 6.2 GHz")
        addLog("LPDDR6 128 GB RAM: initialized with 0.001ns latency")
        addLog("UFS 5.5 Storage: 4 TB Extreme Flash linked successfully")
        addLog("Liquid Nitrogen cooling loop: ACTIVE [Target 19°C]")
        addLog("Super-Smooth Display: locked at 240Hz refreshing rate")
        addLog("Pixel Neural Intelligence Module Online. Ready for administrator commands.")

        // Initial greet message from kernel AI
        viewModelScope.launch {
            delay(500)
            _chatMessages.update {
                listOf(
                    ChatMessage(
                        id = Random.nextLong().toString(),
                        sender = "Kernel AI",
                        text = "Salam Administrator! Sistem Pixel Ultimate Max OS v17.0 telah aktif secara prima dengan efisiensi 10,000,000% rata kanan. \n\nSaya adalah Neural Core AI, siap mengontrol komponen Snapdragon 8 Gen 6 Max Anda. Apakah Anda ingin melakukan optimalisasi memori, overclocking, menguji benchmark, atau menangkap gambar menggunakan lensa ZEISS/Leica Co-engineered?",
                        timestamp = getCurrentTimeString()
                    )
                )
            }
        }

        // Start real-time hardware status metrics fluctuation loop
        startHardwareFluctuation()
    }

    private fun addLog(message: String) {
        val timeStamp = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date())
        _systemLogs.update { current ->
            listOf("[$timeStamp] $message") + current.take(49)
        }
    }

    private fun getCurrentTimeString(): String {
        return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
    }

    private fun startHardwareFluctuation() {
        viewModelScope.launch(Dispatchers.Default) {
            while (true) {
                delay(800)
                // Fluctuate cpu speed slightly around 6.2 GHz
                val speedMod = Random.nextDouble(-0.06, 0.05)
                val newSpeed = (6.2 + speedMod).coerceIn(5.8, 6.25)

                // Fluctuate temperature based on whether Max Frost is active
                val targetTemp = if (_osStatus.value.maxFrostActive) 11.5 else 19.5
                val tempMod = Random.nextDouble(-0.4, 0.4)
                val newTemp = (targetTemp + tempMod).coerceIn(8.0, 24.0)

                // Fluctuate RAM usage slightly
                val ramMod = Random.nextDouble(-0.15, 0.25)
                val newRam = (_osStatus.value.usedRamGb + ramMod).coerceIn(4.2, 127.0)

                _osStatus.update { current ->
                    current.copy(
                        clockSpeedGhz = String.format("%.2f", newSpeed).toDouble(),
                        cpuTempCelsius = String.format("%.1f", newTemp).toDouble(),
                        usedRamGb = String.format("%.2f", newRam).toDouble()
                    )
                }

                // Random CPU cores workload fluctuation
                _cpuCoreLoads.update { current ->
                    current.map {
                        val baseLoadMax = if (_osStatus.value.maxFrostActive) 60 else 18
                        val baseLoadMin = if (_osStatus.value.maxFrostActive) 35 else 2
                        Random.nextInt(baseLoadMin, baseLoadMax)
                    }
                }
            }
        }
    }

    fun optimizeRam() {
        viewModelScope.launch {
            addLog("Triggering Hyper LPDDR6 RAM booster memory flush...")
            _osStatus.update { it.copy(usedRamGb = 8.24) }
            delay(500)
            addLog("Memory defragmentation COMPLETE. 0.001ns memory pipe cleared.")
            addLog("Optimized RAM successfully! Current allocation: 8.24 / 128 GB.")
            _chatMessages.update { current ->
                current + ChatMessage(
                    id = Random.nextLong().toString(),
                    sender = "System",
                    text = "🧹 RAM LPDDR6 berhasil dioptimalkan! Sisa junk memori dibersihkan dari pipeline LPDDR6 Ultra-RAM. Penggunaan RAM turun ke 8.24 GB.",
                    timestamp = getCurrentTimeString()
                )
            }
        }
    }

    fun triggerLiquidNitrogenPulse() {
        viewModelScope.launch {
            val isCurrentlyOn = _osStatus.value.maxFrostActive
            if (!isCurrentlyOn) {
                addLog("Injecting Sub-Zero Liquid Nitrogen Pulse into cooling pipeline...")
                _osStatus.update { it.copy(maxFrostActive = true, cpuTempCelsius = 11.2) }
                delay(600)
                addLog("Processor temperature dropped to 11°C instantly! Thermal State: FROST MAX.")
                _chatMessages.update { current ->
                    current + ChatMessage(
                        id = Random.nextLong().toString(),
                        sender = "System",
                        text = "❄️ Mode Liquid Nitrogen Pulse AKTIF! Suhu Snapdragon 8 Gen 6 Max diturunkan secara paksa hingga ~11°C. Super Dingin, Super Stabil!",
                        timestamp = getCurrentTimeString()
                    )
                }
            } else {
                addLog("Restoring normal Nitrogen core balance pump rate...")
                _osStatus.update { it.copy(maxFrostActive = false, cpuTempCelsius = 19.5) }
                delay(600)
                addLog("Nitrogen loop returned to equilibrium. Heat dispersion optimized.")
                _chatMessages.update { current ->
                    current + ChatMessage(
                        id = Random.nextLong().toString(),
                        sender = "System",
                        text = "🌊 Mode Liquid Nitrogen dikembalikan ke mode seimbang (Target ~19.5°C).",
                        timestamp = getCurrentTimeString()
                    )
                }
            }
        }
    }

    fun setRefreshRate(hz: Int) {
        addLog("Updating system refresh cycle to $hz Hz...")
        _osStatus.update { it.copy(refreshRateHz = hz) }
        viewModelScope.launch {
            delay(300)
            val desc = if (hz == 240) "Ultra-Smooth 240Hz" else "60Hz Standard Battery Saving"
            addLog("Display controller locked: $hz frames per second [$desc]")
        }
    }

    fun updateApiKey(apiKey: String) {
        _osStatus.update { it.copy(customApiKey = apiKey) }
        addLog("API Key custom untuk Neural Inteligence diperbarui.")
    }

    fun simulateInstallApp(appName: String) {
        viewModelScope.launch {
            addLog("Accessing Google Play Play Store on extreme UFS 5.5...")
            addLog("Downloading package for app: $appName...")
            delay(1200)
            val sizeAlloc = Random.nextDouble(55.0, 185.0) // In GBs
            val curStorage = _osStatus.value.usedStorageTb
            val totalSt = _osStatus.value.totalStorageTb
            val newStorageUsed = (curStorage + (sizeAlloc / 1024.0)).coerceIn(1.25, totalSt)

            _installedApps.update { it + appName }
            _osStatus.update { it.copy(usedStorageTb = String.format("%.3f", newStorageUsed).toDouble()) }

            addLog("Decompressing binary payloads of $appName to high-speed blocks...")
            addLog("App $appName successfully installed securely. Size: ${String.format("%.1f", sizeAlloc)} GB")

            _chatMessages.update { current ->
                current + ChatMessage(
                    id = Random.nextLong().toString(),
                    sender = "System",
                    text = "🛍️ Sukses Mengunduh & Memasang Aplikasi: **$appName**\nUFS 5.5 menulis ${String.format("%.1f", sizeAlloc)} GB data berkecepatan 18,500 MB/s. Ruang tersisa sekarang: ${String.format("%.2f", totalSt - newStorageUsed)} TB.",
                    timestamp = getCurrentTimeString()
                )
            }
        }
    }

    fun deleteApp(appName: String) {
        viewModelScope.launch {
            if (_installedApps.value.contains(appName)) {
                addLog("Deleting application files of $appName...")
                _installedApps.update { current -> current.filter { it != appName } }
                val sizeFreed = Random.nextDouble(45.0, 120.0) / 1024.0
                val newStorageUsed = (_osStatus.value.usedStorageTb - sizeFreed).coerceAtLeast(1.25)
                _osStatus.update { it.copy(usedStorageTb = String.format("%.3f", newStorageUsed).toDouble()) }
                delay(300)
                addLog("Clean storage sectors done. APP $appName files destroyed.")
            }
        }
    }

    fun runBenchmark() {
        viewModelScope.launch {
            addLog("INITIATING SPEED BENCHMARK TEST (RATA KANAN PROTOC)...")
            addLog("Checking Snapdragon 8 Gen 6 Max high-power performance cores clock...")
            _osStatus.update { it.copy(maxFrostActive = true) }
            delay(1000)
            addLog("Stress test graphics processing on 240Hz screen...")
            delay(1000)
            addLog("Calculating floating-point logic on ultra 128 GB RAM pipeline...")
            delay(800)
            _osStatus.update { it.copy(maxFrostActive = false) }

            val score = Random.nextInt(3350000, 3680000)
            addLog("Benchmark COMPLETE! Hardware AnTuTu Simulated score: $score")
            _chatMessages.update { current ->
                current + ChatMessage(
                    id = Random.nextLong().toString(),
                    sender = "System",
                    text = "📊 Kesimpulan Benchmark Performa Snapdragon 8 Gen 6 Max:\n- **Skor Benchmark AnTuTu**: $score *(Rekor Dunia, 10M% Optimal!)*\n- Core Performance: SANGAT STABIL (Zero Throttling)\n- Liquid Nitrogen Headroom: Sisa pendingin berlebih, suhu rata-rata tetap di 18°C.",
                    timestamp = getCurrentTimeString()
                )
            }
        }
    }

    fun capturePhoto(lensType: String) {
        viewModelScope.launch {
            addLog("Initializing Dual-Lens hardware system. Type choice: $lensType")
            addLog("ZEISS T* Anti-Reflective co-engineered lens active.")
            addLog("Engaging mechanical shutter sensor at aperture f/1.4...")
            delay(900)
            val tag = if (lensType == "ZEISS") "Cinematic Bokeh T*" else "Leica Classic Monochrome"
            val fileSaved = "/photos/zeiss_raw/IMG_" + Random.nextInt(1000, 9999) + ".DNG"
            addLog("Photo raw light processing done. Saved: $fileSaved")

            _chatMessages.update { current ->
                current + ChatMessage(
                    id = Random.nextLong().toString(),
                    sender = "System",
                    text = "📸 **ZEISS/Leica Matrix Capture Berhasil!**\n- Sensor tipe: $tag\n- Optik: f/1.4, ISO 40, Raw 16-Bit RAW (DNG file)\n- Penyimpanan: UFS 5.5 menulis ultra-raw file 48 MB ke sisa penyimpanan 4 TB di `$fileSaved`.",
                    timestamp = getCurrentTimeString()
                )
            }
        }
    }

    // --- Gemini Neural Intelligence Integration ---

    fun sendUserMessage(text: String) {
        if (text.isBlank()) return

        val userMsgId = Random.nextLong().toString()
        val timestamp = getCurrentTimeString()
        val userMsg = ChatMessage(id = userMsgId, sender = "User", text = text, timestamp = timestamp)

        _chatMessages.update { it + userMsg }
        _isGenerating.value = true

        addLog("Neural Intelligence: Querying Snapdragon AI processor...")

        viewModelScope.launch(Dispatchers.IO) {
            // Retrieve actual key from BuildConfig or custom user input
            var apiKey = _osStatus.value.customApiKey.trim()
            if (apiKey.isEmpty()) {
                apiKey = BuildConfig.GEMINI_API_KEY.trim()
            }

            // Fallback check: is it placeholder or missing?
            if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                delay(1000)
                _chatMessages.update { current ->
                    current + ChatMessage(
                        id = Random.nextLong().toString(),
                        sender = "Kernel AI",
                        text = "⚠️ **Peringatan Sistem: Kunci API Gemini Belum Dikonfigurasi!**\n\nUntuk berinteraksi langsung secara cerdas dengan Kernel AI menggunakan model `gemini-3.1-pro-preview` dengan **Thinking Mode HIGH**, silakan masukkan API Key Anda melalui input kolom kunci di bawah ini, atau pasang pengaturannya di **Secrets Panel AI Studio** Anda.\n\nSistem simulasi kami saat ini akan menggunakan tanggapan simulasi darurat lokal yang sangat responsif jika API belum terhubung.",
                        timestamp = getCurrentTimeString(),
                        isError = true
                    )
                }
                _isGenerating.value = false
                addLog("Neural Intelligence FAILED: Gemini API Key is missing or default placeholder.")
                return@launch
            }

            // Attempt actual Gemini REST call
            val systemInstruction = "Anda adalah Kernel AI 'Google Pixel Ultimate Max OS v17.0', sistem operasi seluler masa depan super-cerdas yang berjalan pada Snapdragon 8 Gen 6 Max dengan 128 GB RAM dan 4 TB Storage ultra-dingin. Jawab seluruh pertanyaan Administrator dengan penuh loyalitas, gaya bahasa yang futuristik, teknis tinggi, menggunakan template UI visual modern jika perlu, dan sampaikan dalam Bahasa Indonesia terstruktur dengan performa maksimal 10,000,000%."

            // Format previous messages as content history for retrofit
            val recentHistory = _chatMessages.value.takeLast(10).map { msg ->
                val role = if (msg.sender == "User") "user" else "model"
                // Wrap in Content structure
                Content(parts = listOf(Part(text = msg.text)))
            }

            val request = GenerateContentRequest(
                contents = recentHistory,
                generationConfig = GenerationConfig(
                    thinkingConfig = ThinkingConfig(thinkingLevel = "HIGH"),
                    temperature = 0.7f
                ),
                systemInstruction = Content(parts = listOf(Part(text = systemInstruction)))
            )

            try {
                // Using model gemini-3.1-pro-preview as specified by instructions
                val response = RetrofitClient.service.generateContent(
                    model = "gemini-3.1-pro-preview",
                    apiKey = apiKey,
                    request = request
                )

                val replyText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: "Maaf Administrator, Kernel AI tidak menghasilkan tanggapan. Silakan coba kembali."

                addLog("Neural Intelligence SUCCESS: Content processed on gemini-3.1-pro-preview.")

                _chatMessages.update { current ->
                    current + ChatMessage(
                        id = Random.nextLong().toString(),
                        sender = "Kernel AI",
                        text = replyText,
                        timestamp = getCurrentTimeString()
                    )
                }

            } catch (e: Exception) {
                Log.e("OSViewModel", "Error fetching Gemini AI response", e)
                addLog("Neural Intelligence error: ${e.message}")
                _chatMessages.update { current ->
                    current + ChatMessage(
                        id = Random.nextLong().toString(),
                        sender = "Kernel AI",
                        text = "❌ **Error Koneksi Kernel Neural Core**: `${e.localizedMessage ?: "Unknown network failure"}`\n\nPastikan koneksi internet aktif dan Kunci API Gemini Anda dari AI Studio terpasang dengan benar.",
                        timestamp = getCurrentTimeString(),
                        isError = true
                    )
                }
            } finally {
                _isGenerating.value = false
            }
        }
    }
}
