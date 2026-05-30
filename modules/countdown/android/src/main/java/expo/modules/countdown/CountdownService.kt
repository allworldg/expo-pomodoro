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
import expo.modules.countdown.contants.ChannelId
import expo.modules.countdown.contants.Constants
import expo.modules.countdown.contants.IntentExtras
import expo.modules.countdown.contants.NotificationId
import expo.modules.countdown.contants.NotificationText
import expo.modules.countdown.contants.StateEnum


class CountdownService : Service() {
    private lateinit var notificationManager: NotificationManager
    private val pendingIntent: PendingIntent by lazy {
        PendingIntent.getActivity(
            this,
            0,
            packageManager.getLaunchIntentForPackage(packageName),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun createNotificationCompatBuilder(channelId: String): NotificationCompat.Builder {
        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentIntent(pendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC).setShowWhen(false)
    }

    private fun getStateText(state: StateEnum): String {
        val stateText: String = when (state.name) {
            StateEnum.FOCUSING.name -> NotificationText.TEXT_FOCUSING
            StateEnum.RESTING.name -> Constants.RESTING_STR
            StateEnum.STOP.name -> Constants.FINISH_STR
            else -> {
                "wrong state : ${state.name}"
            }
        }
        return stateText
    }

    private fun getContentText(state: StateEnum, remainTime: Long): String {
        val contentText: String = if (state.name == StateEnum.STOP.name) {
            ""
        } else {
            Utils.getClockTimeStr(remainTime)
        }
        return contentText
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Constants.ActionEnum.START.name -> {
                val remainTime = intent.getLongExtra(IntentExtras.COUNTDOWN_TIME, 0L)
                val stateName = intent.getStringExtra(IntentExtras.COUNTDOWN_STATE)
                val state =
                    stateName?.let { runCatching { StateEnum.valueOf(it) }.getOrNull() }
                        ?: StateEnum.STOP
                val notification =
                    createNotificationCompatBuilder(ChannelId.FOREGROUND).setCategory(
                        CATEGORY_STOPWATCH
                    ).setContentTitle(getStateText(state))
                        .setContentText(getContentText(state, remainTime)).build()
                notificationManager.cancel(NotificationId.NORMAL)
                startForeground(NotificationId.FOREGROUND, notification)
            }

            Constants.ActionEnum.UPDATE.name -> {
                val remainTime = intent.getLongExtra(IntentExtras.COUNTDOWN_TIME, 0L)
                val stateName = intent.getStringExtra(IntentExtras.COUNTDOWN_STATE)
                val state =
                    stateName?.let { runCatching { StateEnum.valueOf(it) }.getOrNull() }
                        ?: StateEnum.FOCUSING

                val notification =
                    createNotificationCompatBuilder(ChannelId.FOREGROUND).setCategory(
                        CATEGORY_STOPWATCH
                    ).setContentTitle(getStateText(state))
                        .setContentText(getContentText(state, remainTime)).build();
                notificationManager.notify(NotificationId.FOREGROUND, notification)
            }

            Constants.ActionEnum.FINISH.name -> {
                notificationManager.cancel(NotificationId.NORMAL)
                val stateName = intent.getStringExtra(IntentExtras.COUNTDOWN_STATE)
                val title = when (stateName) {
                    StateEnum.FOCUSING.name -> {
                        NotificationText.TEXT_FOCUS_COMPLETE
                    }

                    StateEnum.RESTING.name -> {
                        NotificationText.TEXT_REST_COMPLETE
                    }
                    else -> {}
                }.toString()
                val notification =
                    createNotificationCompatBuilder(ChannelId.NORMAL).setContentTitle(title)
                        .setAutoCancel(true).build()
                notificationManager.notify(NotificationId.NORMAL, notification)
            }

            Constants.ActionEnum.COMPLETE.name -> {
                notificationManager.cancel(NotificationId.NORMAL)
                stopForeground(STOP_FOREGROUND_REMOVE)
                val notification =
                    createNotificationCompatBuilder(ChannelId.NORMAL).setContentTitle(
                        NotificationText.TEXT_COMPLETE
                    )
                        .setAutoCancel(true)
                        .build();
                notificationManager.notify(NotificationId.NORMAL, notification)
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
                ChannelId.FOREGROUND,
                Constants.ChannelName.FOREGROUND.value,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                lockscreenVisibility = VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(foregroundChannel)

            val normalChannel = NotificationChannel(
                ChannelId.NORMAL,
                Constants.ChannelName.FOREGROUND.value,
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
