package dev.alexzvn.universalyogaplus.layout.screen.section

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.alexzvn.universalyogaplus.component.CourseCard
import dev.alexzvn.universalyogaplus.component.SearchDialog
import dev.alexzvn.universalyogaplus.component.SearchDialogState
import dev.alexzvn.universalyogaplus.local.Course
import dev.alexzvn.universalyogaplus.local.CourseQuery
import dev.alexzvn.universalyogaplus.service.CloudService
import dev.alexzvn.universalyogaplus.service.DatabaseService
import dev.alexzvn.universalyogaplus.util.Route
import kotlinx.coroutines.launch


@Composable
fun CourseListSection(
    navigation: NavController = rememberNavController()
) {
    val scope = rememberCoroutineScope()
    var courses by remember { mutableStateOf(listOf<Course>()) }
    var showSearchDialog by remember { mutableStateOf(false) }
    var searchState by remember { mutableStateOf(SearchDialogState()) }

    LaunchedEffect(Unit) {
        scope.launch {
            courses = DatabaseService.course.all()
            Log.d("CourseListSection", "Loaded ${courses.size} courses")
        }
    }

    if (showSearchDialog) {
        SearchDialog(
            onDismiss = { showSearchDialog = false },
            state = searchState,
            onUpdate = { searchState = it },
            onSearch = {
                showSearchDialog = false

                scope.launch {
                    val search = searchState

                    courses = DatabaseService.course.query(
                        CourseQuery(
                            term = when (search.term.isBlank()) {
                                true -> null
                                else -> search.term.trim()
                            },
                            teacher = when (search.teacher.isBlank()) {
                                true -> null
                                else -> search.teacher.trim()
                            },
                            dow = search.dow,
                            type = search.type
                        )
                    ).distinctBy { it.id }
                }
            }
        )
    }

    Column () {
        Row (
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Course Overview",
                modifier = Modifier.padding(top = 10.dp),
                style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold)
            )

            IconButton(
                colors = IconButtonDefaults.outlinedIconButtonColors(),
                onClick = { showSearchDialog = true }
            ) {
                Icon(Icons.Default.Search, "search")
            }

            Button(
                onClick = { navigation.navigate(Route.Course.Create) },
                contentPadding = PaddingValues(horizontal =  20.dp)
            ) {
                Icon(Icons.Default.Add, "add")
                Spacer(Modifier.width(10.dp))
                Text("Add new")
            }
        }

        val delete = { course: Course ->
            courses = courses.filter { it.id != course.id }

            scope.launch {
                DatabaseService.course.delete(course)

                if (course.sync) {
                    CloudService.courses.removeWithSchedule(course)
                }
            }
        }

        val edit = { course: Course ->
            navigation.navigate(Route.Course.edit("${course.id}"))
        }

        if (courses.isEmpty()) {
            return Column (
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(.5f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Add a new course to start", fontSize = 20.sp)
            }.run {  }
        }

        LazyColumn (
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            Log.d("CourseListSection", "Rendering ${courses.size} courses")
            items(courses, key = { it.id!! }) {
                CourseCard(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(),
                    course = it,
                    onDelete = { delete(it) },
                    onEdit = { edit(it) },
                    onClick = {
                        navigation.navigate(Route.Course.view("${it.id}"))
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun CourseListSectionPreview() {
    CourseListSection()
}