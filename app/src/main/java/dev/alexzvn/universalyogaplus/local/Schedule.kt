package dev.alexzvn.universalyogaplus.local

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentSnapshot
import dev.alexzvn.universalyogaplus.util.nanoid

@Entity
data class Schedule(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "start_date")
    val date: Long,

    @ColumnInfo(name = "course_id")
    val courseId: Int,

    @ColumnInfo(name = "teacher")
    val teacher: String,

    @ColumnInfo(name = "comment")
    val comment: String? = null,

    @ColumnInfo(name = "nano_id", index = true)
    val nanoID: String = nanoid()
) {
    fun map(course: Course) = hashMapOf(
        "local_id" to id,
        "start_date" to date,
        "course_id" to courseId,
        "course_nano_id" to course.nanoID,
        "teacher" to teacher,
        "comment" to comment,
    )

    fun map(courseNanoId: String) = hashMapOf(
        "local_id" to id,
        "start_date" to date,
        "course_id" to courseId,
        "course_nano_id" to courseNanoId,
        "teacher" to teacher,
    )

    companion object {
        fun tryFromDocument(document: DocumentSnapshot): Schedule? {
            return try {
                document.data?.run {
                    val getLongAsInt = { key: String ->
                        (get(key) as Long).toInt()
                    }

                    Schedule(
                        id = getLongAsInt("local_id"),
                        date = get("start_date") as Long,
                        courseId = getLongAsInt("course_id"),
                        teacher = get("teacher") as String,
                        comment = get("comment") as String,
                    )
                }
            } catch (e: Exception) {
                return null
            }
        }
    }
}
