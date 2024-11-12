package dev.alexzvn.universalyogaplus.service

import android.util.Log
import com.google.firebase.firestore.DocumentReference
import dev.alexzvn.universalyogaplus.local.Course
import dev.alexzvn.universalyogaplus.local.CourseType
import dev.alexzvn.universalyogaplus.local.DayOfWeek
import dev.alexzvn.universalyogaplus.local.Schedule
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await


object CloudService {
    val db get() = DatabaseService.cloud

    fun collection(name: String) = db.collection(name)

    object courses {
        val collection get() = db.collection("courses")

        val all: Flow<List<Course>>
            get() = callbackFlow {
            val listener = collection.addSnapshotListener { snapshots, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                val list = snapshots!!.documents.map {
                    Course.tryFromDocument(it)
                }

                this.trySend(list.filterNotNull())
            }

            awaitClose { listener.remove() }
        }

        suspend fun sync(vararg courses: Course) {
            for (item in courses) {
                collection.document(item.nanoID).set(item.map).await()
            }
        }

        suspend fun syncWithSchedule(vararg courses: Course) {
            for (item in courses) {
                collection.document(item.nanoID).set(item.map).await()

                for (schedule in DatabaseService.schedule.getByCourse(item.id!!.toLong())) {
                    schedules.document(schedule.nanoID).set(schedule.map(item)).await()
                }
            }
        }

        suspend fun remove(vararg course: Course) {
            for (item in course) {
                schedules.collection.document(item.nanoID).delete().await()
            }
        }

        suspend fun removeWithSchedule(vararg course: Course) {
            for (item in course) {
                collection.document(item.nanoID).delete().await()

                schedules.collection.whereEqualTo("course_nano_id", item.nanoID).get().await().forEach {
                    schedules.collection.document(it.id).delete().await()
                }
            }
        }

        fun document(id: String): DocumentReference {
            return collection.document(id)
        }

        suspend fun all(): List<Course> {
            val list = try {
                collection.get().await()
            } catch (e: Exception) {
                listOf()
            }

            return list.map {
                it.toObject(Course::class.java).copy(nanoID = it.id)
            }
        }
    }

    object schedules {
        val collection get() = db.collection("schedules")

        suspend fun remove(vararg schedules: Schedule) {
            for (item in schedules) {
                collection.document(item.nanoID).delete().await()
            }
        }

        fun document(id: String): DocumentReference {
            return collection.document(id)
        }

        suspend fun getByCourse(nanoID: String): List<Schedule> {
            val list = try {
                collection.whereEqualTo("course_nano_id", nanoID).get().await()
            } catch (e: Exception) {
                listOf()
            }

            return list.mapNotNull { Schedule.tryFromDocument(it) }
        }

        suspend fun all(): List<Course> {
            val list = try {
                courses.collection.get().await()
            } catch (e: Exception) {
                listOf()
            }

            return list.map {
                it.toObject(Course::class.java).copy(nanoID = it.id)
            }
        }
    }
}