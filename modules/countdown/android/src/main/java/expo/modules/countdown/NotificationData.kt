package expo.modules.countdown

import expo.modules.countdown.contants.StateEnum

object NotificationData {
    var state: StateEnum = StateEnum.STOP;
    var coundownTime:Long = 0L;
}