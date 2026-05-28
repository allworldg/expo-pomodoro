package expo.modules.countdown.contants

object Constants {
    const val MILLISECOND = 1L;
    const val SECOND = 1000 * MILLISECOND;
    const val MINUTE = 3 * SECOND;

    enum class TimeEnum(val value: Long) {
        QUICK(10L),
        NORMAL(500L)
    }

    enum class ACTIONENUM() {
        START,
        UPDATE,
        STOP
    }


    enum class CHANNEL_NAME(val value: String) {
        FOREGROUND("倒计时"), NORMAL("普通通知")
    }

    const val FOCUSING_STR = "正在专注"
    const val RESTING_STR = "正在休息"
    const val FINISH_STR = "已全部完成！"
}

object IntentExtras {
    const val COUNTDOWN_TIME = "countdownTime";
    const val COUNTDOWN_STATE = "countdownState";
}

object Channel_ID {
    const val FOREGROUND = "foreground"
    const val NORMAL = "normal"
}

object Notification_ID{
    const val FOREGROUND = 1

    const val NORMAL = 2
}
