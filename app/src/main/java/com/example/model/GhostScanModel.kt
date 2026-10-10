package com.example.model

import androidx.compose.ui.graphics.Color
import kotlin.random.Random

enum class ScanMode(val label: String, val description: String) {
    CODE("CODE", "Scrolling terminal source stream"),
    NUMBERS("NUMBERS", "Digital matrix rain"),
    MIXED_ALERT("MIXED+ALERT", "Matrix rain with intrusion alarms"),
    GLITCH("GLITCH", "Neon chromatic code waterfall")
}

enum class TargetCategory(
    val title: String,
    val hexColor: Long,
    val color: Color,
    val presets: List<String>
) {
    DEVICE(
        title = "Device",
        hexColor = 0xFF39FF14,
        color = Color(0xFF39FF14),
        presets = listOf("iPhone", "Galaxy", "Pixel", "MacBook", "iPad", "Laptop", "ThinkPad", "Surface")
    ),
    WIFI(
        title = "Wi-Fi",
        hexColor = 0xFF00E5FF,
        color = Color(0xFF00E5FF),
        presets = listOf("Router", "Hotspot", "Home-WiFi", "Guest-Net", "Extender", "Fiber-Node", "Mesh-Pod")
    ),
    BLUETOOTH(
        title = "Bluetooth",
        hexColor = 0xFFB266FF,
        color = Color(0xFFB266FF),
        presets = listOf("AirPods", "Speaker", "Watch", "Earbuds", "Car-Kit", "Mouse", "KeyFinder", "SmartBand")
    ),
    CAMERA(
        title = "Camera",
        hexColor = 0xFFFF9D00,
        color = Color(0xFFFF9D00),
        presets = listOf("Webcam", "Doorbell", "CCTV", "Dashcam", "BabyMonitor", "Drone-Cam", "PTZ-Optics")
    ),
    CLOUD(
        title = "Cloud",
        hexColor = 0xFFFF3DF2,
        color = Color(0xFFFF3DF2),
        presets = listOf("iCloud", "Drive", "Backup", "Mail", "Photos", "AWS-S3", "Azure-Blob", "Git-Vault")
    )
}

enum class PanelPosition(val label: String) {
    CENTER("Center"),
    LEFT("Left"),
    RIGHT("Right"),
    TOP("Top"),
    BOTTOM("Bottom"),
    RANDOM("Random")
}

data class RadarTarget(
    val id: String,
    val category: TargetCategory,
    val label: String,
    val xPercent: Float, // 0.08 .. 0.92
    val yPercent: Float, // 0.10 .. 0.82
    val ip: String,
    val mac: String,
    val signalDbm: Int,
    val threatScore: Int,
    val openPorts: List<Int>
)

data class TargetIntelData(
    val title: String,
    val category: TargetCategory?,
    val fields: List<Pair<String, String>>,
    val isLinkReport: Boolean = false,
    val threatScore: Int = 20,
    val openPorts: List<Int> = emptyList(),
    val rawId: String = ""
)

data class TraceHop(
    val hopNumber: Int,
    val ip: String,
    val host: String,
    val latencyMs: Int
)

data class ConfigSettings(
    val fieldVisibility: Map<String, Boolean> = mapOf(
        "name" to true,
        "phone" to true,
        "email" to true,
        "password" to true,
        "address" to true,
        "ip" to true,
        "mac" to true,
        "login" to true,
        "threat" to true,
        "signal" to true
    ),
    val fieldOverrides: Map<String, String> = emptyMap(),
    val panelPosition: PanelPosition = PanelPosition.CENTER,
    val itemsPerScan: Int = 7,
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val scanlinesOverlay: Boolean = true
)

object CyberDataGenerator {
    private val FIRST_NAMES = listOf("Alex", "Jordan", "Sam", "Chris", "Taylor", "Morgan", "Riley", "Jamie", "Casey", "Drew", "Logan", "Avery")
    private val LAST_NAMES = listOf("Smith", "Rivera", "Nguyen", "Brown", "Khan", "Lopez", "Meyer", "Costa", "Young", "Patel", "Vance", "Kowalski")
    private val STREETS = listOf("Maple St", "Oak Ave", "Pine Rd", "Cedar Ln", "Lakeview Dr", "Sunset Blvd", "Cyber Way", "Silicon Alley")
    private val DOMAINS = listOf("mailbox.example", "post.example", "webmail.example", "ghostnode.internal", "cryptonet.org")
    private val PASS_PREFIX = listOf("Sun", "Blue", "Tiger", "Qwerty", "Pass", "Moon", "Viper", "Phantom", "Zero", "Apex")
    private val SPECIAL_CHARS = listOf("!", "#", "$", "@", "*", "&", "%")
    private val HEX_CHARS = "01234567890ABCDEF{}[]<>/\\=+*;:$#@&%!?_|~".toCharArray()

    fun randomHexStr(length: Int): String {
        return (1..length).map { HEX_CHARS.random() }.joinToString("")
    }

    fun randomName(): String = "${FIRST_NAMES.random()} ${LAST_NAMES.random()}"

    fun randomPhone(): String = "(555) 01${Random.nextInt(0, 10)}-${Random.nextInt(1000, 9999)}"

    fun randomEmail(name: String? = null): String {
        val base = if (name != null) {
            name.lowercase().replace(" ", ".")
        } else {
            "${FIRST_NAMES.random()}.${LAST_NAMES.random()}".lowercase()
        }
        return "$base${Random.nextInt(1, 99)}@${DOMAINS.random()}"
    }

    fun randomPassword(): String = "${PASS_PREFIX.random()}${Random.nextInt(10, 9999)}${SPECIAL_CHARS.random()}"

    fun randomAddress(): String = "${Random.nextInt(10, 9999)} ${STREETS.random()}, Apt ${Random.nextInt(1, 50)}"

    fun randomIp(): String = "${Random.nextInt(11, 223)}.${Random.nextInt(0, 255)}.${Random.nextInt(0, 255)}.${Random.nextInt(1, 254)}"

    fun randomMac(): String = (1..6).joinToString(":") { "%02X".format(Random.nextInt(0, 256)) }

    fun randomLogin(): String = "${Random.nextInt(1, 59)} min ago"

    fun randomThreatScore(): Int = Random.nextInt(5, 98)

    fun randomSignal(): Int = -Random.nextInt(32, 88)

    fun randomPorts(): List<Int> {
        val candidates = listOf(21, 22, 53, 80, 443, 3306, 5432, 8080, 8443, 9000, 27017)
        return candidates.shuffled().take(Random.nextInt(1, 5)).sorted()
    }
}
