package expo.modules.countdown.data

import androidx.room.Database
import androidx.room.RoomDatabase
import expo.modules.countdown.data.dao.FocusDailyDao
import expo.modules.countdown.data.dao.SettingDao
import expo.modules.countdown.data.entity.CountdownSetting
import expo.modules.countdown.data.entity.FocusDailyRecord

@Database(entities = [CountdownSetting::class, FocusDailyRecord::class] , version = 1)
abstract class SettingDatabase : RoomDatabase(){
    abstract fun settingDao(): SettingDao
    abstract fun recordDao(): FocusDailyDao
}