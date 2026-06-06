package expo.modules.countdown.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import expo.modules.countdown.data.entity.FocusDailyRecord

@Dao
interface FocusDailyDao {
    @Query("SELECT * FROM focus_daily_record")
    fun getAll(): List<FocusDailyRecord>

    @Query("SELECT * FROM focus_daily_record WHERE date = :date LIMIT 1")
    fun search(date: String): FocusDailyRecord?

    @Insert
    fun insert(record: FocusDailyRecord)

    @Insert
    fun insertAll(vararg: FocusDailyRecord)

    @Update
    fun update(record: FocusDailyRecord)

    @Query("select coalesce(sum(focus_duration),0) from focus_daily_record")
    fun getTotalFocusDuration(): Long

    @Query("DELETE FROM focus_daily_record WHERE date = :date")
    fun del(date: String)

}
