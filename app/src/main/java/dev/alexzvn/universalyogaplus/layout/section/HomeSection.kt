package dev.alexzvn.universalyogaplus.layout.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.alexzvn.universalyogaplus.component.CourseCard
import dev.alexzvn.universalyogaplus.component.CreateCourseDialog
import dev.alexzvn.universalyogaplus.layout.Home
import dev.alexzvn.universalyogaplus.layout.MainLayout
import dev.alexzvn.universalyogaplus.local.Course
import dev.alexzvn.universalyogaplus.local.CourseType
import dev.alexzvn.universalyogaplus.local.DayOfWeek

@Composable
fun HomeSection(padding: PaddingValues? = null) {
    val padding = when(padding != null) {
        true -> padding
        false -> PaddingValues(0.dp)
    }

    var createDialog by remember { mutableStateOf(false) }


    Column (modifier = Modifier.padding(padding)) {
        Row (
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Course Overview",
                modifier = Modifier.padding(top = 10.dp),
                style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold)
            )

            Button(
                onClick = { createDialog = true },
                contentPadding = PaddingValues(horizontal =  20.dp)
            ) {
                Icon(Icons.Default.Add, "add")
                Spacer(Modifier.width(10.dp))
                Text("Add new")
            }
        }

        LazyColumn {
            val course = Course(
                id = 1,
                title = "Example Yoga Class",
                dayOfWeek = DayOfWeek.MONDAY,
                capacity = 10,
                duration = 60,
                price = 100.0,
                type = CourseType.FLOW_YOGA,
                description = "This is an example yoga class"
            )

            items(8) {
                CourseCard(course)
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }

    if (createDialog) {
        CreateCourseDialog(onDismiss = { createDialog = false })
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeSection() {
    MainLayout(modifier = Modifier.fillMaxSize()) {
        HomeSection()
    }
}