package dev.alexzvn.universalyogaplus.layout.screen.section

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.alexzvn.universalyogaplus.R
import dev.alexzvn.universalyogaplus.component.CourseCard
import dev.alexzvn.universalyogaplus.local.Course
import dev.alexzvn.universalyogaplus.service.CloudService
import dev.alexzvn.universalyogaplus.service.DatabaseService
import dev.alexzvn.universalyogaplus.util.Route
import dev.alexzvn.universalyogaplus.util.asPainter
import kotlinx.coroutines.launch

data class SelectableCourse(
    val selected: Boolean = false,
    val course: Course
)

@Composable
fun SelectCourseSyncDialog(
    onDismiss: () -> Unit = {},
    onSave: (List<SelectableCourse>) -> Unit = {}
) {
    var modeSelected by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    var courses by remember { mutableStateOf(listOf<SelectableCourse>()) }

    LaunchedEffect(Unit) {
        scope.launch {
            courses = DatabaseService.course.all().map {
                SelectableCourse(selected = it.sync, course = it)
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box (
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
        ) {
            Column {
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row (verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onDismiss
                        ) { Icon(Icons.Default.Close, "close") }

                        Text(text = "Select courses to sync", style = TextStyle(fontSize = 18.sp))
                    }

                    TextButton (onClick = { onSave(courses) }) { Text("Save") }
                }

                val checkAll = {
                    modeSelected = modeSelected.not()
                    courses = courses.map { it.copy(selected = modeSelected) }.toMutableList()
                }

                Row {
                    val allOn = courses.all { it.selected }
                    val allOff = courses.none { it.selected }

                    TriStateCheckbox(
                        onClick = checkAll,
                        state = when (!allOn && !allOff) {
                            true -> ToggleableState.Indeterminate
                            false -> when (allOn) {
                                true -> ToggleableState.On
                                false -> ToggleableState.Off
                            }
                        }
                    )
                    TextButton(
                        onClick = checkAll,
                        content = { Text("Select All") }
                    )
                }

                LazyColumn {
                    val update = { course: SelectableCourse, modifier: (SelectableCourse) -> SelectableCourse ->
                        val index = courses.indexOf(course)

                        if (index != -1) {
                            courses = courses.toMutableList().apply {
                                set(index, modifier(course))
                            }
                        }
                    }

                    items(courses) { course ->
                        ListItem(
                            modifier = Modifier.clickable {
                                update(course) { it.copy(selected = it.selected.not()) }
                            },
                            headlineContent = { Text(course.course.title) },
                            leadingContent = {
                                Checkbox(
                                    checked = course.selected,
                                    onCheckedChange = {
                                        update(course) { it.copy(selected = it.selected.not()) }
                                    }
                                )
                            }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }

    }
}

@Composable
fun CloudMainView(
    navigation: NavController = rememberNavController()
) {
    val scope = rememberCoroutineScope()

    var syncing by remember { mutableStateOf(false) }
    var showSyncSelectionDialog by remember { mutableStateOf(false) }
    var courses by remember { mutableStateOf(listOf<Course>())}

    LaunchedEffect(Unit) {
        scope.launch {
            CloudService.courses.all.collect {
                courses = it
            }
        }
    }

    if (showSyncSelectionDialog) {
        SelectCourseSyncDialog(
            onDismiss = { showSyncSelectionDialog = false },
            onSave = { courses ->
                showSyncSelectionDialog = false
                syncing = true

                scope.launch {
                    courses.forEach {
                        val course = it.course.copy(sync = it.selected)

                        if (course.sync) {
                            CloudService.courses.syncWithSchedule(course)
                        }

                        DatabaseService.course.update(course)
                    }

                    syncing = false
                }
            }
        )
    }

    Column {
        Row (
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Cloud Data",
                modifier = Modifier.padding(top = 10.dp),
                style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold)
            )

            Button(
                onClick = { showSyncSelectionDialog = true },
                contentPadding = PaddingValues(horizontal =  20.dp),
                enabled = syncing.not()
            ) {
                when (syncing) {
                    true -> {
                        val infinite = rememberInfiniteTransition(label = "spinner")
                        val angle by infinite.animateFloat(
                            label = "spin",
                            initialValue =  360f,
                            targetValue = 0f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(2000, easing = LinearEasing)
                            )
                        )

                        Icon(
                            modifier = Modifier.rotate(angle),
                            painter = R.drawable.baseline_sync_24.asPainter(),
                            contentDescription = "syncing"
                        )
                    }
                    false -> {
                        Icon(R.drawable.baseline_cloud_sync_24.asPainter(), "add")
                    }
                }


                Spacer(Modifier.width(10.dp))
                Text("Sync Courses")
            }
        }

        LazyColumn (
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            items(courses) {
                CourseCard(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(),
                    course = it,
                    onClick = { navigation.navigate(Route.cloud.view(it.nanoID)) },
                    onDelete = {
                        scope.launch {
                            CloudService.courses.removeWithSchedule(it)
                        }
                    }
                )
            }

            item {
                if (courses.isEmpty()) {
                    Spacer(Modifier.height(100.dp))
                    Column (
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Not thing found on cloud.")
                    }
                }
            }

            item {
                Spacer(Modifier.height(200.dp))
            }
        }
    }
}