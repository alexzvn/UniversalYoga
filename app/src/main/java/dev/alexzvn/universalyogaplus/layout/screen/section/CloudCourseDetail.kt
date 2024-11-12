package dev.alexzvn.universalyogaplus.layout.screen.section

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import dev.alexzvn.universalyogaplus.component.ScheduleCard
import dev.alexzvn.universalyogaplus.local.Course
import dev.alexzvn.universalyogaplus.local.Order
import dev.alexzvn.universalyogaplus.local.Schedule
import dev.alexzvn.universalyogaplus.service.CloudService
import dev.alexzvn.universalyogaplus.service.DatabaseService
import dev.alexzvn.universalyogaplus.util.Route
import dev.alexzvn.universalyogaplus.util.observe
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


@Composable
fun CloudCourseDetail(
    nanoId: String,
    navigation: NavController = rememberNavController(),
) {
    var course by remember { mutableStateOf<Course?>(null) }
    var orders by remember { mutableStateOf(listOf<Order>()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            val document = CloudService.courses.document(nanoId)
            val snapshot = document.get().await()

            course = Course.tryFromDocument(snapshot)

            if (course == null) {
                navigation.popBackStack()
                return@launch
            }

            val query = CloudService.collection("orders")
                .whereEqualTo("course_nano_id", course!!.nanoID)

            query.observe().collect { snapshots ->
                Log.d("orders", "orders: $snapshots")
                orders = snapshots
                    .mapNotNull { Order.tryFromDocument(it) }
                    .distinctBy { it.id }
            }

            document.observe().collect {
                course = Course.tryFromDocument(it)
            }
        }
    }


    Column {
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = { navigation.popBackStack() },
                content = { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
            )
        }


        if (course == null) {
            return
        }

        Column (
            modifier = Modifier.padding(horizontal =  20.dp)
        ) {
            val course = course!!

            Text(course.title, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
            Text("$${course.price}", fontSize = 20.sp)
            Spacer(Modifier.height(10.dp))
            Text(
                text = "${course.type.origin} on ${course.dayOfWeek.origin}, ${course.duration} minutes",
                fontSize = 18.sp,
            )
            Text(
                modifier = Modifier.alpha(.5f),
                text = "Start time: ${course.parsedStartTime}"
            )

            Text(
                text = "Revenues: $${orders.sumOf { it.price }}"
            )

            Spacer(Modifier.height(5.dp))

            if (!course.description.isNullOrBlank()) {
                Text(
                    modifier = Modifier.padding(vertical = 10.dp),
                    text = course.description
                )
            }
        }

        var schedules by remember { mutableStateOf(listOf<Schedule>()) }

        LaunchedEffect(Unit) {
            scope.launch {
                schedules = CloudService.schedules.getByCourse(course!!.nanoID)
            }
        }

        LazyColumn (
            modifier = Modifier.weight(1f).padding(horizontal = 10.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            items(schedules) { schedule ->
                var showExtra by remember { mutableStateOf(false) }
                val booked = orders.filter { it.schedule_nano_id == schedule.nanoID }

                val footer = @Composable {
                    Column (
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                    ) {
                        Text("Attendees: ${booked.size}/${course!!.capacity}")

                        if (showExtra) Column {
                            booked.forEach {
                                Text("ref: ${it.user_id}")
                            }
                        }
                    }

                }

                ScheduleCard(
                    schedule = schedule,
                    showAction = false,
                    onClick = { showExtra = showExtra.not() },
                    footer = footer
                )
                Spacer(Modifier.height(10.dp))
            }

            item {
                Spacer(Modifier.height(100.dp))
            }
        }
    }
}