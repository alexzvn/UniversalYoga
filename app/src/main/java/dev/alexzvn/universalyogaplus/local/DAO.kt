package dev.alexzvn.universalyogaplus.local

import android.util.Log
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.room.Update
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery

data class CourseQuery(
    val term: String? = null,
    val dow: DayOfWeek? = null,
    val type: CourseType? = null,
    val teacher: String? = null,
    val startTime: Long? = null
) {
    val query: SimpleSQLiteQuery get() = run {
        val args = mutableListOf<String>()
        val query = StringBuilder("SELECT course.* FROM course")
        val conditions = mutableListOf<String>()

        if (term != null) {
            conditions.add("(title LIKE ? OR description LIKE ?)")
            args.add("%$term%")
            args.add("%$term%")
        }

        if (dow != null) {
            conditions.add("day_of_week = ?")
            args.add(dow.name)
        }

        if (type != null) {
            conditions.add("type = ?")
            args.add(type.name)
        }

        if (startTime != null) {
            conditions.add("start_time = ?")
            args.add(startTime.toString())
        }

        if (conditions.isNotEmpty()) {
            query.append(" WHERE ")
            query.append(conditions.joinToString(" AND "))
        }

        if (teacher != null) {
            // INNER JOIN
            query.append(" JOIN schedule ON course.id = schedule.course_id")
            query.append(" WHERE schedule.teacher LIKE ?")
            args.add("%$teacher%")
        }

        return query.append(";").toString().let {
            Log.d("CourseQuery", "Query: $it")
            SimpleSQLiteQuery(it, args.toTypedArray())
        }
    }
}

@Dao
interface CourseDAO {
    @Query("SELECT * FROM course ORDER BY id DESC")
    suspend fun all(): List<Course>

    @Query("SELECT * FROM course WHERE id IN (:scheduleIds)")
    suspend fun loadAllByIds(scheduleIds: IntArray): List<Course>

    @Query("SELECT * FROM course WHERE title LIKE :title LIMIT 1")
    suspend fun findByTitle(title: String): Course?

    @Query("SELECT * FROM course WHERE id = :id LIMIT 1")
    suspend fun get(id: Int): Course?

    @Update
    suspend fun update(course: Course)

    @Insert
    suspend fun insert(vararg courses: Course)

    @Delete
    suspend fun delete(course: Course)

    @Query("DELETE FROM course")
    suspend fun truncate()

    @RawQuery
    suspend fun query(query: SupportSQLiteQuery): List<Course>

    suspend fun query(courseQuery: CourseQuery): List<Course> {
        return query(courseQuery.query)
    }

    @Query("SELECT * FROM course WHERE title LIKE :query OR description LIKE :query OR type LIKE :query OR day_of_week LIKE :query")
    suspend fun search(query: String): List<Course>
}

@Dao
interface ScheduleDAO {
    @Query("SELECT * FROM schedule")
    suspend fun all(): List<Schedule>

    @Query("SELECT * FROM schedule WHERE id = :id LIMIT 1")
    suspend fun get(id: Long): Schedule?

    @Query("SELECT * FROM schedule WHERE course_id = :id")
    suspend fun getByCourse(id: Long): List<Schedule>

    @Update
    suspend fun update(schedule: Schedule)

    @Delete
    suspend fun delete(schedule: Schedule)

    @Insert
    suspend fun insert(vararg schedules: Schedule)

    @Query("DELETE FROM schedule")
    suspend fun truncate()
}