package dev.alexzvn.universalyogaplus.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Database as DB

@DB(entities = [Course::class, Schedule::class], version = 2)
abstract class Database : RoomDatabase() {
    companion object {
        private const val DATABASE_NAME = "db.sqlite"

        fun create(context: Context, name: String = DATABASE_NAME): Database {
            return Room.databaseBuilder(context, Database::class.java, "2$name").build()
        }
    }

    abstract fun course(): CourseDAO
    abstract fun schedule(): ScheduleDAO

    suspend fun clear() {
        schedule().truncate()
        course().truncate()
    }
}