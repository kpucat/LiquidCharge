package com.mars.liquidcharge

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class PowerReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {

        if (intent?.action == Intent.ACTION_POWER_CONNECTED) {

            Log.d("LiquidCharge", "POWER_CONNECTED received")

            ChargeOverlay.show(context.applicationContext)
        }
    }
}