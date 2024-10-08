package dev.alexzvn.universalyogaplus.local

import androidx.compose.runtime.Composable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity
data class Schedule(
    @PrimaryKey(autoGenerate = true)
    val id: String,

    @ColumnInfo(name = "start_date")
    val date: String,

    /**
     * Start at nth minute of the day
     * eg: 30 -> 00:30, 210 -> 03:30
     */
    @ColumnInfo(name = "start_time")
    val startTime: Int,

    @Embedded
    val course: Course,

    @ColumnInfo(name = "teacher")
    val teacher: String
)
