package expo.modules.countdown

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import expo.modules.countdown.contants.Constants
import expo.modules.countdown.contants.EventTypeEnum
import expo.modules.countdown.contants.IntentExtras
import expo.modules.countdown.contants.StateEnum
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import java.net.URL

class CountdownModule : Module() {
    private var internalTime: Long = 500
    private val countDownData = CountDownData()
    private val workerThread = HandlerThread("countdown").apply {
        start()
    }
    private val handler = Handler(workerThread.looper)
    private var preSeond: Long = -1L;
    private val runnable = object : Runnable {
        override fun run() {
            countdown()
            if (countDownData.state != StateEnum.STOP) {
                handler.postDelayed(this, internalTime)
            }
        }
    }

    private fun countdown() {
        val remainTime = countDownData.targetTime - System.currentTimeMillis()
        if (remainTime > 0) {
            emitTick(remainTime)
            val second = remainTime / Constants.SECOND;
            if (second != preSeond) {
                preSeond = second
                appContext.reactContext?.let {
                    val intent = Intent(it, CountdownService::class.java).apply {
                        action = Constants.ACTIONENUM.UPDATE.name;
                        putExtra(IntentExtras.COUNTDOWN_TIME, remainTime);
                        putExtra(IntentExtras.COUNTDOWN_STATE, countDownData.state.name)
                    }
                    it.startService(intent)
                }
            }
            internalTime = Constants.TimeEnum.NORMAL.value
            return
        }
        onStateChanged()
        internalTime = Constants.TimeEnum.QUICK.value
    }

    private fun onStateChanged() {
        preSeond = -1L
        when (countDownData.state) {
            StateEnum.FOCUSING -> {
                if (hasRest()) {
                    countDownData.state = StateEnum.RESTING
                    countDownData.targetTime =
                        countDownData.rest * Constants.MINUTE
                    countDownData.targetTime += System.currentTimeMillis()
                } else {
                    if (hasNextCycle()) {
                        countDownData.curCycle++
                        countDownData.state = StateEnum.FOCUSING
                        countDownData.targetTime =
                            countDownData.pomodoro * Constants.MINUTE
                        countDownData.targetTime += System.currentTimeMillis()
                    } else {
                        finish()
                    }
                }
                sendStateChangeEvent()
            }

            StateEnum.RESTING -> {
                if (hasNextCycle()) {
                    countDownData.curCycle++
                    countDownData.state = StateEnum.FOCUSING
                    countDownData.targetTime =
                        countDownData.pomodoro * Constants.MINUTE
                    countDownData.targetTime += System.currentTimeMillis()
                } else {
                    finish()
                }
                sendStateChangeEvent()
            }

            else -> {
                println("wrong state")
            }
        }
    }

    private fun finish() {
        handler.removeCallbacks(runnable)
        countDownData.state = StateEnum.STOP
        appContext.reactContext?.let {
            val intent = Intent(it, CountdownService::class.java).apply {
                action = Constants.ACTIONENUM.STOP.name;
                putExtra(IntentExtras.COUNTDOWN_TIME, -1L);
                putExtra(IntentExtras.COUNTDOWN_STATE, countDownData.state.name)
            }
            it.startService(intent)
        }

    }

    private fun hasRest(): Boolean {
        return countDownData.rest > 0
    }

    private fun hasNextCycle(): Boolean {
        return countDownData.curCycle < countDownData.cycles
    }

    private fun sendStateChangeEvent() {
        sendEvent(
            EventTypeEnum.STATECHANGE.value,
            mapOf(
                "state" to countDownData.state.value,
                "curCycle" to countDownData.curCycle,
                "cycles" to countDownData.cycles
            )
        )
    }

    private fun start() {
        preSeond = -1L;
        appContext.reactContext?.let {
            val intent = Intent(it, CountdownService::class.java).apply {
                action = Constants.ACTIONENUM.START.name;
                putExtra(
                    IntentExtras.COUNTDOWN_TIME,
                    countDownData.targetTime - System.currentTimeMillis()
                );
                putExtra(IntentExtras.COUNTDOWN_STATE, countDownData.state.name)
            }
            ContextCompat.startForegroundService(it, intent)
        }
        handler.removeCallbacks(runnable)
        sendStateChangeEvent()
        handler.postDelayed(runnable, 10L)
    }

    private fun emitTick(remainTime: Long) {
        sendEvent(EventTypeEnum.TICK.value, mapOf("remainTime" to remainTime))
    }

    private fun stop() {
        handler.removeCallbacks(runnable)
        appContext.reactContext?.let {
            val intent = Intent(it, CountdownService::class.java)
            it.stopService(intent)
        }
    }

    private fun init(stateData: CountDownData) {
        countDownData.apply {
            this.pomodoro = stateData.pomodoro
            this.cycles = stateData.cycles
            this.rest = stateData.rest
            this.state = StateEnum.FOCUSING
            this.curCycle = 1
            countDownData.targetTime =
                this.pomodoro.toLong() * Constants.MINUTE + System.currentTimeMillis()
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return
        }
        appContext.currentActivity?.let {
            ActivityCompat.requestPermissions(
                it,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1001
            )
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
        Function("requestNotificationPermission") {
        }

        AsyncFunction("startCountdown") { stateData: CountDownData ->
            init(stateData)
            requestNotificationPermission()
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
