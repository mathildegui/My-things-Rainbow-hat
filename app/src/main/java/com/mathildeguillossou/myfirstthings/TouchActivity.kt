package com.mathildeguillossou.myfirstthings

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.GpioCallback
import com.google.android.things.pio.PeripheralManagerService
import java.io.IOException

class TouchActivity : Activity() {

    private var greenLED: Gpio? = null
    private var inputA: Gpio? = null

    private val TOUCH_BTN_A_PIN = "BCM21" //private static final String TOUCH_BTN_A_PIN = "BCM21";
    private val GREEN_LED_PIN   = "BCM19"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val service = PeripheralManagerService()

        try {
            inputA = service.openGpio(TOUCH_BTN_A_PIN)
            greenLED = service.openGpio(GREEN_LED_PIN)
        } catch (e: IOException) {
            throw IllegalStateException(TOUCH_BTN_A_PIN + " bus cannot be opened.", e)
        }

        try {
            inputA?.setDirection(Gpio.DIRECTION_IN)
            inputA?.setActiveType(Gpio.ACTIVE_LOW)

            greenLED?.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
//            bus?.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH)
//            bus?.setActiveType(Gpio.ACTIVE_HIGH)
        } catch (e: IOException) {
            throw IllegalStateException(TOUCH_BTN_A_PIN + " bus cannot be configured.", e)
        }
    }


    private val mCallback = object : GpioCallback() {
        override fun onGpioEdge(gpio: Gpio?): Boolean {
            try {
                val buttonValue = gpio!!.value
                greenLED?.value = buttonValue
            } catch (e: IOException) {
                Log.w(TOUCH_BTN_A_PIN, "Error reading GPIO")
            }

            // Return true to keep callback active.
            return true
        }
    }



//    private val callback = object : GpioCallback() {
//        override fun onGpioEdge(gpio: Gpio): Boolean {
//            try {
//                if (gpio.value) {
//                    Log.i("TUT", "ON PRESSED DOWN")
//                } else {
//                    Log.i("TUT", "ON PRESSED UP")
//                }
//            } catch (e: IOException) {
//                throw IllegalStateException(TOUCH_BTN_A_PIN + " cannot be read.", e)
//            }
//
//            return true
//        }
//    }

    override fun onStart() {
        super.onStart()
        try {
            inputA?.setEdgeTriggerType(Gpio.EDGE_BOTH)
            inputA?.registerGpioCallback(mCallback)
            //inputA?.registerGpioCallback(callback)
        } catch (e: IOException) {
            throw IllegalStateException(TOUCH_BTN_A_PIN + " bus cannot be monitored.", e)
        }
    }

    override fun onStop () {
        super.onStop()
        inputA?.unregisterGpioCallback(mCallback)
//        inputA?.unregisterGpioCallback(callback)
    }

    override fun onDestroy() {
        try {
            inputA?.close()
            greenLED?.close()
        } catch (e: IOException) {
            Log.e("TUT", TOUCH_BTN_A_PIN + " bus cannot be closed, you may experience errors on next launch.", e)
        }

        super.onDestroy()
    }
}
