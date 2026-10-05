package com.mars.liquidcharge

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
        }

        val title = TextView(this).apply {
            text = "LiquidCharge"
            textSize = 28f
        }

        status = TextView(this).apply {
            textSize = 18f
            setPadding(0, 32, 0, 32)
        }

        val permissionButton = Button(this).apply {
            text = "Allow Display Over Other Apps"
            setOnClickListener {
                startActivity(
                    Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    )
                )
            }
        }

        val testButton = Button(this).apply {
            text = "Test Charging Animation"
            setOnClickListener {
                ChargeOverlay.show(this@MainActivity)
            }
        }

        layout.addView(title)
        layout.addView(status)
        layout.addView(permissionButton)
        layout.addView(testButton)

        setContentView(layout)

        updateStatus()
    }

    override fun onResume() {
        super.onResume()
        if (::status.isInitialized) {
            updateStatus()
        }
    }

    private fun updateStatus() {
        status.text = if (Settings.canDrawOverlays(this)) {
            "Overlay permission: ON"
        } else {
            "Overlay permission: OFF"
        }
    }
}