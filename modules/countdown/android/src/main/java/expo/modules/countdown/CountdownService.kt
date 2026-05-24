package expo.modules.countdown

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import expo.modules.countdown.contants.Constants
import expo.modules.countdown.contants.Constants.CHANNEL_ID
import expo.modules.countdown.contants.StateEnum


class CountdownService : Service() {
    private val foreGroundId = 1
    private val normalId = 2;
    private val pendingIntent: PendingIntent by lazy {
        PendingIntent.getActivity(
            this,
            0,
            packageManager.getLaunchIntentForPackage(packageName),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun createNotificationBuilder(
        state: StateEnum,
        countdownTime: Long
    ): NotificationCompat.Builder {
        val stateText: String = when (state.value) {
            StateEnum.FOCUSING.value -> Constants.FOCUSING_STR
            StateEnum.RESTING.value -> Constants.RESTING_STR
            StateEnum.STOP.value -> Constants.FINISH_STR
            else -> {
                "wrong state : ${state.name}"
            }
        }
        val contentText: String = if (state.value == StateEnum.STOP.value) {
            ""
        } else {
            Utils.getClockTimeStr(countdownTime)
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle(stateText)
            .setContentText(contentText)
            .setContentIntent(pendingIntent)
    }

    private fun createNotification(
        state: StateEnum,
        countdownTime: Long,
        id: Int,
        isKeeped: Boolean
    ) {
        val notificationBuilder = createNotificationBuilder(state, countdownTime);
        if (!isKeeped) {
            notificationBuilder.setAutoCancel(true);
        }
        var notification = notificationBuilder.build()
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager;
        notificationManager.notify(id, notification);
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Constants.ACTIONENUM.START.name -> {
                val notification =
                    createNotificationBuilder(
                        NotificationData.state,
                        NotificationData.coundownTime
                    ).build();
                startForeground(1, notification);
            }

            Constants.ACTIONENUM.UPDATE.name -> {
                createNotification(
                    NotificationData.state,
                    NotificationData.coundownTime,
                    foreGroundId,
                    true
                );
            }

            Constants.ACTIONENUM.STOP.name -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
                createNotification(
                    NotificationData.state,
                    NotificationData.coundownTime,
                    normalId,
                    false,
                )
                stopSelf();
            }
        }
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Countdown Service", NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}