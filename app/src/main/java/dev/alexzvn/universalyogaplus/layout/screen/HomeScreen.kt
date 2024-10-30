package dev.alexzvn.universalyogaplus.layout.screen


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dev.alexzvn.universalyogaplus.layout.MainLayout
import dev.alexzvn.universalyogaplus.layout.NavigateSection
import dev.alexzvn.universalyogaplus.layout.screen.section.CourseCreateSection
import dev.alexzvn.universalyogaplus.layout.screen.section.CourseEditSection
import dev.alexzvn.universalyogaplus.layout.screen.section.CourseListSection
import dev.alexzvn.universalyogaplus.layout.screen.section.CourseViewSection
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

            composable(
                route = Route.Course.View,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) {
                CourseViewSection(
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