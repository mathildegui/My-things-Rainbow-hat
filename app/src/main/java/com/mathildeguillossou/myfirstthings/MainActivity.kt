package com.mathildeguillossou.myfirstthings

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import com.google.android.things.pio.PeripheralManagerService
import com.google.android.things.pio.Gpio
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.util.concurrent.TimeUnit


/**
 * Skeleton of an Android Things activity.
 *
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 * <pre>{@code
 * val service = PeripheralManagerService()
 * val mLedGpio = service.openGpio("BCM6")
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
 * mLedGpio.value = true
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 *
 */
class MainActivity : Activity() {



    private var bus: Gpio? = null

    var ledToggleHandler: Handler? = null

    private val GREEN_LED_PIN   = "BCM19" //private static final String GREEN_LED_PIN = "BCM19";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val service = PeripheralManagerService()

        try {
            bus = service.openGpio(GREEN_LED_PIN)
        } catch (e: IOException) {
            throw IllegalStateException(GREEN_LED_PIN + " bus cannot be opened.", e)
        }

        try {
            bus?.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH)
            bus?.setActiveType(Gpio.ACTIVE_HIGH)
        } catch (e: IOException) {
            throw IllegalStateException(GREEN_LED_PIN + " bus cannot be configured.", e)
        }

        val handlerThread = HandlerThread("MyBackgroundThread")
        handlerThread.start()

        ledToggleHandler = Handler(handlerThread.looper)

        btn_hello.setOnClickListener { startLED() }
        btn_bye.setOnClickListener { stopLED() }
    }

    fun startLED() {
        ledToggleHandler?.post(toggleLed)
    }

    fun stopLED() {
        ledToggleHandler?.removeCallbacks(toggleLed)
    }

    private val toggleLed = object : Runnable {
        override fun run() {
            val isOn: Boolean
            try {
                isOn = bus!!.value
            } catch (e: IOException) {
                throw IllegalStateException(GREEN_LED_PIN + " cannot be read.", e)
            }

            try {
                bus?.value = !isOn
            } catch (e:IOException ){
                throw IllegalStateException(GREEN_LED_PIN + " cannot be written.", e)
            }
            ledToggleHandler?.postDelayed(this, TimeUnit.SECONDS.toMillis(1))
        }

    }

    override fun onStart() {
        super.onStart()
    }


    override fun onStop() {
        ledToggleHandler?.removeCallbacks(toggleLed)
        super.onStop()
    }

    override fun onDestroy() {
        try {
            bus?.close()
        } catch (e: IOException) {
            Log.e("TUT", GREEN_LED_PIN + " bus cannot be closed, you may experience errors on next launch.", e)
        }

        super.onDestroy()
    }
}
