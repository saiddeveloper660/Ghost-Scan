package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.CyberAudio
import com.example.model.ConfigSettings
import com.example.model.CyberDataGenerator
import com.example.model.PanelPosition
import com.example.model.RadarTarget
import com.example.model.ScanMode
import com.example.model.TargetCategory
import com.example.model.TargetIntelData
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class GhostScanViewModel(application: Application) : AndroidViewModel(application) {

    val audio = CyberAudio(application)

    // Current Background Mode
    private val _scanMode = MutableStateFlow(ScanMode.CODE)
    val scanMode: StateFlow<ScanMode> = _scanMode.asStateFlow()

    // Live Clock
    private val _currentTime = MutableStateFlow(getCurrentTimeString())
    val currentTime: StateFlow<String> = _currentTime.asStateFlow()

    // Active Category
    private val _activeCategory = MutableStateFlow<TargetCategory?>(null)
    val activeCategory: StateFlow<TargetCategory?> = _activeCategory.asStateFlow()

    // Radar Scanning State
    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _scanProgress = MutableStateFlow(0f)
    val scanProgress: StateFlow<Float> = _scanProgress.asStateFlow()

    private val _statusText = MutableStateFlow("SELECT A TARGET CLASS TO BEGIN SCAN")
    val statusText: StateFlow<String> = _statusText.asStateFlow()

    // Targets on Radar
    private val _radarTargets = MutableStateFlow<List<RadarTarget>>(emptyList())
    val radarTargets: StateFlow<List<RadarTarget>> = _radarTargets.asStateFlow()

    // Intrusion Detected Strobe (for MIXED+ALERT mode)
    private val _isAlertActive = MutableStateFlow(false)
    val isAlertActive: StateFlow<Boolean> = _isAlertActive.asStateFlow()

    // Decryption State
    private val _isDecrypting = MutableStateFlow(false)
    val isDecrypting: StateFlow<Boolean> = _isDecrypting.asStateFlow()

    private val _decryptTargetTitle = MutableStateFlow("")
    val decryptTargetTitle: StateFlow<String> = _decryptTargetTitle.asStateFlow()

    // Decrypted Intel Dossier Data (Modal)
    private val _activeIntelData = MutableStateFlow<TargetIntelData?>(null)
    val activeIntelData: StateFlow<TargetIntelData?> = _activeIntelData.asStateFlow()

    // Config Settings
    private val _config = MutableStateFlow(ConfigSettings())
    val config: StateFlow<ConfigSettings> = _config.asStateFlow()

    // Settings Modal
    private val _isSettingsOpen = MutableStateFlow(false)
    val isSettingsOpen: StateFlow<Boolean> = _isSettingsOpen.asStateFlow()

    // Trace URL text
    private val _traceInput = MutableStateFlow("")
    val traceInput: StateFlow<String> = _traceInput.asStateFlow()

    private var scanJob: Job? = null
    private var alertJob: Job? = null

    init {
        // Clock loop
        viewModelScope.launch {
            while (true) {
                _currentTime.value = getCurrentTimeString()
                delay(1000)
            }
        }
    }

    private fun getCurrentTimeString(): String {
        return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
    }

    fun setScanMode(mode: ScanMode) {
        _scanMode.value = mode
        audio.playBlip(1040f)
        audio.vibrateClick()

        alertJob?.cancel()
        _isAlertActive.value = false

        if (mode == ScanMode.MIXED_ALERT) {
            startAlertLoop()
        }
    }

    private fun startAlertLoop() {
        alertJob?.cancel()
        alertJob = viewModelScope.launch {
            while (_scanMode.value == ScanMode.MIXED_ALERT) {
                val delayTime = Random.nextLong(3000, 6500)
                delay(delayTime)
                if (_scanMode.value != ScanMode.MIXED_ALERT) break

                _isAlertActive.value = true
                audio.playAlarm()
                audio.vibrateAlert()
                delay(1500)
                _isAlertActive.value = false
            }
        }
    }

    fun startScan(category: TargetCategory) {
        scanJob?.cancel()
        _activeCategory.value = category
        _radarTargets.value = emptyList()
        _isScanning.value = true
        _scanProgress.value = 0f

        audio.playSweepChirp()
        audio.vibrateClick()

        val totalDurationMs = 3200L
        val itemCount = _config.value.itemsPerScan

        // Pre-generate target nodes with coordinates
        val plannedTargets = (0 until itemCount).map {
            val label = "${category.presets.random()}-${CyberDataGenerator.randomHexStr(3)}"
            val x = Random.nextInt(8, 92) / 100f
            val y = Random.nextInt(12, 82) / 100f
            RadarTarget(
                id = "tgt_${System.currentTimeMillis()}_$it",
                category = category,
                label = label,
                xPercent = x,
                yPercent = y,
                ip = CyberDataGenerator.randomIp(),
                mac = CyberDataGenerator.randomMac(),
                signalDbm = CyberDataGenerator.randomSignal(),
                threatScore = CyberDataGenerator.randomThreatScore(),
                openPorts = CyberDataGenerator.randomPorts()
            )
        }.sortedBy { it.xPercent }

        scanJob = viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            var nextTargetIndex = 0

            while (true) {
                val elapsed = System.currentTimeMillis() - startTime
                val progress = (elapsed.toFloat() / totalDurationMs).coerceIn(0f, 1f)
                _scanProgress.value = progress

                val percentDisplay = (progress * 100).toInt()
                _statusText.value = "SCANNING ${category.title.uppercase()}… $percentDisplay%  ${CyberDataGenerator.randomHexStr(8)}"

                // Spawn targets as the sweep line passes their xPercent
                while (nextTargetIndex < plannedTargets.size && plannedTargets[nextTargetIndex].xPercent <= progress) {
                    val target = plannedTargets[nextTargetIndex]
                    _radarTargets.value = _radarTargets.value + target
                    audio.playBlip(750f + nextTargetIndex * 90f)
                    audio.vibrateTargetFound()
                    nextTargetIndex++
                }

                if (progress >= 1f) break
                delay(40)
            }

            // Finished
            _isScanning.value = false
            _statusText.value = "SCAN COMPLETE — $itemCount ${category.title.uppercase()} TARGETS FOUND"
            audio.playBlip(1250f, 65)
        }
    }

    fun openTargetIntel(target: RadarTarget) {
        audio.playDecryptionBurst()
        audio.vibrateClick()
        _decryptTargetTitle.value = target.label
        _isDecrypting.value = true

        viewModelScope.launch {
            delay(1900)
            _isDecrypting.value = false

            // Assemble field values based on configuration
            val cfg = _config.value
            val fields = mutableListOf<Pair<String, String>>()

            fun addField(key: String, label: String, generate: () -> String) {
                if (cfg.fieldVisibility[key] == true) {
                    val customVal = cfg.fieldOverrides[key]?.trim()
                    val value = if (!customVal.isNullOrEmpty()) customVal else generate()
                    fields.add(label to value)
                }
            }

            val ownerName = CyberDataGenerator.randomName()
            addField("name", "Name") { ownerName }
            addField("phone", "Phone") { CyberDataGenerator.randomPhone() }
            addField("email", "Email") { CyberDataGenerator.randomEmail(ownerName) }
            addField("password", "Password") { CyberDataGenerator.randomPassword() }
            addField("address", "Address") { CyberDataGenerator.randomAddress() }
            addField("ip", "IP Address") { target.ip }
            addField("mac", "MAC") { target.mac }
            addField("login", "Last login") { CyberDataGenerator.randomLogin() }
            addField("threat", "Threat Score") { "${target.threatScore}/100" }
            addField("signal", "Signal RSSI") { "${target.signalDbm} dBm" }

            _activeIntelData.value = TargetIntelData(
                title = "TARGET: ${target.label}",
                category = target.category,
                fields = fields,
                isLinkReport = false,
                threatScore = target.threatScore,
                openPorts = target.openPorts,
                rawId = target.id
            )
        }
    }

    fun updateTraceInput(input: String) {
        _traceInput.value = input
        audio.playKeyTap()
    }

    fun traceLink() {
        val input = _traceInput.value.trim()
        if (input.isEmpty()) return

        audio.playDecryptionBurst()
        audio.vibrateClick()
        _decryptTargetTitle.value = "LINK TRACE: $input"
        _isDecrypting.value = true

        viewModelScope.launch {
            delay(2100)
            _isDecrypting.value = false

            val host = input.replace(Regex("^https?://"), "").split("/").firstOrNull() ?: input
            val locations = listOf("Frankfurt, DE", "Ashburn, US", "Singapore, SG", "Dublin, IE", "Tokyo, JP", "Stockholm, SE")
            val sslStatuses = listOf("Valid (TLS 1.3)", "Expiring in 7 days", "Self-signed certificate")
            val ports = listOf(22, 80, 443, 3306, 8080).filter { Random.nextFloat() < 0.6f }.ifEmpty { listOf(443) }
            val threatScore = CyberDataGenerator.randomThreatScore()

            val fields = listOf(
                "Host" to host,
                "Server IP" to CyberDataGenerator.randomIp(),
                "Location" to locations.random(),
                "Owner" to CyberDataGenerator.randomName(),
                "Admin email" to CyberDataGenerator.randomEmail(),
                "Registered" to "${Random.nextInt(2010, 2024)}-%02d-%02d".format(Random.nextInt(1, 13), Random.nextInt(1, 29)),
                "SSL" to sslStatuses.random(),
                "Trackers" to "${Random.nextInt(0, 24)} detected",
                "Threat score" to "$threatScore/100",
                "Open ports" to ports.joinToString(", ")
            )

            _activeIntelData.value = TargetIntelData(
                title = "LINK REPORT",
                category = TargetCategory.WIFI, // Cyan aesthetic for link trace
                fields = fields,
                isLinkReport = true,
                threatScore = threatScore,
                openPorts = ports,
                rawId = "link_$host"
            )
        }
    }

    fun dismissIntelDialog() {
        _activeIntelData.value = null
        audio.playBlip(600f, 30)
    }

    fun openSettings() {
        _isSettingsOpen.value = true
        audio.playBlip(920f, 35)
        audio.vibrateClick()
    }

    fun closeSettings() {
        _isSettingsOpen.value = false
        audio.playBlip(620f, 35)
    }

    fun saveConfig(newConfig: ConfigSettings) {
        _config.value = newConfig
        audio.soundEnabled = newConfig.soundEnabled
        audio.hapticsEnabled = newConfig.hapticsEnabled
        audio.playBlip(1100f, 40)
    }
}
