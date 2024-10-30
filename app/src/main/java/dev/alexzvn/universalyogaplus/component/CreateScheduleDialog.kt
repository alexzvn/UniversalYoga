package dev.alexzvn.universalyogaplus.component

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import dev.alexzvn.universalyogaplus.local.Course
import dev.alexzvn.universalyogaplus.local.CourseType
import dev.alexzvn.universalyogaplus.local.CourseWithSchedules
import dev.alexzvn.universalyogaplus.local.DayOfWeek
import dev.alexzvn.universalyogaplus.local.Schedule
import dev.alexzvn.universalyogaplus.util.format
import dev.alexzvn.universalyogaplus.util.toLocal
import java.time.format.DateTimeFormatter
import java.util.Date


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateScheduleDialog(
    course: CourseWithSchedules,
    onDismiss: () -> Unit = {},
    onSave: (Schedule) -> Unit = {}
) {
    val selectableDate = ScheduleSelectableDates(
        dayOfWeek = course.course.dayOfWeek,
        ignoreDate = course.schedules.map { Date(it.date) }
    )
    val signal = ValidateSignal()

    var showDatePicker by remember { mutableStateOf(false) }
    var teacher by remember { mutableStateOf("") }
    var comment by remember { mutableStateOf("") }
    val startDate = rememberDatePickerState(
        selectableDates = selectableDate
    )

    val save = save@{
        if (! signal.request()) {
            return@save
        }

        val schedule = Schedule(
            id = 0,
            date = startDate.selectedDateMillis!!,
            courseId = course.course.id ?: 0,
            teacher = teacher,
            comment = comment
        )

        onSave(schedule)
    }

    Dialog(
        properties = DialogProperties(
            dismissOnBackPress = true,
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        ),
        onDismissRequest = onDismiss
    ) {
        Box (
            modifier = Modifier.fillMaxSize().background(Color.White),
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

                        Text(text = "Add new Schedule", style = TextStyle(fontSize = 18.sp))
                    }

                    TextButton (onClick = save) { Text("Save") }
                }

                Column (
                    modifier = Modifier.padding(horizontal = 10.dp)
                ) {
                    ValidatedInput(
                        label = { Text("Teacher") },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter Teacher name") },
                        signal = signal,
                        value = teacher,
                        onChange = { teacher = it },
                        validate = { teacher.isNotBlank() },
                        error = { Text("Please enter teacher name", color = Color.Red) }
                    )

                    ValidatedInput(
                        label = { Text("Start Date") },
                        placeholder = { Text("Pick a start date") },
                        readOnly = true,
                        signal = signal,
                        value = when (startDate.selectedDateMillis) {
                            null -> ""
                            else -> Date(startDate.selectedDateMillis!!).toLocal().format("dd/MM/yyyy")
                        },
                        validate = { selectableDate.isSelectableDate(startDate.selectedDateMillis ?: 0) },
                        error = { Text("Please select valid date", color = Color.Red) },
                        trailingIcon = {
                            IconButton(
                                onClick = { showDatePicker = true }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Select date"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                            .pointerInput(startDate) {
                                awaitEachGesture {
                                    awaitFirstDown(pass = PointerEventPass.Initial)
                                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)

                                    if (upEvent != null) {
                                        showDatePicker = true
                                    }
                                }
                            },
                    )

                    OutlinedTextField(
                        value = comment,
                        onValueChange = { comment = it },
                        label = { Text("Comment (optional)") },
                        placeholder = { Text("Enter comment") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        singleLine = false
                    )
                }
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = { showDatePicker = false },
                    content = { Text("Confirm") }
                )
            }
        ) {
            DatePicker(
                modifier = Modifier.fillMaxWidth(),
                state = startDate
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun CreateScheduleDialogPreview() {
    val course = CourseWithSchedules(
        course = Course(
            id = 1,
            title = "Yoga",
            dayOfWeek = DayOfWeek.FRIDAY,
            capacity = 20,
            startTime = 20,
            duration = 20,
            type = CourseType.FLOW_YOGA,
            price = 30.0,
            description = ""
        ),
        schedules = listOf(

        )
    )

    CreateScheduleDialog(course)
}