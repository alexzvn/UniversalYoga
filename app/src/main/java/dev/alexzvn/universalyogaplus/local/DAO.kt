package dev.alexzvn.universalyogaplus.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

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