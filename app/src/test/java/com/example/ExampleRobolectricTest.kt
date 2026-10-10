package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.CyberDataGenerator
import com.example.model.ScanMode
import com.example.model.TargetCategory
import com.example.viewmodel.GhostScanViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("GhostScan", appName)
    }

    @Test
    fun `test cyber data generator produces valid telemetry`() {
        val ip = CyberDataGenerator.randomIp()
        assertNotNull(ip)
        assertTrue(ip.contains("."))

        val mac = CyberDataGenerator.randomMac()
        assertNotNull(mac)
        assertEquals(5, mac.count { it == ':' })

        val hex = CyberDataGenerator.randomHexStr(8)
        assertEquals(8, hex.length)
    }

    @Test
    fun `test viewmodel initial state and mode switching`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = GhostScanViewModel(app)

        assertEquals(ScanMode.CODE, viewModel.scanMode.value)
        viewModel.setScanMode(ScanMode.GLITCH)
        assertEquals(ScanMode.GLITCH, viewModel.scanMode.value)

        viewModel.startScan(TargetCategory.WIFI)
        assertEquals(TargetCategory.WIFI, viewModel.activeCategory.value)
        assertTrue(viewModel.isScanning.value)
    }
}
