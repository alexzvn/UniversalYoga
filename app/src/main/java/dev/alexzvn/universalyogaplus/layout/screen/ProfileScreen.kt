package dev.alexzvn.universalyogaplus.layout.screen

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.github.javafaker.Faker
import com.google.firebase.auth.UserInfo
import com.google.firebase.firestore.FirebaseFirestore
import dev.alexzvn.universalyogaplus.layout.MainLayout
import dev.alexzvn.universalyogaplus.layout.NavigateSection
import dev.alexzvn.universalyogaplus.local.Course
import dev.alexzvn.universalyogaplus.local.CourseType
import dev.alexzvn.universalyogaplus.local.DayOfWeek
import dev.alexzvn.universalyogaplus.local.Schedule
import dev.alexzvn.universalyogaplus.service.AuthService
import dev.alexzvn.universalyogaplus.service.CloudService
import dev.alexzvn.universalyogaplus.service.DatabaseService
import dev.alexzvn.universalyogaplus.util.popRandom
import dev.alexzvn.universalyogaplus.util.toEpochMilli
import dev.alexzvn.universalyogaplus.util.toLocal
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date


object DemoApp {
    private val faker by lazy { Faker() }

    suspend fun import() {
        val items = (1 .. 10).map { makeCourse() }.toTypedArray().run {
            DatabaseService.course.insert(*this)
            DatabaseService.course.all()
        }

        for (item in items) {
            val start = Date().toLocal().minusMonths(3)
            val end = Date().toLocal().plusMonths(3)
            val dates = item.dayOfWeek.generateDatesBetween(start, end)
            val schedules = mutableListOf<Schedule>()

            for (i in 1..10) {
                val schedule = Schedule(
                    id = 0,
                    date = dates.popRandom()!!.toEpochMilli(),
                    courseId = item.id!!,
                    teacher = faker.name().fullName(),
                    comment = faker.lorem().sentence(faker.number().numberBetween(4, 16))
                )

                Log.d("DemoApp", "Schedule: $schedule")

                schedules.add(schedule)
            }

            DatabaseService.schedule.insert(*schedules.toTypedArray())
        }
    }

    suspend fun reset() {
        DatabaseService.clear()
    }

    private fun makeCourse(): Course {
        val hour = faker.number().numberBetween(8, 16) * 60
        val minute = when (faker.number().numberBetween(0, 1) ) {
            1 -> 30
            else -> 0
        }

        val type = CourseType.pickRandom()
        val dow = DayOfWeek.pickRandom()
        val unique = faker.nation().capitalCity()
        
        return Course(
            id = null,
            title = "${type.origin} in $unique",
            description = faker.lorem().sentence(24),
            capacity = faker.number().numberBetween(10, 100),
            duration = faker.number().numberBetween(30, 120),
            dayOfWeek = dow,
            price = faker.number().numberBetween(10, 200).toDouble(),
            startTime = hour + minute,
            type = type,
            createdAt = Date().time,
        )
    }

    suspend fun clearFirebase() {
        val courses = CloudService.courses.collection.get().await()
        val schedules = CloudService.schedules.collection.get().await()

        for (doc in courses.documents) {
            doc.reference.delete()
        }

        for (doc in schedules.documents) {
            doc.reference.delete()
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileScreen(
    navigation: NavController,
    user: UserInfo? = null
) {
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }
    val user = user ?: AuthService.user

    val name = user?.displayName ?: user?.email?.run {
        when (val index = indexOf('@')) {
            -1 -> null
            lastIndex -> "A"
            else -> substring(0, index)
        }
    }

    val avatar = name?.let {
        when (it.length) {
            0 -> "A"
            else -> it[0].uppercase()
        }
    }

    MainLayout (
        section = NavigateSection.Profile,
        onNavigate = { NavigateSection.handle(it, navigation) },
    ) { padding ->
        Scaffold (
            modifier = Modifier.padding(padding),
            snackbarHost = { SnackbarHost(snackbar) }
        ) {
            Column (
                modifier = Modifier
            ) {
                Row (
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Your Profile", fontSize = 24.sp, fontWeight = FontWeight.W500)

                    TextButton(onClick = { AuthService.logout() }) {
                        Text("Sign Out")
                        Spacer(Modifier.width(5.dp))
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, "Exit")
                    }
                }

                Box (
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(20.dp)
                        .size(150.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color(0xff_ccc4cf), CircleShape)
                        .background(Color(0xff_f7f7f7))
                        .align(Alignment.CenterHorizontally)
                        .alpha(.6f),
                ) {
                    Text("$avatar", fontSize = 72.sp, fontWeight = FontWeight.Light)
                }

                Column (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Hello, $name", fontSize = 16.sp, fontWeight = FontWeight.W500)

                    if (!user?.email.isNullOrEmpty()) {
                        Text(user?.email!!, modifier = Modifier.alpha(.6f))
                    }
                }


                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 30.dp)
                ) {
                    FilledTonalButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            scope.launch {
                                DemoApp.import()
                                snackbar.showSnackbar("Import demo local data Successfully")
                            }
                        }
                    ) {
                        Text("Import Demo Local Data")
                    }

                    FilledTonalButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            scope.launch {
                                DemoApp.reset()
                                snackbar.showSnackbar("Cleared local database")
                            }
                        }
                    ) {
                        Text("Reset Local Database")
                    }

                    FilledTonalButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            scope.launch {
                                DemoApp.clearFirebase()
                                snackbar.showSnackbar("Cleared Firebase")
                            }
                        }
                    ) {
                        Text("Reset Firebase Data")
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ProfileScreenPreview() {
    val user = object : UserInfo {
        override fun getPhotoUrl(): Uri? {
            return null
        }

        override fun getDisplayName(): String? {
            return null
        }

        override fun getEmail(): String {
            return "someone@gmail.com"
        }

        override fun getPhoneNumber(): String? {
            return null
        }

        override fun getProviderId(): String {
            return "email"
        }

        override fun getUid(): String {
            return "0123456"
        }

        override fun isEmailVerified(): Boolean {
            return true
        }

    }

    ProfileScreen(rememberNavController(), user)
}