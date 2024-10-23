package dev.alexzvn.universalyogaplus.layout.screen

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dev.alexzvn.universalyogaplus.component.CourseCard
import dev.alexzvn.universalyogaplus.component.CreateCourseDialog
import dev.alexzvn.universalyogaplus.layout.MainLayout
import dev.alexzvn.universalyogaplus.layout.NavigateSection
import dev.alexzvn.universalyogaplus.layout.screen.section.CourseCreateSection
import dev.alexzvn.universalyogaplus.layout.screen.section.CourseEditSection
import dev.alexzvn.universalyogaplus.layout.screen.section.CourseListSection
import dev.alexzvn.universalyogaplus.local.Course
import dev.alexzvn.universalyogaplus.local.CourseType
import dev.alexzvn.universalyogaplus.local.DayOfWeek
import dev.alexzvn.universalyogaplus.util.Route

@Composable
fun HomeScreen(
    navigation: NavController
) {
    val navController = rememberNavController()

    MainLayout (
        section = NavigateSection.Home,
        onNavigate = { NavigateSection.handle(it, navigation) },
    ) { padding ->
        NavHost(
            modifier = Modifier.padding(padding),
            navController = navController,
            startDestination = Route.Course.List
        ) {
            composable(Route.Course.List) {
                CourseListSection(navigation = navController)
            }

            composable(Route.Course.Create) {
                CourseCreateSection(navigation = navController)
            }

            composable(
                route = Route.Course.Edit,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) {
                CourseEditSection(
                    id = it.arguments?.getInt("id"),
                    navigation = navController
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeSection() {
    MainLayout(modifier = Modifier.fillMaxSize()) {
        val navController = rememberNavController()
        HomeScreen(navController)
    }
}