package dev.alexzvn.universalyogaplus.service

import dev.alexzvn.universalyogaplus.local.Database

object DatabaseService {
    private lateinit var db: Database

    fun set(database: Database) {
        db = database
    }

    val course get() = db.course()
    val schedule get() = db.schedule()
    suspend fun clear() = db.clear()
}