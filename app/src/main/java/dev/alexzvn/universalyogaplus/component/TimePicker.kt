package dev.alexzvn.universalyogaplus.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker as MTimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePicker(
    onConfirm: (TimePickerState) -> Unit = {},
    onDismiss: () -> Unit = {},
    initialHour: Int? = null,
    initialMinute: Int? = null,
    is24Hour: Boolean = false
) {
    val currentTime = Calendar.getInstance()

    val timePickerState = rememberTimePickerState(
        initialHour = initialHour ?: currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = initialMinute ?: currentTime.get(Calendar.MINUTE),
        is24Hour = is24Hour,
    )


    Column {
        MTimePicker(
            state = timePickerState,

        )
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onDismiss) {
                Text("Dismiss")
            }
            Button(onClick = { onConfirm(timePickerState) }) {
                Text("Confirm")
            }
        }
    }
}