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
    var targetTime: Long = 0L;
    var curState: StateEnum = StateEnum.STOP;
    var curCycle:Int = 1;


    override fun toString(): String {
        return "" +
                "CountDownState(" +
                "pomodoro=$pomodoro, " +
                "rest=$rest, " +
                "totalLoop=$cycles, " +
                "targetTime=$targetTime, " +
                "curState=$curState)"
    }
}