package dev.alexzvn.universalyogaplus.local

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Schedule(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(name = "start_date")
    val date: Long,

    @ColumnInfo(name = "course_id")
    val courseId: Int,

    @ColumnInfo(name = "teacher")
    val teacher: String,

    @ColumnInfo(name = "comment")
    val comment: String? = null
)
