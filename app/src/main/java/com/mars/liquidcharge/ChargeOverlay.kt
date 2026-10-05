package com.mars.liquidcharge

import android.content.Context
import android.graphics.*
import android.os.BatteryManager
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.*
import kotlin.math.min

object ChargeOverlay {
    private var attached = false

    fun show(context: Context) {
        if (attached || !Settings.canDrawOverlays(context)) return
        attached = true

        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val root = ChargeView(context)
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )
        wm.addView(root, params)

        Handler(Looper.getMainLooper()).postDelayed({
            try { wm.removeView(root) } catch (_: Exception) {}
            attached = false
        }, 2850L)
    }

    private class ChargeView(context: Context) : View(context) {
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val handler = Handler(Looper.getMainLooper())
        private val start = System.currentTimeMillis()
        private val battery: Int = run {
            val bm = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
                .coerceIn(0, 100)
        }

        init {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            handler.post(object : Runnable {
                override fun run() {
                    invalidate()
                    if (System.currentTimeMillis() - start < 2850)
                        handler.postDelayed(this, 16)
                }
            })
        }

        override fun onDraw(c: Canvas) {
            super.onDraw(c)

            val t = (System.currentTimeMillis() - start).toFloat()
            val w = width.toFloat()
            val h = height.toFloat()
            val cx = w / 2f
            val phoneW = min(w, h) * 0.29f
            val phoneH = phoneW * 1.92f
            val left = cx - phoneW / 2f
            val top = h * 0.30f
            val right = cx + phoneW / 2f
            val bottom = top + phoneH
            val corner = phoneW * 0.12f

            val fadeIn = (t / 220f).coerceIn(0f, 1f)
            val fadeOut = ((2850f - t) / 550f).coerceIn(0f, 1f)
            val alpha = (255f * min(fadeIn, fadeOut)).toInt()

            val rise = when {
                t < 500f -> 18f * (1f - t / 500f)
                t > 2200f -> -8f * ((t - 2200f) / 650f)
                else -> 0f
            }

            c.save()
            c.translate(0f, rise)

            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 5f
            paint.color = Color.WHITE
            paint.alpha = alpha
            paint.strokeCap = Paint.Cap.ROUND
            paint.setShadowLayer(14f, 0f, 0f, Color.WHITE)
            c.drawRoundRect(left, top, right, bottom, corner, corner, paint)
            paint.clearShadowLayer()

            paint.style = Paint.Style.FILL
            paint.alpha = (alpha * 0.55f).toInt()
            c.drawRoundRect(
                cx - phoneW * 0.10f,
                top + phoneW * 0.035f,
                cx + phoneW * 0.10f,
                top + phoneW * 0.055f,
                8f,
                8f,
                paint
            )

            if (t > 350f) {
                val screenAlpha =
                    (alpha * ((t - 350f) / 350f).coerceIn(0f, 1f) * 0.10f).toInt()

                paint.style = Paint.Style.FILL
                paint.alpha = screenAlpha
                paint.setShadowLayer(28f, 0f, 0f, Color.WHITE)

                c.drawRoundRect(
                    left + 12f,
                    top + 12f,
                    right - 12f,
                    bottom - 12f,
                    corner - 8f,
                    corner - 8f,
                    paint
                )

                paint.clearShadowLayer()
            }

            val cableStartY = h * 0.98f
            val targetY = bottom + 6f
            val cableProgress = ((t - 280f) / 520f).coerceIn(0f, 1f)
            val connectorY =
                cableStartY + (targetY - cableStartY) * cableProgress

            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 7f
            paint.strokeCap = Paint.Cap.ROUND
            paint.alpha = (alpha * 0.90f).toInt()

            val cable = Path()
            cable.moveTo(cx, cableStartY + 80f)
            cable.cubicTo(
                cx - 80f,
                cableStartY + 10f,
                cx + 80f,
                connectorY + 40f,
                cx,
                connectorY
            )

            c.drawPath(cable, paint)

            paint.style = Paint.Style.FILL

            val plugW = phoneW * 0.13f
            val plugH = phoneW * 0.075f

            c.drawRoundRect(
                cx - plugW,
                connectorY - plugH / 2f,
                cx + plugW,
                connectorY + plugH / 2f,
                8f,
                8f,
                paint
            )

            if (t > 850f) {
                val p = ((t - 850f) / 300f).coerceIn(0f, 1f)

                paint.style = Paint.Style.FILL
                paint.textAlign = Paint.Align.CENTER
                paint.typeface = Typeface.create("sans", Typeface.NORMAL)
                paint.textSize = phoneW * 0.19f
                paint.alpha = (alpha * p).toInt()

                c.drawText(
                    "$battery%",
                    cx,
                    top + phoneH * 0.54f,
                    paint
                )

                paint.textSize = phoneW * 0.075f
                paint.alpha = (alpha * p * 0.65f).toInt()

                c.drawText(
                    "Charging",
                    cx,
                    top + phoneH * 0.62f,
                    paint
                )
            }

            if (t > 1050f) {
                val fillP = ((t - 1050f) / 750f).coerceIn(0f, 1f)
                val targetHeight = phoneH * 0.24f * (battery / 100f)
                val currentHeight = targetHeight * fillP
                val barW = phoneW * 0.055f
                val barBottom = bottom - phoneH * 0.16f

                paint.style = Paint.Style.FILL
                paint.alpha = (alpha * 0.85f).toInt()

                c.drawRoundRect(
                    cx - barW / 2f,
                    barBottom - currentHeight,
                    cx + barW / 2f,
                    barBottom,
                    barW / 2f,
                    barW / 2f,
                    paint
                )
            }

            if (t in 800f..1200f) {
                val flash = 1f - kotlin.math.abs(1000f - t) / 200f

                paint.style = Paint.Style.FILL
                paint.alpha = (alpha * flash).toInt()

                val bolt = Path()

                bolt.moveTo(
                    cx + phoneW * 0.11f,
                    top + phoneH * 0.44f
                )

                bolt.lineTo(
                    cx + phoneW * 0.03f,
                    top + phoneH * 0.56f
                )

                bolt.lineTo(
                    cx + phoneW * 0.09f,
                    top + phoneH * 0.55f
                )

                bolt.lineTo(
                    cx + phoneW * 0.01f,
                    top + phoneH * 0.68f
                )

                bolt.lineTo(
                    cx + phoneW * 0.15f,
                    top + phoneH * 0.51f
                )

                bolt.lineTo(
                    cx + phoneW * 0.08f,
                    top + phoneH * 0.52f
                )

                bolt.close()

                c.drawPath(bolt, paint)
            }

            c.restore()
        }
    }
}
