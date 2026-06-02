package expo.modules.countdown.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import expo.modules.countdown.data.entity.FocusDailyRecord

@Dao
interface FocusDailyDao {
    @Query("SELECT * FROM FocusDailyRecord")
    fun getAll(): List<FocusDailyRecord>

    @Query("SELECT * FROM FocusDailyRecord WHERE date = :date LIMIT 1")
    fun search(date: String): FocusDailyRecord?

    @Insert
    fun insert(record: FocusDailyRecord)
    @Insert
    fun insertAll(vararg: FocusDailyRecord)

    @Update
    fun update(record: FocusDailyRecord)

    @Query("DELETE FROM FocusDailyRecord WHERE date = :date")
    fun del(date: String)
}
