package dev.alexzvn.universalyogaplus.local

import android.annotation.SuppressLint
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import androidx.room.TypeConverters
import java.time.DayOfWeek as DOW
import dev.alexzvn.universalyogaplus.util.nanoid
import dev.alexzvn.universalyogaplus.util.pickRandom
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

enum class DayOfWeek(val origin: String) {
    MONDAY("Monday"),
    TUESDAY("Tuesday"),
    WEDNESDAY("Wednesday"),
    THURSDAY("Thursday"),
    FRIDAY("Friday"),
    SATURDAY("Saturday"),
    SUNDAY("Sunday");

    val native: DOW
        get() = when (this) {
            MONDAY ->  DOW.MONDAY
            TUESDAY -> DOW.TUESDAY
            WEDNESDAY -> DOW.WEDNESDAY
            THURSDAY -> DOW.THURSDAY
            FRIDAY -> DOW.FRIDAY
            SATURDAY -> DOW.SATURDAY
            SUNDAY -> DOW.SUNDAY
        }

    fun generateDatesBetween(start: LocalDate, end: LocalDate): MutableList<LocalDate> {
        val dates = mutableListOf<LocalDate>()
        var current = start.with(TemporalAdjusters.nextOrSame(native))

        while (current <= end) {
            if (current >= start) {
                dates.add(current)
            }
            current = current.plusWeeks(1)
        }

        return dates
    }

    companion object {
        fun pickRandom() = listOf(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY).pickRandom()!!
    }
}

enum class CourseType(val origin: String) {
    FLOW_YOGA("Flow Yoga"),
    AERIAL_YOGA("Aerial Yoga"),
    FAMILY_YOGA("Family Yoga");

    companion object {
        fun pickRandom() = listOf(FLOW_YOGA, FAMILY_YOGA, AERIAL_YOGA).pickRandom()!!
    }
}

@Entity
data class Course(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,

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

    @ColumnInfo(name = "start_time")
    val startTime: Int,

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
    val description: String? = null,

    @ColumnInfo(name = "nano_id")
    val nanoID: String = nanoid(),

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
) {
    val parsedStartTime
        @SuppressLint("DefaultLocale")
        get() = object {
            val hours: Int = startTime / 60
            val minutes: Int = startTime % 60

            override fun toString(): String {
                val a = hours.toString().padStart(2, '0')
                val b = minutes.toString().padStart(2, '0')

                return "$a:$b"
            }
        }
}

data class CourseWithSchedules(
    @Embedded val course: Course,

    @Relation(
        parentColumn = "id",
        entityColumn = "course_id"
    )
    val schedules: List<Schedule>
)