package dev.alexzvn.universalyogaplus.layout

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import dev.alexzvn.universalyogaplus.R
import dev.alexzvn.universalyogaplus.ui.theme.UniversalYogaPlusTheme
import dev.alexzvn.universalyogaplus.util.Route
import dev.alexzvn.universalyogaplus.util.asPainter

sealed class NavigateSection() {
    data object Home : NavigateSection()
    data object Cloud : NavigateSection()
    data object Profile : NavigateSection()

    companion object {
        fun handle(section: NavigateSection, navigation: NavController) {
            when (section) {
                is Home -> navigation.navigate(Route.Home)
                is Cloud -> navigation.navigate(Route.Cloud)
                is Profile -> navigation.navigate(Route.Profile)
            }

            Log.d("Navigation", "Navigate to ${section.javaClass.simpleName}")
        }
    }
}


@Composable
fun MainLayout(
    modifier: Modifier = Modifier,
    section: NavigateSection = NavigateSection.Home,
    onNavigate: (NavigateSection) -> Unit = {},
    modal: @Composable () -> Unit = {},
    content: @Composable (padding: PaddingValues) -> Unit
) {
    Scaffold (
        modifier = modifier,

        bottomBar = {
            NavigationBar {
                val updateState: (NavigateSection) -> Unit = { value ->
                    if (section != value) {
                        value.apply(onNavigate)
                    }
                }

                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = section is NavigateSection.Home,
                    onClick = { updateState(NavigateSection.Home) },
                )

                NavigationBarItem(
                    icon = { Icon(R.drawable.baseline_cloud_24.asPainter(), contentDescription = "Home") },
                    label = { Text("Cloud") },
                    selected = section is NavigateSection.Cloud,
                    onClick = { updateState(NavigateSection.Cloud) },
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Person, contentDescription = "Home") },
                    label = { Text("Profile") },
                    selected = section is NavigateSection.Profile,
                    onClick = { updateState(NavigateSection.Profile) },
                )
            }
        }
    ) { padding ->
        content(padding)
    }

    modal()
}


@Preview(showBackground = true)
@Composable
fun MainLayoutPreview() {
    MainLayout(modifier = Modifier.fillMaxSize()) { }
}