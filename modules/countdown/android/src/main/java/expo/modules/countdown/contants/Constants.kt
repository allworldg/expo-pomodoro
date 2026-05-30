package expo.modules.countdown.contants

object Constants {
    const val MILLISECOND = 1L;
    const val SECOND = 1000 * MILLISECOND;
    const val MINUTE = 3 * SECOND;

    enum class TimeEnum(val value: Long) {
        QUICK(10L),
        NORMAL(500L)
    }

    enum class ActionEnum() {
        START,
        UPDATE,
        FINISH,
        COMPLETE
    }


    enum class ChannelName(val value: String) {
        FOREGROUND("倒计时"), NORMAL("普通通知")
    }

    const val FOCUSING_STR = "正在专注"
    const val RESTING_STR = "正在休息"
    const val FINISH_STR = "已全部完成！"
}

object NotificationText {
    const val TEXT_FOCUSING = "正在专注"
    const val TEXT_RESTING = "正在休息"
    const val TEXT_FOCUS_COMPLETE="已完成专注"
    const val TEXT_REST_COMPLETE="已完成休息"
    const val TEXT_COMPLETE = "已全部完成！"
}

object IntentExtras {
    const val COUNTDOWN_TIME = "countdownTime";
    const val COUNTDOWN_STATE = "countdownState";
}

object ChannelId {
    const val FOREGROUND = "foreground"
    const val NORMAL = "normal"
}

object NotificationId {
    const val FOREGROUND = 1

    const val NORMAL = 2
}
