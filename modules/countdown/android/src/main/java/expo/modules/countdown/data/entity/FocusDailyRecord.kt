package expo.modules.countdown.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "focus_daily_record")
data class FocusDailyRecord(
    @PrimaryKey val date: String, // "2026-06-02"

    @ColumnInfo(name = "focus_count") val focusCount: Int,

    @ColumnInfo(name = "focus_duration") val focusDuration: Long //
)