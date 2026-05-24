package expo.modules.countdown

import expo.modules.countdown.contants.Constants

object Utils {
    fun getClockTimeStr(time: Long): String {
        val minute: String = (time.floorDiv(Constants.MINUTE)).toString().padStart(2, '0')
        val second =
            ((time % Constants.MINUTE).floorDiv(Constants.SECOND)).toString().padStart(2, '0')
        return "$minute:$second"
    }
}