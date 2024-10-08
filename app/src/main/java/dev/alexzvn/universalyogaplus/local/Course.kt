package dev.alexzvn.universalyogaplus.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

enum class DayOfWeek(private val text: String) {
    MONDAY("Monday"),
    TUESDAY("Tuesday"),
    WEDNESDAY("Wednesday"),
    THURSDAY("Thursday"),
    FRIDAY("Friday"),
    SATURDAY("Saturday"),
    SUNDAY("Sunday");

    override fun toString(): String {
        return text
    }
}

enum class CourseType(private val text: String) {
    FLOW_YOGA("Flow Yoga"),
    AERIAL_YOGA("Aerial Yoga"),
    FAMILY_YOGA("Family Yoga"),
}

@Entity
data class Course(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(name = "title")
    val title: String,

    /**
     * day of the week
     * eg: monday, tuesday, etc..
     */
    @ColumnInfo(name = "day_of_week")
    @TypeConverters(DayOfWeek::class)
    val dayOfWeek: DayOfWeek,

    @ColumnInfo(name = "capacity")
    val capacity: Int,

    /**
     * Duration in minutes
     */
    @ColumnInfo(name = "duration")
    val duration: Int,

    @ColumnInfo(name = "price")
    val price: Double,

    @ColumnInfo(name = "type")
    val type: CourseType,

    @ColumnInfo(name = "description")
    val description: String? = null
)
