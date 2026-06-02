package expo.modules.countdown

import expo.modules.countdown.contants.StateEnum
import expo.modules.kotlin.records.Field
import expo.modules.kotlin.records.Record

class CountDownData : Record {
    @Field
    var pomodoro: Int = 0;

    @Field
    var rest: Int = 0;

    @Field
    var cycles: Int = 1;

    @Field
    var targetTime: Long = 0L;

    @Field
    var state: StateEnum = StateEnum.STOP;

    @Field
    var curCycle: Int = 1;


    override fun toString(): String {
        return "" +
                "CountDownState(" +
                "pomodoro=$pomodoro, " +
                "rest=$rest, " +
                "cycles=$cycles, " +
                "curcycle = $curCycle" +
                "targetTime=$targetTime, " +
                "curState=$state)"
    }
}