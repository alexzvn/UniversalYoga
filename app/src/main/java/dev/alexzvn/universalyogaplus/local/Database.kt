package dev.alexzvn.universalyogaplus.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Database as DB

@DB(entities = [Course::class, Schedule::class], version = 1)
abstract class Database : RoomDatabase() {
    companion object {
        const val DATABASE_NAME = "db.sqlite"

        fun create(context: Context): Database {
            return Room.databaseBuilder(context, Database::class.java, DATABASE_NAME).build()
        }
    }

    abstract fun course(): CourseDAO
    abstract fun schedule(): ScheduleDAO
}