package dev.alexzvn.universalyogaplus.local

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot

data class Order(
    val id: String? = null,
    val course_nano_id: String,
    val schedule_nano_id: String,
    val user_id: String,
    val price: Double,
    val created_at: Long
) {

    companion object {
        fun tryFromDocument(document: DocumentSnapshot): Order? {
            return try {
                document.data?.run {
                    Order(
                        id = document.id,
                        course_nano_id = get("course_nano_id") as String,
                        schedule_nano_id = get("schedule_nano_id") as String,
                        user_id = get("user_id") as String,
                        price = (get("price") as Long).toDouble(),
                        created_at = get("created_at") as Long
                    )
                }
            } catch(error: Exception) {
                Log.d("error", "error: $error")

                return null
            }
        }
    }
}
