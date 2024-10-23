package dev.alexzvn.universalyogaplus.layout.screen.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.alexzvn.universalyogaplus.component.OptionSelection
import dev.alexzvn.universalyogaplus.component.TimePicker
import dev.alexzvn.universalyogaplus.component.ValidateSignal
import dev.alexzvn.universalyogaplus.component.ValidatedInput
import dev.alexzvn.universalyogaplus.layout.MainLayout
import dev.alexzvn.universalyogaplus.local.Course
import dev.alexzvn.universalyogaplus.local.CourseType
import dev.alexzvn.universalyogaplus.local.DayOfWeek
import dev.alexzvn.universalyogaplus.service.DatabaseService
import dev.alexzvn.universalyogaplus.util.Scope
import dev.alexzvn.universalyogaplus.util.tryToInt
import kotlinx.coroutines.launch

data class CourseData (
    val title: String = "",
    val startTime: String = "",
    val dayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    val capacity: String = "",
    val duration: String = "",
    val price: String = "",
    val type: CourseType = CourseType.FLOW_YOGA,
    val description: String = "",
    val selectingTime: Boolean = false
) {
    private fun formatInt(value: String): String? {
        if (value.isBlank()) {
            return ""
        }

        val int = value.tryToInt() ?: return null

        if (int == 0) {
            return  ""
        }

        return int.toString()
    }

    fun cloneCapacity(value: String): CourseData {
        return this.copy(
            capacity = formatInt(value) ?: capacity
        )
    }

    fun clonePrice(value: String): CourseData {
        return this.copy(
            price = formatInt(value) ?: price
        )
    }

    fun cloneDuration(value: String): CourseData {
        return this.copy(
            duration = formatInt(value) ?: duration
        )
    }

    fun parseTime(): StartTime? {
        if (startTime.isBlank()) {
            return null
        }

        startTime.split(':').apply {
            return StartTime(get(0).tryToInt(), get(1).tryToInt())
        }
    }

    data class StartTime(val hours: Int?, val minutes: Int?) {
        fun toInt(): Int {
            val a = (hours ?: 0) * 60
            val b = minutes ?: 0

            return a + b
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseCreateSection (
    navigation: NavController = rememberNavController()
) {
    val signal = ValidateSignal()
    var state by remember { mutableStateOf(CourseData()) }

    val focus = object {
        val requester = remember { FocusRequester() }
        val manager = LocalFocusManager.current
    }

    if (state.selectingTime) {
        val init = state.parseTime()

        Dialog(onDismissRequest = { state = state.copy(selectingTime = false) }) {
            Card (
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box (
                    modifier = Modifier.padding(16.dp)
                ) {
                    TimePicker(
                        initialMinute = init?.minutes,
                        initialHour = init?.hours,
                        onConfirm = {
                            focus.manager.clearFocus()
                            state = state.copy(
                                startTime = it.hour.toString() + ":" + it.minute.toString(),
                                selectingTime = false,
                            )
                        },
                        onDismiss = {
                            focus.manager.clearFocus()
                            state = state.copy(selectingTime = false)
                        }
                    )
                }
            }
        }
    }

    var creating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val create = create@{
        if (creating || !signal.request()) {
            return@create
        }

        creating = true

        scope.launch {
            val course = Course(
                id = null,
                title = state.title,
                capacity = state.capacity.tryToInt()!!,
                duration = state.duration.tryToInt()!!,
                startTime = state.parseTime()!!.toInt(),
                price = state.price.tryToInt()!!.toDouble(),
                dayOfWeek = state.dayOfWeek,
                type = state.type,
                description = state.description
            )

            DatabaseService.course.insert(course)

            Scope.Main.launch {
                creating = false
                navigation.popBackStack()
            }
        }
    }

    
    Column (
        modifier = Modifier.fillMaxSize()
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navigation.popBackStack() },
                content = { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
            )
            Text("Create new Course", fontSize = 24.sp)
            TextButton(
                onClick = create,
                content = { Text("Save") },
                enabled = !creating
            )
        }

        Column (
            modifier = Modifier.padding(10.dp)
        ) {
            ValidatedInput(
                modifier = Modifier.fillMaxWidth(),
                value = state.title,
                onChange = { state = state.copy(title = it) },
                label = { Text("Course Title") },
                placeholder = { Text("Enter your course title") },
                validate = { it.isNotBlank() },
                error = { Text("Course title is required", color = Color.Red) },
                signal = signal,
                singleLine = true
            )

            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ValidatedInput(
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focus.requester)
                        .onFocusChanged {
                            state = state.copy(selectingTime = it.isFocused)
                        },
                    label = { Text("Start Time") },
                    value = state.startTime,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    validate = { it.isNotBlank() },
                    error = { Text("Start time is required", color = Color.Red) },
                    placeholder = { Text("Enter start time") },
                    signal = signal
                )

                Spacer(Modifier.width(10.dp))

                OptionSelection(
                    modifier = Modifier.weight(1f),
                    label = { Text("Day of week") },
                    options = DayOfWeek.entries,
                    selected = state.dayOfWeek,
                    onOptionSelected = { state = state.copy(dayOfWeek = it) },
                    format = { it.origin }
                )
            }

            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ValidatedInput(
                    modifier = Modifier.weight(1f),
                    label = { Text("Price") },
                    placeholder = { Text("Enter a price") },
                    value = state.price,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    onChange = { state = state.clonePrice(it) },
                    validate = { it.matches(Regex("^[0-9]+$")) },
                    error = { Text(text = "Price must be a number", color = Color.Red) },
                    signal = signal
                )

                Spacer(Modifier.width(10.dp))

                ValidatedInput(
                    modifier = Modifier.weight(1f),
                    label = { Text("Capacity") },
                    placeholder = { Text("Max people") },
                    value = state.capacity,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    onChange = { state = state.cloneCapacity(it) },
                    validate = { it.matches(Regex("^[0-9]+$")) },
                    error = { Text(text = "Capacity must be a number", color = Color.Red) },
                    signal = signal
                )
            }

           Row {
               OptionSelection(
                   modifier = Modifier.weight(1f),
                   label = { Text("Type of class") },
                   selected = state.type,
                   options = CourseType.entries,
                   format = { it.origin },
                   onOptionSelected = { state = state.copy(type = it) }
               )

               Spacer(Modifier.width(10.dp))

               ValidatedInput(
                   modifier = Modifier.weight(1f),
                   label = { Text("Duration") },
                   placeholder = { Text("number of minutes") },
                   value = state.duration,
                   keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                   singleLine = true,
                   onChange = { state = state.cloneDuration(it) },
                   validate = { it.matches(Regex("^[0-9]+$")) },
                   error = { Text(text = "Duration must be a number", color = Color.Red) },
                   signal = signal
               )
           }

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.description,
                label = { Text("Description (optional)") },
                onValueChange = { state = state.copy(description = it) },
                minLines = 3,
            )
        }
    }
}


@Composable
@Preview(showBackground = true)
fun CourseCreatePreview() {
    MainLayout { padding ->
        Box(Modifier.padding(padding)) {
            CourseCreateSection()
        }
    }
}