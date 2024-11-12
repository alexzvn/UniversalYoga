package dev.alexzvn.universalyogaplus.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.alexzvn.universalyogaplus.local.Schedule
import dev.alexzvn.universalyogaplus.util.asLocalDate
import dev.alexzvn.universalyogaplus.util.format
import dev.alexzvn.universalyogaplus.util.sleep
import dev.alexzvn.universalyogaplus.util.toLocal
import kotlinx.coroutines.launch
import java.util.Date

@Composable
fun ScheduleCard(
    schedule: Schedule,
    modifier: Modifier = Modifier,
    showAction: Boolean = true,
    onClick: () -> Unit = {},
    onDelete: () -> Unit = {},
    onEdit: () -> Unit = {},
    footer: @Composable () -> Unit = {}
) {
    val date = schedule.date.asLocalDate()
    val now = Date().toLocal()

    ElevatedCard(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .padding(10.dp)
                    .weight(1f),
                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
                text = "${schedule.teacher} - ${date.format("dd/MM/yyyy")}".let {
                    when (now.isBefore(date)) {
                        true -> "$it - future"
                        false -> it
                    }
                }
            )

            Box (
                modifier = Modifier.wrapContentSize(Alignment.TopStart)
            ) {
                val scope = rememberCoroutineScope()
                var expanded by remember { mutableStateOf(false) }

                val close = {
                    scope.launch {
                        sleep(200)
                        expanded = false
                    }
                }

                if (showAction) {
                    IconButton(
                        onClick = { expanded = true }
                    ) {
                        Icon(Icons.Default.MoreVert, "more")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
//                        DropdownMenuItem(
//                            text = { Text("Edit schedule") },
//                            onClick = { close().also { onEdit() } },
//                            leadingIcon = { Icon(Icons.Outlined.Edit, contentDescription = null) }
//                        )

                        DropdownMenuItem(
                            text = { Text(text = "Delete schedule", color = Color.Red) },
                            onClick = { close().also { onDelete() } },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) }
                        )
                    }
                }
            }
        }

        if (!schedule.comment.isNullOrEmpty()) {
            Text(
                modifier = Modifier
                    .alpha(.7f)
                    .padding(horizontal = 10.dp),
                style = TextStyle(fontSize = 16.sp),
                text = schedule.comment
            )

            Spacer(Modifier.height(10.dp))
        }

        footer.invoke()
    }
}

@Composable
@Preview(showBackground = true)
fun ScheduleCardPreview() {
    val schedule = Schedule(
        id = 0,
        teacher = "alex",
        comment = "heehehehe ashdashbd hbasbdh",
        date = Date().time,
        courseId = 0,
    )

    ScheduleCard(schedule)
}