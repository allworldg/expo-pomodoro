package expo.modules.countdown.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class CountdownSetting(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "pomodoro") val pomodoro: Int,
    @ColumnInfo(name = "rest") val rest: Int,
    @ColumnInfo(name="cycles") val cycles: Int,
)
