package com.mathildeguillossou.myfirstthings

import android.app.Activity
import android.os.Bundle
import android.view.KeyEvent
import com.google.android.things.contrib.driver.button.ButtonInputDriver
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.PeripheralManagerService
import java.io.IOException
import com.google.android.things.contrib.driver.button.Button



class TouchActivity : Activity() {

    private var redLED: Gpio?   = null
    private var blueLED: Gpio?  = null
    private var greenLED: Gpio? = null

    private var inputA: ButtonInputDriver?   = null
    private var inputB: ButtonInputDriver?   = null
    private var inputC: ButtonInputDriver?   = null

    private val TOUCH_BTN_A_PIN = "BCM21" //private static final String TOUCH_BTN_A_PIN = "BCM21";
    private val TOUCH_BTN_B_PIN = "BCM20" //private static final String TOUCH_BTN_A_PIN = "BCM20";
    private val TOUCH_BTN_C_PIN = "BCM16" //private static final String TOUCH_BTN_A_PIN = "BCM16";


    private val RED_LED_PIN     = "BCM6"
    private val BLUE_LED_PIN    = "BCM26"
    private val GREEN_LED_PIN   = "BCM19"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val service = PeripheralManagerService()

        try {
            inputA = ButtonInputDriver(
                    TOUCH_BTN_A_PIN,
                    Button.LogicState.PRESSED_WHEN_LOW,
                    KeyEvent.KEYCODE_0)
            inputB = ButtonInputDriver(
                    TOUCH_BTN_B_PIN,
                    Button.LogicState.PRESSED_WHEN_LOW,
                    KeyEvent.KEYCODE_1)
            inputC = ButtonInputDriver(
                    TOUCH_BTN_C_PIN,
                    Button.LogicState.PRESSED_WHEN_LOW,
                    KeyEvent.KEYCODE_2)

            inputA?.register()
            inputB?.register()
            inputC?.register()

            redLED   = service.openGpio(RED_LED_PIN)
            blueLED  = service.openGpio(BLUE_LED_PIN)
            greenLED = service.openGpio(GREEN_LED_PIN)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        try {
            redLED?.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
            blueLED?.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
            greenLED?.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        setLed(keyCode, true)
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        setLed(keyCode, false)
        return super.onKeyUp(keyCode, event)
    }

    fun setLed(keyCode: Int, on: Boolean) {
        when (keyCode) {
            KeyEvent.KEYCODE_0 -> setLedValue(on, redLED)
            KeyEvent.KEYCODE_1 -> setLedValue(on, greenLED)
            KeyEvent.KEYCODE_2 -> setLedValue(on, blueLED)
        }
    }

    /**
     * Update the value of the LED output.
     */
    private fun setLedValue(value: Boolean, gpio: Gpio?) {
        try {
            gpio?.value = value
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    override fun onDestroy() {
        try {
            inputA?.unregister()
            inputA?.close()

            inputB?.unregister()
            inputB?.close()

            inputC?.unregister()
            inputC?.close()

            greenLED?.close()
            blueLED?.close()
            redLED?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        super.onDestroy()
    }
}
