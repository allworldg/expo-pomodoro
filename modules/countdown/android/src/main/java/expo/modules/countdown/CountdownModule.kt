package expo.modules.countdown

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import expo.modules.countdown.contants.Constants
import expo.modules.countdown.contants.Event
import expo.modules.countdown.contants.InitData
import expo.modules.countdown.contants.IntentExtras
import expo.modules.countdown.contants.StateEnum
import expo.modules.countdown.data.SettingDatabase
import expo.modules.countdown.data.entity.CountdownSetting
import expo.modules.countdown.data.entity.FocusDailyRecord
import expo.modules.kotlin.functions.Coroutine
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CountdownModule : Module() {
    private var internalTime: Long = Constants.InternalTime.NORMAL
    private val countDownData = CountDownData()
    private var preSeond: Long = -1L;
    private val coroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var countdownJob: Job? = null
    private var ringtoneUri:String? = null
    private val db: SettingDatabase by lazy {
        Room.databaseBuilder(
            requireNotNull(appContext.reactContext) { "reactContext is null" },
            SettingDatabase::class.java,
            "setting.db"
        ).build()
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
                        action = Constants.ActionEnum.UPDATE.name;
                        putExtra(IntentExtras.COUNTDOWN_TIME, remainTime);
                        putExtra(IntentExtras.COUNTDOWN_STATE, countDownData.state.name)
                    }
                    it.startService(intent)
                }
            }
            internalTime = Constants.InternalTime.NORMAL
            return
        }
        playMusic()
        onStateChanged()
        internalTime = Constants.InternalTime.QUICK
    }

    private fun playMusic() {
        appContext.reactContext?.let {
            val intent = Intent(it, CountdownService::class.java).apply {
                action = Constants.ActionEnum.PLAYMUSIC.name
                putExtra(IntentExtras.PLAY_MUSIC,ringtoneUri)
            }
            it.startService(intent)
        }
    }

    private fun onStateChanged() {
        preSeond = -1L
        var shouldNotifyFinished = false
        val finishState = countDownData.state
        when (countDownData.state) {
            StateEnum.FOCUSING -> {
                var focusDao = db.recordDao()
                val today = SimpleDateFormat(
                    "yyyy-MM-dd", Locale.getDefault()
                ).format(Date())
                var result = focusDao.search(today)
                if (result == null) {
                    var record = FocusDailyRecord(
                        today, 1, countDownData.pomodoro * Constants.MINUTE
                    )
                    result = record
                    focusDao.insertAll(record)
                } else {
                    result = result.copy(
                        focusCount = result.focusCount + 1,
                        focusDuration = result.focusDuration + countDownData.pomodoro * Constants.MINUTE
                    )
                    focusDao.update(
                        result
                    )
                }
                val totalDuration = focusDao.getTotalFocusDuration()
                sendEvent(
                    Event.RECORD,
                    mapOf(
                        "focusCount" to result.focusCount,
                        "focusDuration" to result.focusDuration,
                        "totalDuration" to totalDuration
                    )
                )

                if (hasRest()) {
                    shouldNotifyFinished = true;
                    countDownData.state = StateEnum.RESTING
                    countDownData.targetTime = countDownData.rest * Constants.MINUTE
                    countDownData.targetTime += System.currentTimeMillis()
                } else {
                    if (hasNextCycle()) {
                        shouldNotifyFinished = true;
                        countDownData.curCycle++
                        countDownData.state = StateEnum.FOCUSING
                        countDownData.targetTime = countDownData.pomodoro * Constants.MINUTE
                        countDownData.targetTime += System.currentTimeMillis()
                    } else {
                        finish()
                    }
                }
                if (shouldNotifyFinished) {
                    sendFinishIntent(finishState);
                }
                sendStateChangeEvent()
            }

            StateEnum.RESTING -> {
                if (hasNextCycle()) {
                    shouldNotifyFinished = true
                    countDownData.curCycle++
                    countDownData.state = StateEnum.FOCUSING
                    countDownData.targetTime = countDownData.pomodoro * Constants.MINUTE
                    countDownData.targetTime += System.currentTimeMillis()
                } else {
                    finish()
                }
                if (shouldNotifyFinished) {
                    sendFinishIntent(finishState);
                }
                sendStateChangeEvent()
            }

            else -> {
                println("wrong state")
            }
        }
    }

    private fun finish() {
        countdownJob?.cancel()
        countDownData.state = StateEnum.STOP
        appContext.reactContext?.let {
            val intent = Intent(it, CountdownService::class.java).apply {
                action = Constants.ActionEnum.COMPLETE.name;
            }
            it.startService(intent)
        }
    }

    private fun sendFinishIntent(finishedState: StateEnum) {
        appContext.reactContext?.let {
            val intent = Intent(it, CountdownService::class.java).apply {
                action = Constants.ActionEnum.FINISH.name;
                putExtra(IntentExtras.COUNTDOWN_STATE, finishedState.name)
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
            Event.STATE_CHANGE,
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
                action = Constants.ActionEnum.START.name;
                putExtra(
                    IntentExtras.COUNTDOWN_TIME,
                    countDownData.targetTime - System.currentTimeMillis()
                );
                putExtra(IntentExtras.COUNTDOWN_STATE, countDownData.state.name)
            }
            ContextCompat.startForegroundService(it, intent)
        }
        countdownJob?.cancel()
        sendStateChangeEvent()
        countdownJob = coroutineScope.launch {
            delay(Constants.InternalTime.QUICK)
            while (isActive && countDownData.state != StateEnum.STOP) {
                countdown()
                delay(internalTime)
            }
        }

    }

    private fun emitTick(remainTime: Long) {
        sendEvent(Event.TICK, mapOf("remainTime" to remainTime))
    }


    private fun stop() {
        countdownJob?.cancel()
        appContext.reactContext?.let {
            val intent = Intent(it, CountdownService::class.java)
            it.stopService(intent)
        }
    }


    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return
        }
        appContext.currentActivity?.let {
            ActivityCompat.requestPermissions(
                it, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001
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
        Events(
            Event.TICK,
            Event.STATE_CHANGE,
            Event.STOP,
            Event.RECORD,
            Event.RINGTONE_CHANGE
        )

        AsyncFunction("selectMusic") Coroutine { ->
            val activity = requireNotNull(appContext.currentActivity) {
                "Activity is null"
            }
            var selectedUri = withContext(Dispatchers.IO) {
                db.settingDao().getFirst().ringtoneUri
            }
            this
            val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
                putExtra(
                    RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT,
                    false
                )
                putExtra(
                    RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
                    selectedUri?.let { Uri.parse(it) }
                )
            }
            activity.startActivityForResult(intent, 1001)
        }

        OnActivityResult { _, payload ->

            if (payload.requestCode != 1001) {
                return@OnActivityResult
            }

            if (payload.resultCode != Activity.RESULT_OK) {
                return@OnActivityResult
            }


            val uri = payload.data
                ?.getParcelableExtra<Uri>(
                    RingtoneManager.EXTRA_RINGTONE_PICKED_URI
                )

            val title = RingtoneManager.getRingtone(appContext.reactContext, uri)
                .getTitle(appContext.reactContext)


            sendEvent(
                Event.RINGTONE_CHANGE, mapOf(
                    "title" to title
                )
            )
            ringtoneUri = uri?.toString()

            coroutineScope.launch {
                var setting = db.settingDao().getFirst()
                db.settingDao().update(setting.copy(ringtoneUri = uri?.toString()))
            }
        }

        Function("getCountdownSetting") {
            var settingDao = db.settingDao()
            lateinit var countdownSetting: CountdownSetting
            if (settingDao.getAll().isEmpty()) {
                countdownSetting =
                    CountdownSetting(
                        1,
                        InitData.POMODORO,
                        InitData.REST,
                        InitData.CYCLE,
                        InitData.ringToneUri,
                    )
                settingDao.insertAll(countdownSetting)
            } else {
                countdownSetting = settingDao.getFirst();
            }
            ringtoneUri = countdownSetting.ringtoneUri

            val ringtoneName =
                RingtoneManager.getRingtone(
                    appContext.reactContext,
                    countdownSetting.ringtoneUri?.let { Uri.parse(it) }
                ).getTitle(appContext.reactContext)

            return@Function mapOf(
                "pomodoro" to countdownSetting.pomodoro,
                "rest" to countdownSetting.rest,
                "cycles" to countdownSetting.cycles,
                "ringtoneName" to ringtoneName
            )
        }
        AsyncFunction("updateSetting") { countDownData: CountDownData ->
            var settingDao = db.settingDao()
            var setting = settingDao.getFirst()
            settingDao.update(
                setting.copy(
                    pomodoro = countDownData.pomodoro,
                    rest = countDownData.rest,
                    cycles = countDownData.cycles
                )
            )
        }

        Function(name = "stopCountdown") {

            stop()
        }
        Function("testOpenMusic") {
            val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                putExtra(
                    RingtoneManager.EXTRA_RINGTONE_TYPE,
                    RingtoneManager.TYPE_NOTIFICATION
                )
            }
        }

        Function("startCountdown") { countdownData: CountDownData ->
            countDownData.apply {
                this.pomodoro = countdownData.pomodoro
                this.cycles = countdownData.cycles
                this.rest = countdownData.rest
                this.state = StateEnum.FOCUSING
                this.curCycle = 1
                countDownData.targetTime =
                    this.pomodoro.toLong() * Constants.MINUTE + System.currentTimeMillis()
            }
            internalTime = Constants.InternalTime.NORMAL
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

        AsyncFunction("getRecord") {
            val recordDao = db.recordDao()
            val totalDuration = recordDao.getTotalFocusDuration();

            var result = recordDao.search(
                SimpleDateFormat(
                    "yyyy-MM-dd", Locale.getDefault()
                ).format(Date())
            )
            mapOf(
                "focusCount" to (result?.focusCount ?: 0),
                "focusDuration" to (result?.focusDuration ?: 0),
                "totalDuration" to totalDuration
            )
        }
        OnDestroy {
            countdownJob?.cancel()
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
