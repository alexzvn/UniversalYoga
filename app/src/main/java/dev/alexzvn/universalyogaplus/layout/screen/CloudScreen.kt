package dev.alexzvn.universalyogaplus.layout.screen

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import dev.alexzvn.universalyogaplus.layout.MainLayout
import dev.alexzvn.universalyogaplus.layout.NavigateSection


@Composable
fun CloudScreen(navigation: NavController) {
    MainLayout(
        section = NavigateSection.Cloud,
        onNavigate = { NavigateSection.handle(it, navigation) }
    ) {

    }
}