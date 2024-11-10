package dev.alexzvn.universalyogaplus.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.alexzvn.universalyogaplus.R
import dev.alexzvn.universalyogaplus.local.Course
import dev.alexzvn.universalyogaplus.local.CourseType
import dev.alexzvn.universalyogaplus.local.DayOfWeek
import dev.alexzvn.universalyogaplus.util.Scope
import dev.alexzvn.universalyogaplus.util.asPainter
import dev.alexzvn.universalyogaplus.util.sleep
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


@Composable
fun CourseCard(
    modifier: Modifier = Modifier,
    course: Course,
    onClick: (() -> Unit)? = null,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    dropdownContent: @Composable () -> Unit = {}
) {
    ElevatedCard (
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick ?: {},
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
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold),
                text = course.title
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

                IconButton(
                    onClick = { expanded = true }
                ) {
                    Icon(Icons.Default.MoreVert, "more")
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    if (onClick != null) {
                        DropdownMenuItem(
                            text = { Text("Details") },
                            onClick = { close().also { onClick() } },
                            leadingIcon = { Icon(Icons.Outlined.Info, contentDescription = null) }
                        )
                    }

                    if (onEdit != null) {
                        DropdownMenuItem(
                            text = { Text("Edit course") },
                            onClick = { close().also { onEdit() } },
                            leadingIcon = { Icon(Icons.Outlined.Edit, contentDescription = null) }
                        )
                    }

                    if (onDelete != null) {
                        DropdownMenuItem(
                            text = { Text(text = "Delete course", color = Color.Red) },
                            onClick = { close().also { onDelete() } },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) }
                        )
                    }

                    dropdownContent()
                }
            }
        }

        Row (
            modifier = Modifier.padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.alpha(.8f),
                painter = R.drawable.baseline_attach_money_24.asPainter(),
                contentDescription = "Price"
            )
            Text(
                text = "${course.price}",
                modifier = Modifier.alpha(.7f),
                style = TextStyle(fontWeight = FontWeight.Bold)
            )

            Spacer(Modifier.width(10.dp))

            Icon(
                modifier = Modifier
                    .padding(end = 5.dp)
                    .alpha(.8f),
                imageVector = Icons.Default.DateRange,
                contentDescription = "Date"
            )
            Text(
                text = "${course.dayOfWeek.origin}, ${course.duration} min",
                modifier = Modifier.alpha(.7f)
            )

        }

        Spacer(Modifier.height(5.dp))

        Row (
            modifier = Modifier.padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .padding(end = 5.dp)
                    .alpha(.8f),
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Date",
            )

            Text(
                text = "${course.capacity}",
                modifier = Modifier.alpha(.7f)
            )

            Spacer(Modifier.width(10.dp))

            Icon(
                modifier = Modifier
                    .padding(end = 5.dp)
                    .alpha(.8f),
                imageVector = Icons.Default.DateRange,
                contentDescription = "Date"
            )
            Text(
                text = "Start at ${course.parsedStartTime}",
                modifier = Modifier.alpha(.7f)
            )
        }

        course.description?.also {
            Text(
                modifier = Modifier
                    .alpha(.7f)
                    .padding(horizontal = 16.dp)
                    .padding(top = 10.dp),
                style = TextStyle(fontSize = 16.sp),
                text = course.description
            )
            Spacer(Modifier.height(10.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CourseCardPreview() {
    val course = Course(
        id = 1,
        title = "Example Yoga Class",
        dayOfWeek = DayOfWeek.MONDAY,
        capacity = 10,
        duration = 60,
        price = 100.0,
        type = CourseType.FLOW_YOGA,
        startTime = 10,
        // description = "This is an example yoga class. This is an example yoga class"
    )

    Scaffold (modifier = Modifier.fillMaxWidth()) { it
        CourseCard(course = course)
    }
}