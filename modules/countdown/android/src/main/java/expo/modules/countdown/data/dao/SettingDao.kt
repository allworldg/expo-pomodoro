package expo.modules.countdown.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import expo.modules.countdown.data.entity.CountdownSetting

@Dao
interface SettingDao {
    @Query("SELECT * FROM countdown_setting")
    fun getAll(): List<CountdownSetting>

    @Query("SELECT * FROM countdown_setting WHERE id IN (:ids)")
    fun loadAllByIds(ids: IntArray): List<CountdownSetting>

    @Query("select * from countdown_setting limit 1")
    fun getFirst(): CountdownSetting

    @Insert
    fun insertAll(vararg: CountdownSetting)

    @Update
    fun update(setting: CountdownSetting)

}