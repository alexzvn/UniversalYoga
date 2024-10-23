package dev.alexzvn.universalyogaplus.layout.screen.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.LaunchedEffect
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseEditSection (
    id: Int? = null,
    navigation: NavController = rememberNavController()
) {
    if (id == null) {
        return navigation.popBackStack().run {}
    }

    val scope = rememberCoroutineScope()
    val signal = ValidateSignal()
    var course by remember { mutableStateOf<Course?>(null) }
    var state by remember { mutableStateOf(CourseData()) }
    val focus = object {
        val requester = remember { FocusRequester() }
        val manager = LocalFocusManager.current
    }

    LaunchedEffect(Unit) {
        scope.launch {
            course = DatabaseService.course.get(id)?.also {
                state = CourseData(
                    title = it.title,
                    capacity = it.capacity.toString(),
                    duration = it.duration.toString(),
                    startTime = it.parsedStartTime.toString(),
                    dayOfWeek = it.dayOfWeek,
                    price = it.price.toInt().toString(),
                    type = it.type,
                    description = it.description ?: ""
                )
            }
        }
    }

    var saving by remember { mutableStateOf(false) }
    val save = save@{
        if (saving || !signal.request()) {
            return@save
        }

        saving = true

        scope.launch {
            course = course!!.copy(
                title = state.title,
                capacity = state.capacity.tryToInt()!!,
                duration = state.duration.tryToInt()!!,
                startTime = state.parseTime()!!.toInt(),
                price = state.price.tryToInt()!!.toDouble(),
                dayOfWeek = state.dayOfWeek,
                type = state.type,
                description = state.description
            ).also {
                DatabaseService.course.update(it)
            }

            Scope.Main.launch {
                saving = false
                navigation.popBackStack()
            }
        }
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
                                startTime = "${it.hour}:${it.minute}",
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

    Column {
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navigation.popBackStack() },
                content = { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
            )
            Text("Edit Course #${course?.id}", fontSize = 24.sp)
            TextButton(
                onClick = save,
                content = { Text("Save") },
                enabled = !saving
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
fun CourseEditPreview() {
    MainLayout { padding ->
        Box(Modifier.padding(padding)) {
            CourseEditSection()
        }
    }
}