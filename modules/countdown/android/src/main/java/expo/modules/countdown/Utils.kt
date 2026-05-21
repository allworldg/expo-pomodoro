package expo.modules.countdown

import expo.modules.countdown.contants.Constants

object Utils {
    fun convertTimeToStr(time: Long): String {
        val minute = time / Constants.MINUTE;
        val second = (time % Constants.MINUTE) / Constants.SECOND;
        return "$minute:$second"
    }
}