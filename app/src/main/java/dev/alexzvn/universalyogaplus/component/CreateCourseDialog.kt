package dev.alexzvn.universalyogaplus.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun CreateCourseDialog(
    onDismiss: () -> Unit = {}
) {
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

                        Text(text = "Create new Course", style = TextStyle(fontSize = 18.sp))
                    }

                    TextButton (onClick = {}) { Text("Save") }
                }

                Column (Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Course Title") },
                        value = "",
                        singleLine = true,
                        onValueChange = { }
                    )

                    Spacer(Modifier.height(15.dp))

                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
//                        OptionSelection(
//                            modifier = Modifier.weight(1f),
//                            label = { Text("Day of week") },
//                            options = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"),
//                            selected = "Monday",
//                            onOptionSelected = {}
//                        )

                        Spacer(Modifier.width(10.dp))

                        OutlinedTextField(
                            modifier = Modifier.weight(1f),
                            label = { Text("Duration") },
                            value = "",
                            singleLine = true,
                            onValueChange = { }
                        )
                    }
                    
                    Spacer(Modifier.height(10.dp))

                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedTextField(
                            modifier = Modifier.weight(1f),
                            label = { Text("Price") },
                            value = "",
                            singleLine = true,
                            onValueChange = { }
                        )

                        Spacer(Modifier.width(10.dp))

                        OutlinedTextField(
                            modifier = Modifier.weight(1f),
                            label = { Text("Capacity") },
                            value = "",
                            singleLine = true,
                            onValueChange = { }
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    var description by remember { mutableStateOf("") }

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 100.dp),
                        label = { Text("Description (optional)") },
                        value = description,
                        singleLine = false,
                        maxLines = 10,
                        onValueChange = { description = it }
                    )
                }
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun CreateCourseDialogPreview() {
    CreateCourseDialog()
}