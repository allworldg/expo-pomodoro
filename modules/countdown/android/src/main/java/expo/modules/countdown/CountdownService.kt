package expo.modules.countdown

import android.app.Notification
import android.app.Notification.CATEGORY_STOPWATCH
import android.app.Notification.VISIBILITY_PUBLIC
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import expo.modules.countdown.contants.Constants
import expo.modules.countdown.contants.Constants.CHANNEL_ID
import expo.modules.countdown.contants.IntentExtras
import expo.modules.countdown.contants.StateEnum


class CountdownService : Service() {
    private val foreGroundId = 1
    private val normalId = 2
    private lateinit var notificationManager: NotificationManager
    private val pendingIntent: PendingIntent by lazy {
        PendingIntent.getActivity(
            this,
            0,
            packageManager.getLaunchIntentForPackage(packageName),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun createNotificationBuilder(
        channelId: String, state: StateEnum, countdownTime: Long
    ): NotificationCompat.Builder {
        val stateText: String = when (state.name) {
            StateEnum.FOCUSING.name -> Constants.FOCUSING_STR
            StateEnum.RESTING.name -> Constants.RESTING_STR
            StateEnum.STOP.name -> Constants.FINISH_STR
            else -> {
                "wrong state : ${state.name}"
            }
        }
        val contentText: String = if (state.name == StateEnum.STOP.name) {
            ""
        } else {
            Utils.getClockTimeStr(countdownTime)
        }

        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle(stateText).setContentText(contentText).setContentIntent(pendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC).setShowWhen(false)
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Constants.ACTIONENUM.START.name -> {
                val countdownTime = intent.getLongExtra(IntentExtras.COUNTDOWN_TIME, 0L)
                val stateName = intent.getStringExtra(IntentExtras.COUNTDOWN_STATE)
                val state =
                    stateName?.let { runCatching { StateEnum.valueOf(it) }.getOrNull() }
                        ?: StateEnum.STOP
                val notification = createNotificationBuilder(
                    CHANNEL_ID.FOREGROUND.name,
                    state,
                    countdownTime
                ).setCategory(CATEGORY_STOPWATCH).build()
                println("start foreground notification")
                startForeground(foreGroundId, notification)
            }

            Constants.ACTIONENUM.UPDATE.name -> {
                val countdownTime = intent.getLongExtra(IntentExtras.COUNTDOWN_TIME, 0L)
                val stateName = intent.getStringExtra(IntentExtras.COUNTDOWN_STATE)
                val state =
                    stateName?.let { runCatching { StateEnum.valueOf(it) }.getOrNull() }
                        ?: StateEnum.FOCUSING
                val notification = createNotificationBuilder(
                    CHANNEL_ID.FOREGROUND.name,
                    state,
                    countdownTime
                ).setCategory(CATEGORY_STOPWATCH).build()
                notificationManager.notify(foreGroundId, notification)
            }

            Constants.ACTIONENUM.STOP.name -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
                val countdownTime = intent.getLongExtra(IntentExtras.COUNTDOWN_TIME, 0L)
                val stateName = intent.getStringExtra(IntentExtras.COUNTDOWN_STATE)
                val state =
                    stateName?.let { runCatching { StateEnum.valueOf(it) }.getOrNull() }
                        ?: StateEnum.FOCUSING
                val notification = createNotificationBuilder(
                    CHANNEL_ID.NORMAL.name,
                    state,
                    countdownTime
                ).setAutoCancel(true).build()
                notificationManager.notify(normalId, notification)
                stopSelf()
            }
        }
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val foregroundChannel = NotificationChannel(
                CHANNEL_ID.FOREGROUND.name,
                Constants.CHANNEL_NAME.FOREGROUND.value,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                lockscreenVisibility = VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(foregroundChannel)

            val normalChannel = NotificationChannel(
                CHANNEL_ID.NORMAL.name,
                Constants.CHANNEL_NAME.FOREGROUND.value,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableLights(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(normalChannel)
        }
    }


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
