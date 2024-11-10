package dev.alexzvn.universalyogaplus.service

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dev.alexzvn.universalyogaplus.local.Course
import dev.alexzvn.universalyogaplus.local.Database

object DatabaseService {
    private lateinit var db: Database

    val cloud: FirebaseFirestore
        get() = Firebase.firestore

    fun set(database: Database) {
        db = database
    }

    val course get() = db.course()
    val schedule get() = db.schedule()
    suspend fun clear() = db.clear()
}