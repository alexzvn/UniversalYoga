package dev.alexzvn.universalyogaplus.layout.screen.section

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.alexzvn.universalyogaplus.component.CreateScheduleDialog
import dev.alexzvn.universalyogaplus.component.ScheduleCard
import dev.alexzvn.universalyogaplus.local.Course
import dev.alexzvn.universalyogaplus.local.CourseType
import dev.alexzvn.universalyogaplus.local.CourseWithSchedules
import dev.alexzvn.universalyogaplus.local.DayOfWeek
import dev.alexzvn.universalyogaplus.local.Schedule
import dev.alexzvn.universalyogaplus.service.CloudService
import dev.alexzvn.universalyogaplus.service.DatabaseService
import dev.alexzvn.universalyogaplus.util.Route
import kotlinx.coroutines.launch
import java.util.Date

@Composable
private fun CourseView(
    courseWithSchedules: CourseWithSchedules,
    navigation: NavController,
) {
    val scope = rememberCoroutineScope()
    val course = courseWithSchedules.course
    var schedules by remember { mutableStateOf(courseWithSchedules.schedules) }
    var showAddSchedule by remember { mutableStateOf(false) }

    val addSchedule = {schedule: Schedule ->
        schedules = schedules + schedule

        showAddSchedule = false

        scope.launch {
            DatabaseService.schedule.insert(schedule)
        }
    }

    Column (
        modifier = Modifier.fillMaxSize()
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = { navigation.popBackStack() },
                content = { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
            )

            TextButton(
                onClick = { navigation.navigate(Route.Course.edit("${course.id}")) },
                content = { Text("Edit") }
            )
        }

        Column (
            modifier = Modifier.padding(horizontal =  20.dp)
        ) {
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

            Spacer(Modifier.height(5.dp))

            if (!course.description.isNullOrBlank()) {
                Text(
                    modifier = Modifier.padding(vertical = 10.dp),
                    text = course.description
                )
            }
        }

        LazyColumn (
            modifier = Modifier.padding(horizontal = 10.dp)
        ) {

            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { showAddSchedule = true }
                ) {
                    Column (
                        modifier = Modifier
                            .padding(horizontal = 10.dp, vertical = 15.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row (verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, "add")
                            Text(text = "Add new Schedule", fontSize = 16.sp)
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))
            }

            items(schedules.sortedBy { it.date }) { schedule ->
                Spacer(Modifier.height(10.dp))
                ScheduleCard(
                    schedule = schedule,
                    onDelete = {
                        scope.launch {
                            DatabaseService.schedule.delete(schedule)

                            if (course.sync) {
                                CloudService.schedules.remove(schedule)
                            }
                        }

                        schedules = schedules.filter { it.id != schedule.id }
                    }
                )
            }
            
            item { Spacer(Modifier.height(200.dp)) }
        }
    }

    if (showAddSchedule) {
        CreateScheduleDialog(
            courseWithSchedules,
            onDismiss = { showAddSchedule = false },
            onSave = { addSchedule(it) }
        )
    }
}

@Composable
fun CourseViewSection(
    id: Int? = null,
    navigation: NavController = rememberNavController()
) {
    if (id == null) {
        return navigation.popBackStack().run {}
    }

    val scope = rememberCoroutineScope()
    var state by remember { mutableStateOf<CourseWithSchedules?>(null) }

    LaunchedEffect(Unit) {
        scope.launch {
            val course = DatabaseService.course.get(id)
            val schedules = DatabaseService.schedule.getByCourse(id.toLong())

            state = CourseWithSchedules(course!!, schedules)
        }
    }

    state?.also {
        CourseView(it, navigation)
    }
}


@Composable
@Preview(showBackground = true, showSystemUi = true)
fun CourseViewSectionPreview() {
    val course = Course(
        id = 1,
        title = "My first course",
        description = "helas dasndiuas duiajksdn aiudkjasbdigw jfb sjdc iuewhfk bkf w",
        capacity = 20,
        duration = 60,
        dayOfWeek = DayOfWeek.MONDAY,
        price = 100.0,
        startTime = 1200,
        type = CourseType.AERIAL_YOGA,
        createdAt = Date().time
    )

    CourseView(CourseWithSchedules(course, listOf()), rememberNavController())
}