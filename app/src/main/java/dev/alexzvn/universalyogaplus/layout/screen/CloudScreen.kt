package dev.alexzvn.universalyogaplus.layout.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dev.alexzvn.universalyogaplus.R
import dev.alexzvn.universalyogaplus.layout.MainLayout
import dev.alexzvn.universalyogaplus.layout.NavigateSection
import dev.alexzvn.universalyogaplus.layout.screen.section.CloudCourseDetail
import dev.alexzvn.universalyogaplus.layout.screen.section.CloudMainView
import dev.alexzvn.universalyogaplus.util.Route
import dev.alexzvn.universalyogaplus.util.asPainter
import dev.alexzvn.universalyogaplus.util.tryToInt


@Composable
fun CloudScreen(navigation: NavController) {
    val navController = rememberNavController()

    MainLayout(
        section = NavigateSection.Cloud,
        onNavigate = { NavigateSection.handle(it, navigation) }
    ) { padding ->
        NavHost(
            modifier = Modifier.padding(padding),
            navController = navController,
            startDestination = Route.cloud.Home
        ) {
            composable(Route.cloud.Home) {
                CloudMainView(navController)
            }

            composable(
                route = Route.cloud.View,
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) {
                val id = it.arguments?.getString("id")
                CloudCourseDetail(nanoId = id!!, navigation = navController)
            }
        }
    }
}