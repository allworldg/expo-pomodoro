package expo.modules.countdown.contants

object Constants {

   val CHANNEL_ID = "countdown_channel"
   const val MILLISECOND = 1L;
   const val SECOND = 1000*MILLISECOND;
   const val MINUTE = 3*SECOND;
   enum class TimeEnum(val value:Long){
      QUICK(10L),
      NORMAL(500L)
   }
   enum class ACTIONENUM(){
      START,
      UPDATE,
      STOP

   }
   const val FOCUSING_STR = "正在专注"
   const val RESTING_STR = "正在休息"
   const val FINISH_STR = "已全部完成！"
}