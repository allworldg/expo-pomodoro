package expo.modules.countdown

import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import expo.modules.countdown.contants.Constants
import expo.modules.countdown.contants.EventTypeEnum
import expo.modules.countdown.contants.StateEnum
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import java.net.URL

class CountdownModule : Module() {
    private var internalTime: Long = 500
    private val countDownData = CountDownData()
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            countdown()
            if (countDownData.curState != StateEnum.STOP) {
                handler.postDelayed(this, internalTime)
            }
        }
    }

    private fun countdown() {
        val remainTime = countDownData.targetTime - System.currentTimeMillis()
        if (remainTime > 0) {
            emitTick(remainTime)
            internalTime = Constants.TimeEnum.NORMAL.value
            return
        }
        onStateFinished()
        internalTime = Constants.TimeEnum.QUICK.value
    }

    private fun onStateFinished() {
        when (countDownData.curState) {
            StateEnum.FOCUSING -> {
                if (hasRest()) {
                    countDownData.curState = StateEnum.RESTING
                    countDownData.targetTime =
                        countDownData.rest * Constants.MINUTE
                    countDownData.targetTime += System.currentTimeMillis()
                } else {
                    countDownData.curCycle++
                    if (hasNextCycle()) {
                        countDownData.curState = StateEnum.FOCUSING
                        countDownData.targetTime =
                            countDownData.pomodoro * Constants.MINUTE
                        countDownData.targetTime += System.currentTimeMillis()
                    } else {
                        finish()
                    }
                }
            }

            StateEnum.RESTING -> {
                countDownData.curCycle++
                if (hasNextCycle()) {
                    countDownData.curState = StateEnum.FOCUSING
                    countDownData.targetTime =
                        countDownData.pomodoro * Constants.MINUTE
                    countDownData.targetTime += System.currentTimeMillis()
                } else {
                    finish()
                }
            }

            else -> {
                println("wrong state")
            }
        }
    }

    private fun finish() {
        handler.removeCallbacks(runnable)
        countDownData.curState = StateEnum.STOP
        sendEvent(EventTypeEnum.STOP.value)
    }

    private fun hasRest(): Boolean {
        return countDownData.rest > 0
    }

    private fun hasNextCycle(): Boolean {
        return countDownData.curCycle < countDownData.cycles
    }


    private fun start() {
        handler.removeCallbacks(runnable)
        handler.postDelayed(runnable, 10L)
    }

    private fun emitTick(remainTime: Long) {
        sendEvent(EventTypeEnum.TICK.value, mapOf("remainTime" to remainTime))
    }

    private fun stop() {
        handler.removeCallbacks(runnable)
    }

    private fun init(stateData: CountDownData) {
        countDownData.apply {
            this.pomodoro = stateData.pomodoro
            this.cycles = stateData.cycles
            this.rest = stateData.rest
            this.curState = StateEnum.FOCUSING
            this.curCycle = 0
            countDownData.targetTime =
                this.pomodoro.toLong() * Constants.MINUTE + System.currentTimeMillis()
        }
    }


    // Each module class must implement the definition function. The definition consists of components
    // that describes the module's functionality and behavior.
    // See https://docs.expo.dev/modules/module-api for more details about available components.
    override fun definition() = ModuleDefinition {
        // Sets the name of the module that JavaScript code will use to refer to the module. Takes a string as an argument.
        // Can be inferred from module's class name, but it's recommended to set it explicitly for clarity.
        // The module will be accessible from `requireNativeModule('Countdown')` in JavaScript.
        Name("Countdown")


        // Defines constant property on the module.
        Constant("PI") {
            Math.PI
        }

        // Defines event names that the module can send to JavaScript.
        Events(EventTypeEnum.TICK.value, EventTypeEnum.STATECHANGE.value, EventTypeEnum.STOP.value)

        // Defines a JavaScript synchronous function that runs the native code on the JavaScript thread.
        Function("hello") {
            "Hello world22"
        }

        Function(name = "stopCountdown") {
            stop()
        }

        AsyncFunction("startCountdown") { stateData: CountDownData ->
            init(stateData)

            appContext.reactContext?.let {
                val intent = Intent(it, CountdownService::class.java)
                ContextCompat.startForegroundService(it, intent)
            }
            start()
        }

        // Defines a JavaScript function that always returns a Promise and whose native code
        // is by default dispatched on the different thread than the JavaScript runtime runs on.
        AsyncFunction("setValueAsync") { value: String ->
            // Send an event to JavaScript.
            sendEvent(
                "onChange", mapOf(
                    "value" to value
                )
            )
        }
        OnDestroy {
            handler.removeCallbacksAndMessages(null)
        }


        // Enables the module to be used as a native view. Definition components that are accepted as part of
        // the view definition: Prop, Events.
        View(CountdownView::class) {
            // Defines a setter for the `url` prop.
            Prop("url") { view: CountdownView, url: URL ->
                view.webView.loadUrl(url.toString())
            }
            // Defines an event that the view can send to JavaScript.
            Events("onLoad")
        }
    }
}
