package expo.modules.countdown

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import androidx.core.app.NotificationCompat
import expo.modules.countdown.contants.Constants
import expo.modules.countdown.contants.Constants.CHANNEL_ID
import expo.modules.countdown.contants.StateEnum
import kotlinx.coroutines.Runnable


class CountdownService : Service() {
    private val id = 1
    private var isForeground = false;
    private val pendingIntent: PendingIntent by lazy {
        PendingIntent.getActivity(
            this,
            0,
            packageManager.getLaunchIntentForPackage(packageName),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun buildNotification(state: StateEnum, countdownTime: Long): Notification {
        val stateText: String = when (state.value) {
            StateEnum.FOCUSING.value -> Constants.FOCUSING_STR
            StateEnum.RESTING.value -> Constants.RESTING_STR
            else -> {
                "error state in notification"
            }
        }
        println("state is ${state.value} + countTime is ${countdownTime}")

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle(stateText)
            .setContentText(Utils.convertTimeToStr(countdownTime))
            .setContentIntent(pendingIntent).build()
    }

    private fun updateNotification(state: StateEnum, countdownTime: Long) {
        val notification = buildNotification(state, countdownTime);
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager;
        notificationManager.notify(id, notification);
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Constants.ACTIONENUM.ACTION_START.name -> {
                if (!isForeground) {
                    val notification =
                        buildNotification(NotificationData.state, NotificationData.coundownTime);
                    startForeground(1, notification);
                }
            }

            Constants.ACTIONENUM.ACTION_UPDATE.name -> {
                updateNotification(NotificationData.state, NotificationData.coundownTime);
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
        stopForeground(STOP_FOREGROUND_REMOVE)
    }
}