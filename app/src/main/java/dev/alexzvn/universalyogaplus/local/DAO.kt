package dev.alexzvn.universalyogaplus.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CourseDAO {
    @Query("SELECT * FROM course")
    fun all(): List<Course>

    @Query("SELECT * FROM course WHERE id IN (:scheduleIds)")
    fun loadAllByIds(scheduleIds: IntArray): List<Course>

    @Query("SELECT * FROM course WHERE title LIKE :title LIMIT 1")
    fun findByTitle(title: String): Course

    @Update
    fun update(course: Course)

    @Insert
    fun insert(vararg courses: Course)

    @Delete
    fun delete(course: Course)

    @Query("DELETE FROM course")
    fun truncate()

    @Query("SELECT * FROM course WHERE title LIKE :query OR description LIKE :query OR type LIKE :query OR day_of_week LIKE :query")
    fun search(query: String): List<Course>
}

@Dao
interface ScheduleDAO {
    @Query("SELECT * FROM schedule")
    fun all(): List<Schedule>

    @Query("SELECT * FROM schedule WHERE id = :id LIMIT 1")
    fun get(id: Long): Schedule?

    @Update
    fun update(schedule: Schedule)

    @Delete
    fun delete(schedule: Schedule)

    @Insert
    fun insert(vararg schedules: Schedule)

    @Query("DELETE FROM schedule")
    fun truncate()
}