package dev.alexzvn.universalyogaplus.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.alexzvn.universalyogaplus.local.CourseType
import dev.alexzvn.universalyogaplus.local.DayOfWeek

data class SearchDialogState(
    val term: String = "",
    val teacher: String = "",
    val dow: DayOfWeek? = null,
    val type: CourseType? = null,
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchDialog(
    state: SearchDialogState = SearchDialogState(),
    onDismiss: () -> Unit = {},
    onSearch: () -> Unit = {},
    onUpdate: (SearchDialogState) -> Unit = {},
) {
    Dialog(
        properties = DialogProperties(
            dismissOnBackPress = true,
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        ),
        onDismissRequest = onDismiss
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(10.dp),
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

                        Text(text = "Search your courses", style = TextStyle(fontSize = 18.sp))
                    }

                    TextButton (onClick = { onUpdate(SearchDialogState()) }) { Text("Reset") }
                }

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Search term") },
                    placeholder = { Text("search by course title, description") },
                    value = state.term,
                    onValueChange = { state.copy(term = it).run(onUpdate) }
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Teacher Name") },
                    placeholder = { Text("Search by teacher's name") },
                    value = state.teacher,
                    onValueChange = { state.copy(teacher = it).run(onUpdate) }
                )

                Spacer(Modifier.height(10.dp))
                Text(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    text = "Day of week",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                HorizontalDivider()

                FlowRow (
                    modifier = Modifier.padding(7.dp),
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    ElevatedFilterChip(
                        selected = state.dow == null,
                        label = { Text("Any") },
                        onClick = { state.copy(dow = null).run(onUpdate) }
                    )

                    for (item in DayOfWeek.entries) {
                        FilterChip(
                            selected = state.dow == item,
                            label = { Text(item.origin) },
                            onClick = { state.copy(dow = item).run(onUpdate) }
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))
                Text(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    text = "Course Type",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                HorizontalDivider()

                FlowRow (
                    modifier = Modifier.padding(7.dp),
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    ElevatedFilterChip(
                        selected = state.type == null,
                        label = { Text("Any") },
                        onClick = { state.copy(type = null).run(onUpdate) }
                    )

                    for (item in CourseType.entries) {
                        FilterChip(
                            selected = state.type == item,
                            label = { Text(item.origin) },
                            onClick = { state.copy(type = item).run(onUpdate) }
                        )
                    }
                }

                HorizontalDivider()
                Spacer(Modifier.height(10.dp))
                FilledTonalButton(
                    onClick = onSearch,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Update Search Result")
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun SearchDialogPreview() {
    var state by remember { mutableStateOf(SearchDialogState()) }

    SearchDialog(
        state = state,
        onUpdate = { state = it }
    )
}