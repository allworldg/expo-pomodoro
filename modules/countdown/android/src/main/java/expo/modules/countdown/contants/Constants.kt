package expo.modules.countdown.contants

object Constants {
   const val MILLISECOND = 1L;
   const val SECOND = 1000*MILLISECOND;
   const val MINUTE = 3*SECOND;
   enum class TimeEnum(val value:Long){
      QUICK(10L),
      NORMAL(500L)
   }

}