package com.mars.liquidcharge

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class PowerReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_POWER_CONNECTED) {
            ChargeOverlay.show(context.applicationContext)
        }
    }
}
