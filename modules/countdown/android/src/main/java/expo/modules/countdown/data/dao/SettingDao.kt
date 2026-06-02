package expo.modules.countdown.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import expo.modules.countdown.data.entity.CountdownSetting

@Dao
interface SettingDao {
    @Query("SELECT * FROM countdownsetting")
    fun getAll(): List<CountdownSetting>

    @Query("SELECT * FROM CountdownSetting WHERE id IN (:ids)")
    fun loadAllByIds(ids: IntArray): List<CountdownSetting>

    @Query("select * from CountdownSetting limit 1")
    fun getFirst(): CountdownSetting

    //    @Query("SELECT * FROM setting WHERE first_name LIKE :first AND " +
//            "last_name LIKE :last LIMIT 1")
//    fun findByName(first: String, last: String): User
//
    @Insert
    fun insertAll(vararg: CountdownSetting)

    @Update
    fun update(setting: CountdownSetting)

//
//    @Delete
//    fun delete(user: User)
}